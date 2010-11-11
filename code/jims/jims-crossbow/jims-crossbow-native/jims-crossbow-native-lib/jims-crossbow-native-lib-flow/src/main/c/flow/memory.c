#include <libdladm.h>

#include <stdlib.h>

#include <common/defs.h>

#include <flow/defs.h>
#include <flow/memory.h>


key_value_pairs_t* malloc_key_value_pairs( size_t len )
{
	key_value_pairs_t* key_value_pairs = malloc( sizeof( *key_value_pairs ) );

	key_value_pairs->key_value_pairs = malloc( ( len + 1 ) * sizeof( *( key_value_pairs->key_value_pairs ) ) );

	for ( int i = 0; i < len; ++i )
	{
		key_value_pairs->key_value_pairs[ i ] = malloc_key_value_pair(
			MAXKEYSIZE,
			MAXVALSIZE
		);
	}
	key_value_pairs->key_value_pairs[ len ] = NULL;

	return key_value_pairs;
}


void free_key_value_pairs( key_value_pairs_t* key_value_pairs )
{
	for ( key_value_pair_t** key_value_pair_it = key_value_pairs->key_value_pairs;
	      *key_value_pair_it != NULL;
	      ++key_value_pair_it )
	{
		free_key_value_pair( *key_value_pair_it );
	}

	free( key_value_pairs->key_value_pairs );
	free( key_value_pairs );
}


key_value_pair_t* malloc_key_value_pair( size_t key_len, size_t value_len )
{
	key_value_pair_t* key_value_pair = malloc( sizeof( *key_value_pair ) );

	key_value_pair->key = malloc( key_len );
	key_value_pair->value = malloc( value_len );

	return key_value_pair;
}


void free_key_value_pair( key_value_pair_t* key_value_pair )
{
	free( key_value_pair->key );
	free( key_value_pair->value );
}


flow_infos_t* malloc_flow_infos( size_t len )
{
	flow_infos_t* flow_infos = malloc( sizeof( *flow_infos ) );

	flow_infos->flow_infos = malloc( ( len + 1 ) * sizeof( *( flow_infos->flow_infos ) ) );

	for ( int i = 0; i < len; ++i )
	{
		flow_infos->flow_infos[ i ] = malloc_flow_info();
	}
	flow_infos->flow_infos[ len ] = NULL;

	return flow_infos;
}


void free_flow_infos( flow_infos_t* flow_infos )
{
	for ( flow_info_t** flow_info_it = flow_infos->flow_infos;
	      *flow_info_it != NULL;
	      ++flow_info_it )
	{
		free_flow_info( *flow_info_it );
	}

	free( flow_infos->flow_infos );
	free( flow_infos );
}


flow_info_t* malloc_flow_info( void )
{
	flow_info_t* flow_info = malloc( sizeof( *flow_info ) );

	flow_info->name = malloc( MAXFLOWNAMELEN );
	flow_info->link = malloc( MAXLINKNAMELEN );
	flow_info->attrs = malloc_key_value_pairs( MAXFLOWINFOATTRS );
	flow_info->props = malloc_key_value_pairs( MAXFLOWINFOPROPS );

	return flow_info;
}


void free_flow_info( flow_info_t* flow_info )
{
	free( flow_info->name );
	free( flow_info->link );
	free_key_value_pairs( flow_info->attrs );
	free_key_value_pairs( flow_info->props );

	free( flow_info );
}

