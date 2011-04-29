#ifndef VLAN_WRAPPER_TYPES_H
#define VLAN_WRAPPER_TYPES_H


typedef struct
{
	char* name;
	char* link;
	int tag;
}
vlan_info_t;


typedef struct
{
	vlan_info_t** vlan_infos;
	size_t len;
}
vlan_infos_t;


#endif  // VLAN_WRAPPER_TYPES_H

