#!/bin/sh

zoneadm -z COMM..FIRST halt
zoneadm -z COMM..FIRST uninstall -F
zonecfg -z COMM..FIRST delete -F

zoneadm -z COMM..SECOND halt
zoneadm -z COMM..SECOND uninstall -F
zonecfg -z COMM..SECOND delete -F

flowadm remove-flow COMM..IFACE0..TEST.POLICY

dladm delete-vnic COMM..FIRST..IFACE0
dladm delete-vnic COMM..SECOND..IFACE0

dladm delete-etherstub COMM..SWITCH13


