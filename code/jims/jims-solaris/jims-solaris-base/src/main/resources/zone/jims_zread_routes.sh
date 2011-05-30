#!/usr/bin/pfksh

# jims_zmanage_routes.sh
#
# Reads entries from zone's routing table.
#
# Return code:
# 0  on success
# 1  on failure


print_usage_and_exit()
{
	echo "Usage:"
	echo "$0 -z <zone-name>"
	exit 1
}


set -- `getopt z: $*`

if [ $? != 0 ]; then
	print_usage_and_exit
  exit 1
fi


for i in $*; do
  case $i in
   -z) ZONE_NAME=$2; shift 2 ;;
  esac
done

if [ "x$ZONE_NAME" = x ]; then
	echo "No zone specified.\n"
	print_usage_and_exit
fi

STDIN="true"

STDIN="$STDIN && netstat -nrf inet | gawk --posix -- 'BEGIN { FS = \"[[:blank:]]+\" } /([0-9]{1,3}\.){3}[0-9]{1,3}/ { print $1 \":\" $2 }'"
STDIN="$STDIN && exit"

echo $STDIN | zlogin $ZONE_NAME sh

