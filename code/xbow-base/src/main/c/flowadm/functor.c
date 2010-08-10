#include <libdllink.h>

#include <stdlib.h>
#include <string.h>

#include "defs.h"
#include "functor.h"
#include "types.h"



// TODO-DAWID: replace with proper include
#define MAX_PROP_LINE  256

extern dladm_handle_t handle;


/*
 * dladm_walk_flow functors
 */

int count( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
           void* counter )
{
	*( ( int* ) counter ) += 1;
	return DLADM_WALK_CONTINUE;
}


int get_attrs( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
               void* arg )
{
	dladm_flow_attr_t** attrs = arg;

	memcpy( *attrs, flow_attr, sizeof( *flow_attr ) );
	++( *attrs );

	return DLADM_WALK_CONTINUE;
}


/*
 * dladm_walk_flowprop functors
 */

int get_props( void* arg, const char* propname )
{
	char* values[ 10 ];  // TODO-DAWID: no hardcoded numbers!
	uint_t values_len = 10;

	char* flow = ( ( get_props_arg_t* ) arg )->flow;
	key_value_pair_t** it = ( ( get_props_arg_t* ) arg )->key_value_pair_it;

	for ( int i = 0; i < LEN( values ); ++i )
	{
		values[ i ] = malloc( MAX_PROP_LINE );
	}

	dladm_get_flowprop( handle, flow, DLADM_PROP_VAL_CURRENT,
	                    propname, values, &values_len );

	if ( values_len > 0 )
	{
		strcpy( ( *it )->key, propname );

		( ( *it )->value )[ 0 ] = '\0';
		for ( int i = 0; i < values_len; ++i )
		{
			strcat( ( *it )->value, values[ i ] );

			free( values[ i ] );
		}

		++( ( ( get_props_arg_t* ) arg )->key_value_pair_it );
	}

	return DLADM_WALK_CONTINUE;
}

