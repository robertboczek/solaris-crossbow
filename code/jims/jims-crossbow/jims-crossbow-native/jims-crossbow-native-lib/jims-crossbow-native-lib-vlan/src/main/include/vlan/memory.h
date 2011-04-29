#ifndef VLAN_WRAPPER_MEMORY_H
#define VLAN_WRAPPER_MEMORY_H

#include "types.h"


vlan_infos_t* malloc_vlan_infos( size_t len );
void free_vlan_infos( vlan_infos_t* vlan_infos );

vlan_info_t* malloc_vlan_info();
void free_vlan_info( vlan_info_t* vlan_info );

#endif

