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
	// TODO-DAWID: no hardcoded numbers!

	char* values[ 10 ];
	uint_t values_len = 10;
	int i;

	get_props_arg_t* argg = arg;

	for ( i = 0; i < LEN( values ); ++i )
	{
		values[ i ] = malloc( MAX_PROP_LINE );
	}

	argg->out = malloc( LEN( values ) * MAX_PROP_LINE );
	argg->out[ 0 ] = '\0';

	dladm_get_flowprop( handle, argg->flow, DLADM_PROP_VAL_CURRENT,
	                    propname, values, &values_len );

	for ( i = 0; i < values_len; ++i )
	{
		strcat( argg->out, values[ i ] );
		strcat( argg->out, " " );

		free( values[ i ] );
	}

	argg->out[ strlen( argg->out ) - 1 ] = '\0';

	return DLADM_WALK_CONTINUE;
}

