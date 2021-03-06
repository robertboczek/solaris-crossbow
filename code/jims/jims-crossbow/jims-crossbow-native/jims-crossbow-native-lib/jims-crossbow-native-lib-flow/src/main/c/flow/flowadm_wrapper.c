#include <libdllink.h>
#include <libdladm.h>
#include <libdllink.h>
#include <priv.h>
#include <sys/acctctl.h>

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include <common/defs.h>
#include <common/mappings.h>

#include <flow/aux.h>
#include <flow/defs.h>
#include <flow/flowadm_wrapper.h>
#include <flow/functor.h>
#include <flow/memory.h>
#include <flow/types.h>


dladm_handle_t handle = 0;


int init()
{
	init_mapping();

	return map_status( dladm_open( &handle ) );
}


int create( flow_info_t* flow_info, int temporary )
{
	int rc = DLADM_STATUS_OK;

	dladm_arg_list_t* proplist = NULL;
	dladm_arg_list_t* attrlist = NULL;

	// Parse attributes.
	
	char* flat_attrs = flatten_key_value_pairs( flow_info->attrs );
	rc = dladm_parse_flow_attrs( flat_attrs, &attrlist, B_FALSE );
	free( flat_attrs );

	if ( DLADM_STATUS_OK == rc )
	{
		// Attributes valid. Parse properties.
	
		char* flat_props = flatten_key_value_pairs( flow_info->props );
		rc = dladm_parse_flow_props( flat_props, &proplist, B_FALSE );
		free( flat_props );

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
				                     temporary ? B_TRUE : B_FALSE,
				                     NULL /* no root dir */ );
			}
		}
		else
		{
			rc = DLADM_STATUS_PROP_PARSE_ERR;
		}
	}

	dladm_free_props( proplist );
	dladm_free_props( attrlist );

	return map_status( rc );
}


int remove_flow( char* flow, int temporary )
{
	// Just call dladm_flow_remove directly and returned mapped rc.

	return map_status( dladm_flow_remove(
		handle, flow,
		temporary, "" /* no root dir */
	) );
}


/**
 * \brief Retrieves flows' attributes.
 *
 * Allocates and fills *flow_attrs array with attributes
 * for flows assigned do link_name. *len is filled with flows count.
 *
 * \param  link_name   link name
 * \param  flow_attrs  pointer to array of flow attributes
 *                     the function allocates and fills
 * \param  len         number of elements *flow_attrs has
 *                     after execution of the function
 */
static void collect_flow_attrs( char* link_name,
                                dladm_flow_attr_t** flow_attrs, int* len )
{
	// Get link ID for link_name.

	datalink_id_t link_id;
	dladm_name2info( handle, link_name, &link_id, NULL, NULL, NULL );

	// Count flows on the link.

	*len = 0;
	dladm_walk_flow( &count, handle, link_id, len, 0 );

	// Collect attributes.

	*flow_attrs = malloc( sizeof( **flow_attrs ) * ( *len ) );

	dladm_flow_attr_t* attrs_it = *flow_attrs;
	dladm_walk_flow( &get_attrs, handle, link_id, &attrs_it, 0 );
}


