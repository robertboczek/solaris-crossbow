
# zonecfg -z zone1
zone1: No such zone configured
Use 'create' to begin configuring a new zone
zonecfg:zone1> create
zonecfg:zone1> set zonepath=/Zones/zone1
zonecfg:zone1> set ip-type=exclusive
zonecfg:zone1> add net
zonecfg:zone1:net> set address=192.168.1.101
zonecfg:zone1:net> set physical=vnic1
zonecfg:zone1:net> end
