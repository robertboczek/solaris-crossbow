# zfs create rpool/Zones/mplayer

# zonecfg -z mplayer

zonecfg:mplayer> create
zonecfg:mplayer> set zonepath=/rpool/Appliances/mplayer
zonecfg:mplayer> set autoboot=true
zonecfg:mplayer> set ip-type=exclusive
zonecfg:mplayer> verify
zonecfg:mplayer> commit
zonecfg:mplayer> exit

# chmod 700 rpool/Appliances/mplayer
# zoneadm -z mplayer install
