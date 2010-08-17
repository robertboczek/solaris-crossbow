#ifndef LINK_WRAPPER_TYPES_H
#define LINK_WRAPPER_TYPES_H


typedef struct
{
	char* name;
	int up;
}
nic_info_t;


typedef struct
{
	nic_info_t** nic_infos;
	size_t nic_infos_len;
}
nic_infos_t;

#endif

