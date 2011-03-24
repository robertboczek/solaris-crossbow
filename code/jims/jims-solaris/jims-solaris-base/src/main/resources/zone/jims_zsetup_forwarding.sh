#!/usr/bin/pfksh

# jims_zsetup_forwarding.sh
#
# Enables or disables IP forwarding for specified zone.
#
# Return code:
# 0  on success
# 1  on failure


print_usage_and_exit()
{
	echo "Usage:"
	echo "$0 -z <zone-name> up|down"
	exit 1
}


set -- `getopt z: $*`

if [ $? != 0 ]; then
	print_usage_and_exit
  exit 1
fi


for i in $*; do
  case $i in
   -z)         ZONE_NAME=$2; shift 2 ;;
   @(up|down)) ACTION=$i; shift 1 ;;
  esac
done

if [ "x$ZONE_NAME" = x ]; then
	echo "No zone specified.\n"
	print_usage_and_exit
elif [ "x$ACTION" = x ]; then
	echo "No action specified.\n"
	print_usage_and_exit
fi

if [ "$ACTION" = up ]; then
	STDIN="routeadm -e ipv4-forwarding\\n"
else
	STDIN="routeadm -d ipv4-forwarding\\n"
fi

STDIN="$STDIN routeadm -u\\n"
STDIN="$STDIN exit\\n"

echo $STDIN | zlogin $ZONE_NAME sh

exit 0
