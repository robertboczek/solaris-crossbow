#include <libdllink.h>

#include <stdlib.h>

dladm_handle_t handle = 0;


int init()
{
	// TODO-DAWID: initialize mappings here!
	// return map_status( dladm_open( &handle ) );
	return dladm_open( &handle );
}


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


nic_info_t* malloc_nic_info( void )
{
	nic_info_t* nic_info = malloc( sizeof( *nic_info ) );

	nic_info->name = malloc( MAXLINKNAMELEN );

	return nic_info;
}


nic_infos_t* malloc_nic_infos( size_t len )
{
	nic_infos_t* nic_infos = malloc( sizeof( *nic_infos ) );

	nic_infos->nic_infos = malloc( ( len + 1 ) * sizeof( *( nic_infos->nic_infos ) ) );

	for ( int i = 0; i < len; ++i )
	{
		nic_infos->nic_infos[ i ] = malloc_nic_info();
	}
	nic_infos->nic_infos[ len ] = NULL;

	return nic_infos;
}


int collect_nic_info( dladm_handle_t handle,
                       datalink_id_t link_id, void* arg )
{
	nic_info_t*** nic_infos_it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        ( **nic_infos_it )->name, MAXLINKNAMELEN );

	++( *nic_infos_it );

	return DLADM_WALK_CONTINUE;
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

