#ifndef LINK_WRAPPER_MEMORY_H
#define LINK_WRAPPER_MEMORY_H

#include "types.h"


nic_info_t* malloc_nic_info( void );
void free_nic_info( nic_info_t* );

nic_infos_t* malloc_nic_infos( size_t len );
void free_nic_infos( nic_infos_t* nic_infos );

#endif

