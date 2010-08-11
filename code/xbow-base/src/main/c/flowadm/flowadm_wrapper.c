#include <libdllink.h>
#include <libdladm.h>
#include <libdllink.h>
#include <priv.h>

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "defs.h"
#include "flowadm_wrapper.h"
#include "functor.h"
#include "memory.h"
#include "types.h"


dladm_handle_t handle = 0;


int init()
{
	return dladm_open( &handle );
}


int create( flow_info_t* flow_info )
{
	int rc = DLADM_STATUS_OK;

	dladm_arg_list_t* proplist = NULL;
	dladm_arg_list_t* attrlist = NULL;

	// Parse attributes.
	
	rc = dladm_parse_flow_attrs( flow_info->attrs, &attrlist, B_FALSE );

	if ( DLADM_STATUS_OK == rc )
	{
		// Attributes valid. Parse properties.
	
		rc = dladm_parse_flow_props( flow_info->props, &proplist, B_FALSE );

		if ( DLADM_STATUS_OK == rc )
		{
			// Properties valid. Retrieve link name.

			datalink_id_t link_id;
			rc = dladm_name2info( handle, flow_info->link, &link_id, NULL, NULL, NULL );

			if ( DLADM_STATUS_OK == rc )
			{
				// Try to create the flow.

				rc = dladm_flow_add( handle,
				                     link_id, attrlist, proplist, flow_info->name,
				                     flow_info->temporary ? B_TRUE : B_FALSE,
				                     NULL /* no root dir */ );
			}
		}
	}

	return map_status( rc );
}


int remove_flow( char* flow, int temporary )
{
	return map_status( dladm_flow_remove( handle, flow, temporary, "" ) );
}


void collect_flow_attrs( char* link_name,
                         dladm_flow_attr_t** flow_attrs, int* len )
{
	// Get link ID for link_name.

	datalink_id_t link_id;
	dladm_name2info( handle, link_name, &link_id, NULL, NULL, NULL );

	// Count flows on the link.

	*len = 0;
	dladm_walk_flow( &count, handle, link_id, len, 0 );

	// Collect attributes.

	*flow_attrs = malloc( sizeof( dladm_flow_attr_t ) * ( *len ) );
	dladm_walk_flow( &get_attrs, handle, link_id, flow_attrs, 0 );
	*flow_attrs -= *len;
}


int collect_link_names( dladm_handle_t handle,
                        datalink_id_t link_id, void* arg )
{
	char** it = arg;

	dladm_datalink_id2info( handle, link_id, NULL, NULL, NULL,
	                        *it, MAXLINKNAMELEN );

	*it += MAXLINKNAMELEN;

	return DLADM_WALK_CONTINUE;
}


