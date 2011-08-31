# flowadm show-flow -l e1000g0 - displays all flows assigned to 
	link e1000g0,
# flowadm add-flow -l e1000g0 -a transport=udp udpflow - creates new 
	flow assigned to link e1000g0 for all udp packets.