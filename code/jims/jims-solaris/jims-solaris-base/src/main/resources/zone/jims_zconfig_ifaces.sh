#!/usr/bin/pfksh

# jims_zconfig_ifaces.sh
#
# Plumbs, sets ip/netmask and brings interfaces up.
#
# Return code:
# 0  on success
# 1  on failure


print_usage_and_exit()
{
	echo "Usage:"
	echo "$0 -z <zone-name> (-c <interface-name>:<ip-address>/<netmask>)+"
	exit 1
}


set -- `getopt z:c: $*`

if [ $? != 0 ]; then
	print_usage_and_exit
  exit 1
fi


for i in $*; do
  case $i in
   -z) ZONE_NAME=$2; shift 2; GOT_ZONE=yes ;;
   -c) CFGS="$CFGS $2"; shift 2; GOT_CONFIG=yes ;;
  esac
done

if [ "x${GOT_ZONE}x" = "xx" ]; then
	echo "No zone specified.\n"
	print_usage_and_exit
elif [ "x${GOT_CONFIG}x" = "xx" ]; then
	echo "No config(s) specified.\n"
	print_usage_and_exit
fi


for IFACE in $CFGS; do
  STDIN="$STDIN ifconfig `echo $IFACE | cut -d: -f1` plumb\\n"
done

for LINE in $CFGS; do
  STDIN="$STDIN ifconfig `echo $LINE | sed -e 's/:/ /'` up\\n"
done

STDIN="$STDIN exit\\n"

echo $STDIN | zlogin $ZONE_NAME sh

exit 0

