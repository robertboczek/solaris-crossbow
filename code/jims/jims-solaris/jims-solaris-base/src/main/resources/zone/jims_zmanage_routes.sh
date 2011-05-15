#!/usr/bin/pfksh

# jims_zmanage_routes.sh
#
# Manages (add, removes) static routes.
#
# Return code:
# 0  on success
# 1  on failure


print_usage_and_exit()
{
	echo "Usage:"
	echo "$0 -z <zone-name> [(-a|-d) <destination>:<gateway>]*"
	exit 1
}


set -- `getopt z:a:d: $*`

if [ $? != 0 ]; then
	print_usage_and_exit
  exit 1
fi


for i in $*; do
  case $i in
   -z) ZONE_NAME=$2; shift 2 ;;
   -a) STDIN="$STDIN route add `echo $2 | sed 's/:/ /g'`\\n"; shift 2 ;;
   -d) STDIN="$STDIN route delete `echo $2 | sed 's/:/ /g'`\\n"; shift 2 ;;
  esac
done

if [ "x$ZONE_NAME" = x ]; then
	echo "No zone specified.\n"
	print_usage_and_exit
fi

STDIN="$STDIN exit\\n"

echo $STDIN | zlogin $ZONE_NAME sh

exit 0

