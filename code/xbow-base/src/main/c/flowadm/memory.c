#include <libdladm.h>

#include <stdlib.h>

#include "memory.h"


void free_key_value_pair( key_value_pair_t* key_value_pair )
{
	free( key_value_pair->key );
	free( key_value_pair->value );
}


flow_infos_t* malloc_flow_infos( size_t len )
{
	flow_infos_t* flow_infos = malloc( sizeof( *flow_infos ) );

	flow_infos->flow_infos = NULL;

	if ( len > 0 )
	{
		flow_infos->flow_infos = malloc( len * sizeof( *( flow_infos->flow_infos ) ) );

		for ( int i = 0; i < len; ++i )
		{
			flow_infos->flow_infos[ i ] = malloc_flow_info();
		}
	}

	flow_infos->flow_infos_len = len;

	return flow_infos;
}


void free_flow_infos( flow_infos_t* flow_infos )
{
	for ( int i = 0; i < flow_infos->flow_infos_len; ++i )
	{
		free_flow_info( flow_infos->flow_infos[ i ] );
	}

	free( flow_infos->flow_infos );
	free( flow_infos );
}


flow_info_t* malloc_flow_info()
{
	flow_info_t* flow_info = malloc( sizeof( *flow_info ) );

	flow_info->name = malloc( MAXFLOWNAMELEN );
	flow_info->link = malloc( MAXLINKNAMELEN );
	flow_info->attrs = malloc( 100 );  // TODO-DAWID: refactor
	flow_info->props = "";  // TODO-DAWID: change

	return flow_info;
}


void free_flow_info( flow_info_t* flow_info )
{
	free( flow_info->name );
	free( flow_info->link );
	free( flow_info->attrs );
	// TODO-DAWID: free props

	free( flow_info );
}

