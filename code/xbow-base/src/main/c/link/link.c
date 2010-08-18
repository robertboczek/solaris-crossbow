#include <kstat.h>
#include <libdllink.h>

#include <stdlib.h>
#include <string.h>

#include "functor.h"
#include "link.h"
#include "memory.h"
#include "types.h"


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

	// NIC state
	
	// Assume the NIC is down (or unknown).
	
	nic_info->up = 0;

	kstat_ctl_t* kcp = kstat_open();
	if ( kcp != NULL )
	{
		kstat_t* ksp = kstat_lookup( kcp, "link", 0, name );
		if ( ksp != NULL )
		{
			if ( kstat_read( kcp, ksp, NULL ) != -1 )
			{
				link_state_t link_state;

				// TODO-DAWID: rewrite dladm_kstat_value?
				if ( dladm_kstat_value( ksp, "link_state", KSTAT_DATA_UINT32, &link_state ) >= 0 )
				{
					nic_info->up = ( LINK_STATE_UP == link_state );
				}
				else
				{
					// TODO-DAWID: error
				}
			}
			else
			{
				// TODO-DAWID: error
			}
		}
		else
		{
			// TODO-DAWID: error
		}

		kstat_close( kcp );
	}
	else
	{
		// TODO-DAWID: error here
	}

	return nic_info;
}


nic_infos_t* get_nic_infos( void )
{
	int nic_count = 0;

	// Count NICs.

	dladm_walk_datalink_id( &count, handle, &nic_count,
	                        DATALINK_CLASS_PHYS, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	// Allocate and fill nic_infos structure.

	nic_infos_t* nic_infos = malloc_nic_infos( nic_count );

	nic_info_t** nic_infos_it = nic_infos->nic_infos;

	dladm_walk_datalink_id( &collect_nic_info, handle, &nic_infos_it,
	                        DATALINK_CLASS_PHYS, DATALINK_ANY_MEDIATYPE,
	                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

	nic_infos->nic_infos_len = nic_infos_it - nic_infos->nic_infos;

	return nic_infos;
}

