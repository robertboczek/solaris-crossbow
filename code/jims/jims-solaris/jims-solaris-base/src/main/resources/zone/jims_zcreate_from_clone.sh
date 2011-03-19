#!/usr/bin/pfksh
#
# jims_zcreate_from_clone.sh 
#
# jims_zcreate_from_clone.sh modifies cloned zone configuration 
# 
#
# status return
#   0 -- good
#   1 -- error, bad params,
#	        could not create zone,
#		conflict with similar zone name already in use, 
#	        zone already exits and is active,
#		or could not find IP address for the new zone.
#

set -x 

DEBUG=${DEBUG:-0}
DEBUG_TIME=${DEBUG_TIME:-0}

print_usage_and_exit()
{
  echo "Usage: $0 -z <zone_name> -l <destination_zpool> -s <source_zfs_snapshot> [-n <address,physical>]+ [-a <autoboot>] [-m <cpu_pool_name>] [-k <name=value>]*"
  exit 1
}

## TO DO: remove rctl?
function zone_create_from_zfs_snapshot
{
	# ZFS create and receive from snapshot
	ZFS_ZONE_FS=$ZONE_ZPOOL/${ZONE_NAME}

	/usr/sbin/zfs receive $ZFS_ZONE_FS < $ZONE_SRC_SNAPSHOT.SNAP
	/usr/sbin/zfs receive $ZFS_ZONE_FS/ROOT < $ZONE_SRC_SNAPSHOT.ROOT.SNAP
	/usr/sbin/zfs receive $ZFS_ZONE_FS/ROOT/zbe < $ZONE_SRC_SNAPSHOT.ROOT.zbe.SNAP

	typeset zfile=/tmp/.zone_create.$$
	typeset ret=0;

	# Mounted ZFS with imported zone's dir structure
	ZONE_PATH=`zfs get -H -o value mountpoint $ZFS_ZONE_FS`
	
	zfs set mountpoint=legacy $ZFS_ZONE_FS/ROOT
	zfs set mountpoint=legacy $ZFS_ZONE_FS/ROOT/zbe


cat > ${zfile} <<_EOF
create
set zonepath=$ZONE_PATH 
# set autoboot=$ZONE_AUTOBOOT
# remove net
_EOF

if [ -n "$ZONE_CPU_POOL" ] ; then
 echo "set pool=$ZONE_CPU_POOL" >> ${zfile}
fi
if [ "$ZONE_NETS" ] ; then
  echo "$ZONE_NETS" | awk 'BEGIN {RS=" "; FS=","} {if (length($0)!=0) {printf "add net\nset address=%s\nset physical=%s\nend\n", $1, $2 }}' >> ${zfile}
fi
if [ "$ZONE_RCTLS" ] ; then
  ZONE_RCTLS="$ZONE_RCTLS "
  echo "$ZONE_RCTLS" | awk ' BEGIN {RS=" "} {if (length($0)>1) {name=substr($0, 0, index($0,"=") - 1); value=substr($0, index($0,"=") + 1);printf "add rctl\nset name=%s\nadd value %s\nend\n", name, value; }}' >> ${zfile}
fi

cat >> ${zfile} <<_EOF
verify
commit
exit
_EOF

	# create the zone
	typeset zonecmd="/usr/sbin/zonecfg -z ${ZONE_NAME} -f ${zfile}"

	if [[ ${DEBUG} -gt 0 ]]; then
		echo ${zonecmd}
		echo "zone config file : ${zfile}"
		cat ${zfile}
	fi

	${zonecmd}
	ret=$?;

	# rm -f ${zfile}

	if [ $ret != 0 ]; then
	    echo "jims_zcreate_from_clone: could not create $ZONE_NAME "
	    exit 1;
	fi

	mount -F zfs $ZFS_ZONE_FS/ROOT/zbe $ZONE_PATH/root

	echo "Attaching zone ..."
	/usr/sbin/zoneadm -z ${ZONE_NAME} attach
	echo $?
	echo "Zone attached."

	return 0		
}

function zone_perform_installation_tasks
{
	ZONE_ROOT=${ZONE_PATH}/root

	cp /etc/hosts $ZONE_ROOT/etc

        typeset znodename=/tmp/.zone_nodename.$$
        echo ${ZONE_NAME} > ${znodename}
        cp ${znodename} $ZONE_ROOT/etc/nodename
	rm -f ${znodename}
	
	return 0
}

