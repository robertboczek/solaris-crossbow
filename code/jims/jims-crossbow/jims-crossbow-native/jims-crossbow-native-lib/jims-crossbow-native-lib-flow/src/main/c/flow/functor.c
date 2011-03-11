#include <libdllink.h>

#include <stdlib.h>
#include <string.h>

#include <common/defs.h>

#include <flow/defs.h>
#include <flow/functor.h>
#include <flow/types.h>


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

	memcpy( *attrs, flow_attr, sizeof( **attrs ) );
	++( *attrs );

	return DLADM_WALK_CONTINUE;
}


/*
 * dladm_walk_flowprop functors
 */

int get_props( void* arg, const char* propname )
{
	char* values[ DLADM_MAX_PROP_VALCNT ];
	uint_t values_len = DLADM_MAX_PROP_VALCNT;

	char* flow = ( ( get_props_arg_t* ) arg )->flow;
	key_value_pair_t** it = ( ( get_props_arg_t* ) arg )->key_value_pair_it;

	for ( int i = 0; i < LEN( values ); ++i )
	{
		values[ i ] = malloc( DLADM_STRSIZE );
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

  
/*
 * dladm_walk_datalink_id functors
 */

int collect_link_names( dladm_handle_t handle,
                        datalink_id_t link_id, void* arg )
{
	char** it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        *it, MAXLINKNAMELEN );

	*it += MAXLINKNAMELEN;

	return DLADM_WALK_CONTINUE;
}


int count_links( dladm_handle_t handle,
                 datalink_id_t link_id, void* counter )
{
	*( ( int* ) counter ) += 1;
	return DLADM_WALK_CONTINUE;
}


/*
 * dladm_walk_usage_res functors
 */

int get_usage( dladm_usage_t* usage, void* arg )
{
	flow_statistics_t* stats = arg;

	#if 0
	stats->ipackets += usage->du_ipackets;
	stats->opackets += usage->du_opackets;
	#endif

	stats->rbytes   += usage->du_rbytes;
	stats->obytes   += usage->du_obytes;

	return DLADM_STATUS_OK;
}

