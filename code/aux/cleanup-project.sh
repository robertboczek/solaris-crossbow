#!/bin/sh


PRO=$1
REGEXP="^$PRO\.\."

for E in `zoneadm list -i | egrep "$REGEXP"`; do
	echo "Removing $E"
	zoneadm -z $E halt
	yes | zoneadm -z $E uninstall -F
done

for E in `zoneadm list -c | egrep "$REGEXP"`; do
	yes | zonecfg -z $E delete -F
done

for E in `flowadm show-flow -p -o flow | egrep "$REGEXP"`; do
	echo "Removing $E"
	flowadm remove-flow $E
done

for E in `dladm show-vnic -p -o link | egrep "$REGEXP"`; do
	echo "Removing $E"
	dladm delete-vnic $E
done

for E in `dladm show-etherstub -p | egrep "$REGEXP"`; do
	echo "Removing $E"
	dladm delete-etherstub $E
done