flow_infos_t* get_flows_info( char* link_name[] )
{
	char* links;
	int links_len = 0;

	if ( NULL == link_name )
	{
		dladm_walk_datalink_id( &count_links, handle, &links_len,
		                        DATALINK_CLASS_ALL, DATALINK_ANY_MEDIATYPE,
		                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );

		char* links_it = links = malloc( links_len * MAXLINKNAMELEN );

		dladm_walk_datalink_id( &collect_link_names, handle, &links_it,
		                        DATALINK_CLASS_ALL, DATALINK_ANY_MEDIATYPE,
		                        DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST );
	}
	else
	{
		int i = 0;
		while ( link_name[ i ] != NULL )
		{
			++i;
		}

		links_len = i;

		links = malloc( links_len * MAXLINKNAMELEN );

		for ( i = 0; i < links_len; ++i )
		{
			strcpy( links + i * MAXLINKNAMELEN, link_name[ i ] );
		}
	}

	// Count flows.
	
	int flows_len = 0;
	for ( int j = 0; j < links_len; ++j )
	{
		datalink_id_t link_id;
		dladm_name2info( handle, links + j * MAXLINKNAMELEN, &link_id,
		                 NULL, NULL, NULL );

		dladm_walk_flow( &count, handle, link_id, &flows_len, 0 );
	}

	flow_infos_t* flow_infos = malloc_flow_infos( flows_len );

	int flow_info_it = 0;

	for ( int j = 0; j < links_len; ++j )
	{
		dladm_flow_attr_t* flow_attrs;
		int i, flow_attrs_len;

		collect_flow_attrs( links + j * MAXLINKNAMELEN,
		                    &flow_attrs, &flow_attrs_len );

		for ( i = 0; i < flow_attrs_len; ++i )
		{
			flow_info_t* flow_info = flow_infos->flow_infos[ flow_info_it ];

			strcpy( flow_info->name, flow_attrs[ i ].fa_flowname );
			strcpy( flow_info->link, links + j * MAXLINKNAMELEN );

			// Attributes processing
			
			key_value_pair_t** key_value_pair_it = flow_info->attrs->key_value_pairs;

			// Local IP

			if ( FLOW_IP_LOCAL & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				strcpy( ( *key_value_pair_it )->key, "local_ip" );
				dladm_flow_attr_ip2str( flow_attrs + i,
				                        ( *key_value_pair_it )->value,
				                        INET6_ADDRSTRLEN + 4 );

				++key_value_pair_it;
			}

			// Remote IP

			if ( FLOW_IP_REMOTE & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				strcpy( ( *key_value_pair_it )->key, "remote_ip" );
				dladm_flow_attr_ip2str( flow_attrs + i,
				                        ( *key_value_pair_it )->value,
				                        INET6_ADDRSTRLEN + 4 );

				++key_value_pair_it;
			}

			// Protocol

			if ( FLOW_IP_PROTOCOL & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				strcpy( ( *key_value_pair_it )->key, "transport" );
				strcpy( ( *key_value_pair_it )->value,
				        dladm_proto2str( flow_attrs[ i ].fa_flow_desc.fd_protocol ) );

				++key_value_pair_it;
			}

			// DiffServ field

			if ( FLOW_IP_DSFIELD & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				strcpy( ( *key_value_pair_it )->key, "dsfield" );
				sprintf( ( *key_value_pair_it )->value,
				         "0x%x:0x%x",
				         flow_attrs[ i ].fa_flow_desc.fd_dsfield,
				         flow_attrs[ i ].fa_flow_desc.fd_dsfield_mask );

				++key_value_pair_it;
			}

			// Local port

			if ( FLOW_ULP_PORT_LOCAL & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				// Check, not to lose information while converting fd_local_port.
				STATIC_CHECK( sizeof( in_port_t ) <= sizeof( unsigned int ) );

				strcpy( ( *key_value_pair_it )->key, "local_port" );
				sprintf( ( *key_value_pair_it )->value, "%d",
				         ( unsigned int ) ntohs( flow_attrs[ i ].fa_flow_desc.fd_local_port ) );

				++key_value_pair_it;
			}

			// Remote port

			if ( FLOW_ULP_PORT_REMOTE & flow_attrs[ i ].fa_flow_desc.fd_mask )
			{
				// Check, not to lose information while converting fd_remote_port.
				STATIC_CHECK( sizeof( in_port_t ) <= sizeof( unsigned int ) );

				strcpy( ( *key_value_pair_it )->key, "remote_port" );
				sprintf( ( *key_value_pair_it )->value, "%d",
				         ( unsigned int ) ntohs( flow_attrs[ i ].fa_flow_desc.fd_remote_port ) );

				++key_value_pair_it;
			}

			flow_info->attrs->len = key_value_pair_it
			                        - flow_info->attrs->key_value_pairs;

			// Remember that you can write at most MAXFLOWINFOATTRS
			// (defined in defs.h) key-value pairs for a given flow_info_t!

			// Properties processing

			free_key_value_pairs( flow_info->props );
			flow_info->props = get_properties( flow_attrs[ i ].fa_flowname );

			++flow_info_it;
		}

		free( flow_attrs );
	}

	flow_infos->len = flow_info_it;

	// Clean up.
	
	free( links );

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
		char* propline = malloc( strlen( key ) + strlen( "=" ) + strlen( *values ) + 1 );
		sprintf( propline, "%s=%s", key, *values );
		rc = dladm_parse_flow_props( propline, &proplist, 0 );
		free( propline );
	}
	else
	{
		// Just reset. novalues set to 1.

		rc = dladm_parse_flow_props( key, &proplist, 1 );
	}

	dladm_free_props( proplist );

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

	key_value_pairs->len = arg.key_value_pair_it
	                       - key_value_pairs->key_value_pairs;

	return key_value_pairs;
}


#if 1
int enable_accounting()
{
	priv_set( PRIV_ON, PRIV_EFFECTIVE, PRIV_SYS_DL_CONFIG, NULL );

	printf( "%d\n",
	dladm_start_usagelog( handle, DLADM_LOGTYPE_FLOW, 20 ) );

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
#endif


flow_statistics_t* get_statistics( char* flow, char* stime, char* etime )
{
	flow_statistics_t stats_zero = { 0 };
	flow_statistics_t* stats = malloc_flow_stats();

	// Initialize all fields with zeros.

	*stats = stats_zero;

	// Check if net accounting is activated.

	int state;
	char file[ MAXPATHLEN ] = { 0 };

	if ( ( -1 == acctctl( AC_NET | AC_STATE_GET, &state, sizeof( state ) ) ) || ( AC_ON != state ) )
	{
	
	}
	else
	{
		// Accounting is enabled. Get the filename.
	
		if ( -1 == acctctl( AC_NET | AC_FILE_GET, file, sizeof( file ) ) )
		{
		
		}
		else
		{
			char* end_time = ( 0 == strcmp( "now", etime ) ) ? NULL : etime;

			if ( DLADM_STATUS_OK != dladm_walk_usage_res( &get_usage, DLADM_LOGTYPE_FLOW, file,
			                                              flow, stime, end_time, stats ) )
			{
				*stats = stats_zero;
			}
		}
	}

	return stats;
}

