#include <libdllink.h>

#include <stdlib.h>
#include <string.h>

#include "types.h"
#include "link.h"
#include "memory.h"


dladm_handle_t handle = 0;


int init()
{
	// TODO-DAWID: initialize mappings here!
	// return map_status( dladm_open( &handle ) );
	return dladm_open( &handle );
}


int collect_nic_info( dladm_handle_t handle,
                      datalink_id_t link_id, void* arg )
{
	char name[ MAXLINKNAMELEN ];
	nic_info_t*** nic_infos_it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        name, sizeof( name ) );

	free_nic_info( **nic_infos_it );
	**nic_infos_it = get_nic_info( name );

	++( *nic_infos_it );

	return DLADM_WALK_CONTINUE;
}


nic_info_t* get_nic_info( char* name )
{
	nic_info_t* nic_info = malloc_nic_info();

	strcpy( nic_info->name, name );

	return nic_info;
}


nic_infos_t* get_nic_infos( void )
{
	nic_infos_t* nic_infos = malloc_nic_infos( 20 );  // TODO-DAWID: count

	nic_info_t** nic_infos_it = nic_infos->nic_infos;

	dladm_walk_datalink_id( &collect_nic_info, handle, &nic_infos_it,
	                        DATALINK_CLASS_PHYS, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	nic_infos->nic_infos_len = nic_infos_it - nic_infos->nic_infos;

	return nic_infos;
}

