#!/usr/bin/pfksh


set -- `getopt z:c: $*`

if [ $? != 0 ]; then
  exit 13
fi


for i in $*; do
  case $i in
   -z) ZONE_NAME=$2; shift 2;;
   -c) LINES="$LINES $2"; shift 2;;
  esac
done


for IFACE in $LINES; do
  STDIN="$STDIN ifconfig `echo $IFACE | cut -d: -f1` plumb\\n"
done

for LINE in $LINES; do
  STDIN="$STDIN ifconfig `echo $LINE | sed -e 's/:/ /'` up\\n"
done

STDIN="$STDIN exit\\n"

echo $STDIN | zlogin $ZONE_NAME sh

exit 0

