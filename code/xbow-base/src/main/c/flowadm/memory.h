#ifndef FLOWADM_WRAPPER_MEMORY_H
#define FLOWADM_WRAPPER_MEMORY_H

#include "types.h"


key_value_pairs_t* malloc_key_value_pairs( size_t len );
void free_key_value_pairs( key_value_pairs_t* key_value_pairs );

key_value_pair_t* malloc_key_value_pair( size_t key_len, size_t value_len );
void free_key_value_pair( key_value_pair_t* key_value_pair );

flow_infos_t* malloc_flow_infos( size_t len );
void free_flow_infos( flow_infos_t* flow_infos );

flow_info_t* malloc_flow_info();
void free_flow_info( flow_info_t* flow_info );

#endif

