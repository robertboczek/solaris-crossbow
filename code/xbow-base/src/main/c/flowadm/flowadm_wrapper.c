#include <libdllink.h>
#include <libdladm.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include "defs.h"
#include "flowadm_wrapper.h"
#include "functor.h"
#include "types.h"



dladm_handle_t handle = 0;


int init()
{
	return dladm_open( &handle );
}


int remove_flow( char* flow, int temporary )
{
	return dladm_flow_remove( handle, flow, temporary, "" );
}


void collect_flow_attrs( char* link_name,
                         dladm_flow_attr_t** flow_attrs, int* len )
{
	datalink_id_t link_id;
	dladm_name2info( handle, link_name, &link_id, NULL, NULL, NULL );

	dladm_walk_flow( &count, handle, link_id, len, 0 );

	*flow_attrs = malloc( sizeof( dladm_flow_attr_t ) * ( *len ) );
	dladm_walk_flow( &get_attrs, handle, 1, flow_attrs, 0 );
	*flow_attrs -= *len;
}


char** get_names()
{
	int i, len = 0;
	dladm_flow_attr_t* flow_attrs;
	char** names;

	collect_flow_attrs( "e1000g0", &flow_attrs, &len );

	names = malloc( sizeof( char* ) * ( len + 1 ) );
	names[ len ] = 0;

	for ( i = 0; i < len; ++i )
	{
		names[ i ] = malloc( MAXFLOWNAMELEN );
		memcpy( names[ i ], flow_attrs[ i ].fa_flowname, MAXFLOWNAMELEN );
	}

	free( flow_attrs );

	// TODO-DAWID: what about freeing names?

	return names;
}


int set_property( char* flow, char* key, char* values[], unsigned int values_len, int temporary )
{
	// TODO-DAWID: parse it
	//             handle temporary

	printf( "%d\n",
	dladm_set_flowprop( handle, flow, key, values, values_len, DLADM_OPT_ACTIVE, 0 ) );

	return 0;
}


key_value_pair_t* get_properties( char* flow )
{
	get_props_arg_t arg;
	arg.flow = flow;

	dladm_walk_flowprop( &get_props, flow, &arg );

	// TODO-DAWID: memory leaks!

	key_value_pair_t* res = malloc( sizeof( key_value_pair_t ) );

	res->key = "props";
	res->value = arg.out;

	return res;
}


int create( flow_info_t* flow_info )
{
	dladm_arg_list_t* proplist = NULL;
	dladm_arg_list_t* attrlist = NULL;
	datalink_id_t link_id;

	printf( "%s\n", flow_info->link );
	printf( "%s\n", flow_info->attrs );
	printf( "%s\n", flow_info->props );
	printf( "%d\n", flow_info->temporary );

	dladm_name2info( handle, flow_info->link, &link_id, NULL, NULL, NULL );

	printf( "%d\n",
	dladm_parse_flow_attrs( flow_info->attrs, &attrlist, B_FALSE ) );

	printf( "%d\n",
	dladm_parse_flow_props( flow_info->props, &proplist, B_FALSE ) );

	printf( "%d\n",

	dladm_flow_add( handle,
	                link_id, attrlist, proplist, flow_info->name,
	                flow_info->temporary ? B_TRUE : B_FALSE,
	                NULL /* no root dir */ )

	);

	return 0;
}