flow_infos_t* get_flows_info( char* link_name[] )
{
	char links[ 10 ][ MAXLINKNAMELEN ];
	int links_len;

	if ( NULL == link_name )
	{
		char* links_it = links[ 0 ];

		dladm_walk_datalink_id( &collect_link_names, handle, &links_it,
		                        DATALINK_CLASS_ALL, DATALINK_ANY_MEDIATYPE,
		                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

		links_len = ( links_it - links[ 0 ] ) / sizeof( *links );
	}
	else
	{
		int i = 0;
		while ( link_name[ i ] != NULL )
		{
			strcpy( links[ i ], link_name[ i ] );

			++i;
		}

		links_len = i;
	}

	flow_infos_t* flow_infos = malloc_flow_infos( 100 );

	int flow_info_it = 0;

	for ( int j = 0; j < links_len; ++j )
	{
		dladm_flow_attr_t* flow_attrs;
		int i, flow_attrs_len;

		collect_flow_attrs( links[ j ], &flow_attrs, &flow_attrs_len );

		for ( i = 0; i < flow_attrs_len; ++i )
		{
			flow_info_t* flow_info = flow_infos->flow_infos[ flow_info_it ];

			strcpy( flow_info->name, flow_attrs[ i ].fa_flowname );

			strcpy( flow_info->link, links[ j ] );

			// Attributes processing
			
			flow_info->attrs[ 0 ] = '\0';

			// Local IP

			if ( flow_attrs[ i ].fa_flow_desc.fd_mask & FLOW_IP_LOCAL )
			{
				strcat( flow_info->attrs, "local_ip=" );
				dladm_flow_attr_ip2str( flow_attrs + i,
																flow_info->attrs + strlen( flow_info->attrs ),
				                        INET6_ADDRSTRLEN + 4 );
			}

			// Remote IP

			if ( flow_attrs[ i ].fa_flow_desc.fd_mask & FLOW_IP_REMOTE )
			{
				strcat( flow_info->attrs, "remote_ip=" );
				dladm_flow_attr_ip2str( flow_attrs + i,
																flow_info->attrs + strlen( flow_info->attrs ),
				                        INET6_ADDRSTRLEN + 4 );
			}

			// Protocol

			if ( flow_attrs[ i ].fa_flow_desc.fd_mask & FLOW_IP_PROTOCOL )
			{
				strcat( flow_info->attrs, "protocol=" );
				strcat( flow_info->attrs,
								dladm_proto2str( flow_attrs[ i ].fa_flow_desc.fd_protocol ) );
			}

			// TODO-DAWID: all attrs

			// flow_info[ flow_info_it ].props = "";
			flow_info->temporary = 0;

			++flow_info_it;
		}

		free( flow_attrs );
	}

	flow_infos->flow_infos_len = flow_info_it;

	return flow_infos;
}


int set_property( char* flow,
                  char* key, char* values[], unsigned int values_len,
                  int temporary )
{
	int rc = DLADM_STATUS_OK;
	dladm_arg_list_t* proplist = NULL;

	// Parse input.

	if ( values != NULL )
	{
		char propline[ 1000 ];  // TODO-DAWID: refactor!
		sprintf( propline, "%s=%s", key, *values );
		rc = dladm_parse_flow_props( propline, &proplist, 1 );
	}
	else
	{
		rc = dladm_parse_flow_props( key, &proplist, 0 );
	}

	if ( DLADM_STATUS_OK == rc )
	{
		int persist_opt = ( temporary ? DLADM_OPT_ACTIVE : DLADM_OPT_PERSIST );

		// Set the property.

		rc = dladm_set_flowprop( handle, flow, key, values, values_len, persist_opt, 0 );
	}

	return map_status( rc );
}


int reset_property( char* flow, char* key, int temporary )
{
	return set_property( flow, key, NULL, 0, temporary );
}


key_value_pairs_t* get_properties( char* flow )
{
	key_value_pairs_t* key_value_pairs = malloc_key_value_pairs( MAXFLOWPROPERTIESLEN );

	get_props_arg_t arg = {
		.flow = flow,
		.key_value_pair_it = key_value_pairs->key_value_pairs
	};

	dladm_walk_flowprop( &get_props, flow, &arg );

	key_value_pairs->key_value_pairs_len = arg.key_value_pair_it
	                                       - key_value_pairs->key_value_pairs;

	return key_value_pairs;
}


int enable_accounting()
{
	priv_set( PRIV_ON, PRIV_EFFECTIVE, PRIV_SYS_DL_CONFIG, NULL );

	printf( "%d\n",
	dladm_start_usagelog( handle,
	                      DLADM_LOGTYPE_FLOW, 20 ) );

	priv_set( PRIV_OFF, PRIV_EFFECTIVE, PRIV_SYS_DL_CONFIG, NULL );

	return 0;
}


int disable_accounting()
{
	priv_set( PRIV_ON, PRIV_EFFECTIVE, PRIV_SYS_DL_CONFIG, NULL );

	printf( "%d\n",
	dladm_stop_usagelog( handle, DLADM_LOGTYPE_FLOW ) );

	priv_set( PRIV_OFF, PRIV_EFFECTIVE, PRIV_SYS_DL_CONFIG, NULL );

	return 0;
}

