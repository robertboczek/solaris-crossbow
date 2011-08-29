# dladm create-vnic vnic1 -l e1000g0 - creates new VNIC vnic1 over 
	existing NIC e1000g0,
# dladm create-etherstub ether00 - creates new Etherstub ether00,
# dladm show-linkprop vnic11 - lists all properties assigned to vnic11 
	link,
# dladm set-linkprop -pmaxbw=1000 vnic11 - assignes 1Mbps bandwith limit 
	to vnic11 link,
# dladm set-linkprop -ppriority=low vnic11 - assignes low priority to 
	vnic11 link.