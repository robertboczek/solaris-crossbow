#include <libdllink.h>

#include <stdlib.h>

#include "memory.h"
#include "types.h"


nic_info_t* malloc_nic_info( void )
{
	nic_info_t* nic_info = malloc( sizeof( *nic_info ) );

	nic_info->name = malloc( MAXLINKNAMELEN );

	return nic_info;
}


void free_nic_info( nic_info_t* nic_info )
{
	free( nic_info->name );
	free( nic_info );
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


void free_nic_infos( nic_infos_t* nic_infos )
{
	for ( nic_info_t** it = nic_infos->nic_infos; *it != NULL; ++it )
	{
		free_nic_info( *it );
	}

	free( nic_infos->nic_infos );
	free( nic_infos );
}

