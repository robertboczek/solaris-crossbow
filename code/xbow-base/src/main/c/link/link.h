#ifndef LINK_WRAPPER_H
#define LINK_WRAPPER_H

#include "types.h"


int init();


nic_info_t* get_nic_info( char* name );


nic_infos_t* get_nic_infos( void );

#endif

