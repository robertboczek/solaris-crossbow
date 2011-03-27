#!/usr/bin/pfksh

# jims_zattach_ifaces.sh
#
# Attaches specified interface(s) to a zone. Sets ip-type to exclusive.
#
# Return code:
# 0  on success
# 1  on failure


print_usage_and_exit()
{
	echo "Usage:"
	echo "$0 -z <zone-name> (-i <interface-name>)+"
	exit 1
}


set -- `getopt z:i: $*`

if [ $? != 0 ]; then
	print_usage_and_exit
fi


for i in $*; do
	case $i in
		-z) ZONE_NAME=$2; shift 2; GOT_ZONE=yes ;;
		-i) IFACES="$IFACES $2"; shift 2; GOT_IFACE=yes ;;
	esac
done

if [ "x${GOT_ZONE}x" = "xx" ]; then
	echo "No zone specified.\n"
	print_usage_and_exit
elif [ "x${GOT_IFACE}x" = "xx" ]; then
	echo "No interface(s) specified.\n"
	print_usage_and_exit
fi


STDIN="set ip-type=exclusive\\n"

for IFACE in $IFACES; do
	STDIN="$STDIN add net\\n"
	STDIN="$STDIN set physical=$IFACE\\n"
	STDIN="$STDIN end\\n"
done

STDIN="$STDIN verify\\n"
STDIN="$STDIN commit\\n"
STDIN="$STDIN exit\\n"

echo "$STDIN" | zonecfg -z $ZONE_NAME

exit 0

