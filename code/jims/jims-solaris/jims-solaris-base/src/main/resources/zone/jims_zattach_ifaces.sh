#!/usr/bin/pfksh


set -- `getopt z:i: $*`

if [ $? != 0 ]; then
	exit 13
fi


for i in $*; do
  case $i in
   -z) ZONE_NAME=$2; shift 2;;
   -i) IFACES="$IFACES $2"; shift 2;;
  esac
done


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