####################### MAIN ##########################

echo "jims_zcreate_from_clone.sh: $*"

CMD_FULL="${0}"

## Parse command line

set -- `getopt z:l:s:n:a:k:m: $*`

if [ $? != 0 ] ; then
  print_usage_and_exit
fi

for i in $*; do
  case $i in
   -z) HOST_NAME=$2; shift 2;;
   -a) ZONE_AUTOBOOT=$2; shift 2;;
   -n) ZONE_NETS=" $ZONE_NETS $2"; shift 2;;
   -k) ZONE_RCTLS=" $ZONE_RCTLS $2" shift 2;;
   -l) ZONE_ZPOOL=$2; shift 2;;
   -s) ZONE_SRC_SNAPSHOT=$2; shift 2;; 
   -m) ZONE_CPU_POOL=$2; shift 2;;
   \?) print_usage_and_exit;;
  esac
done
 
#
## Validate mandatory arguments
#
if [ -z "$HOST_NAME" -o -z "$ZONE_ZPOOL" -o -z "$ZONE_SRC_SNAPSHOT" ] ; then
  print_usage_and_exit
fi

ZONE_AUTOBOOT=${ZONE_AUTOBOOT:-false}

DIR_NAME=$(/usr/bin/dirname $CMD_FULL)
ZONE_NAME=$($DIR_NAME/jims_zmatch.sh $HOST_NAME)
if [ $? != 0 ]; then
  echo "$ZONE_NAME"
  exit 1
fi

#
# Check if for a currently active zone.
# No overinstall for active zones.
#

ZONE_STATE="none"
ZONE_STATUS=`/usr/sbin/zoneadm -z $ZONE_NAME list -p 2>/dev/null`

if [ $? -eq 0 ]; then
    ZONE_STATE=$(echo "$ZONE_STATUS" |  nawk -F: '{print $3}')

    if [ $ZONE_STATE == "ready" ] ||
       [ $ZONE_STATE == "running" ] ||
       [ $ZONE_STATE == "shutting_down" ]; then
	echo "jims_zcreate_from_clone: $ZONE_NAME exists and is an active zone. "
	echo "jims_zcreate_from_clone: cannot install $ZONE_NAME. "
	exit 1
    fi
fi

#
# If zone state is "incomplete", cleanup via uninstall before continuing
#

if [ $ZONE_STATE == "incomplete" ]; then
    echo "jims_zcreate_from_clone: $ZONE_NAME exists but is incomplete. "
    echo "Removing incomplete zone. "

    /usr/sbin/zoneadm -z ${ZONE_NAME} uninstall -F
    if [ $? -ne 0 ]; then
        exit 1
    fi
    ZONE_PATH=$(/usr/sbin/zonecfg -z $ZONE_NAME info zonepath | \
			nawk '{print $2}')
    /usr/bin/rmdir $ZONE_PATH 

    echo "Incomplete zone removed. "

    ZONE_STATUS=`/usr/sbin/zoneadm -z $ZONE_NAME list -p 2>/dev/null`
    if [ $? -eq 0 ]; then
        ZONE_STATE=$(echo "$ZONE_STATUS" |  nawk -F: '{print $3}')
    else
	ZONE_STATE="none"
    fi
fi

#
# Check for an existing (but inactive) installed zone.
# If found, set ZONE_PATH to its actual location.
#

if [ $ZONE_STATE == "down" ] ||
   [ $ZONE_STATE == "installed" ]; then
    ZONE_PATH=$(/usr/sbin/zonecfg -z $ZONE_NAME info zonepath | \
			nawk '{print $2}')
    echo "jims_zcreate_from_clone: $ZONE_NAME exists and is an inactive zone. "
    echo "zone $ZONE_NAME path is $ZONE_PATH "
fi

echo "jims_zcreate_from_clone: Creating zone ... "
zone_create_from_zfs_snapshot

echo "jims_zcreate_from_clone: Done. Perform after installation tasks ... "
zone_perform_installation_tasks

echo "jims_zcreate_from_clone: Finished."
