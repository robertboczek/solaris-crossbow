#include <libdladm.h>
#include <libdlflow.h>

#include <string.h>

#include <common/defs.h>

#include <flow/flowadm_wrapper.h>
#include <flow/memory.h>

#include <mock/cmockery.h>
#include <mock/common.h>
#include <mock/flow.h>
#include <mock/link.h>


static char flow[] = "aflow", key[] = "akey";
static int temporary = 1;


void alloc_info( void** state )
{
	flow_info_t* info = malloc_flow_info();

	info->props->len = info->attrs->len = 0;
	strcpy( info->name, "aflow" );
	strcpy( info->link, "alink" );

	*state = info;
}


void free_info( void** state )
{
	free_flow_info( *state );
}


void test_create_invalid_attributes( void** state )
{
	flow_info_t* info = *state;

	will_return( dladm_parse_flow_attrs, DLADM_STATUS_ATTR_PARSE_ERR );

	assert_int_equal( XBOW_STATUS_ATTR_PARSE_ERR, create( info, 0 ) );
}


void test_create_invalid_properties( void** state )
{
	flow_info_t* info = *state;

	will_return( dladm_parse_flow_attrs, DLADM_STATUS_OK );

	expect_any( dladm_parse_flow_props, str );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_FAILED );

	assert_int_equal( XBOW_STATUS_PROP_PARSE_ERR, create( info, 0 ) );
}


void test_create_invalid_name( void** state )
{
	flow_info_t* info = *state;

	will_return( dladm_parse_flow_attrs, DLADM_STATUS_OK );

	expect_any( dladm_parse_flow_props, str );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_OK );

	expect_string( dladm_name2info, link, info->link );
	will_return( dladm_name2info, ( datalink_id_t[] ){ 13 } );
	will_return( dladm_name2info, ( void* )( DLADM_STATUS_OK + 1 ) );

	assert_int_not_equal( XBOW_STATUS_OK, create( info, 0 ) );
}


void test_create_flow( void** state )
{
	flow_info_t* info = *state;
	datalink_id_t link_id = 13;
	int temporary = 0;

	will_return( dladm_parse_flow_attrs, DLADM_STATUS_OK );

	expect_any( dladm_parse_flow_props, str );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_OK );

	expect_string( dladm_name2info, link, info->link );
	will_return( dladm_name2info, &link_id );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_flow_add, linkid, link_id );
	expect_value( dladm_flow_add, flowname, info->name );
	expect_value( dladm_flow_add, temporary, temporary );
	will_return( dladm_flow_add, DLADM_STATUS_OK );

	assert_int_equal( XBOW_STATUS_OK, create( info, temporary ) );
}


void test_removing_flow( void** state )
{
	expect_string( dladm_flow_remove, flow, flow );
	expect_value( dladm_flow_remove, temporary, temporary );
	will_return( dladm_flow_remove, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == remove_flow( flow, temporary ) );
}


void test_reset_property( void** state )
{
	expect_string( dladm_parse_flow_props, str, key );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_OK );

	expect_string( dladm_set_flowprop, flow, flow );
	expect_string( dladm_set_flowprop, key, key );
	will_return( dladm_set_flowprop, DLADM_STATUS_OK );

	assert_int_equal( XBOW_STATUS_OK, reset_property( flow, key, temporary ) );
}


void test_reset_property_persistently( void** state )
{
	temporary = 0;

	expect_string( dladm_parse_flow_props, str, key );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_OK );

	expect_string( dladm_set_flowprop, flow, flow );
	expect_string( dladm_set_flowprop, key, key );
	will_return( dladm_set_flowprop, DLADM_STATUS_OK );

	assert_int_equal( XBOW_STATUS_OK, reset_property( flow, key, temporary ) );
}


void test_reset_property_invalid_key( void** state )
{
	expect_string( dladm_parse_flow_props, str, key );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, ( int )( DLADM_STATUS_OK + 1 ) );

	assert_int_not_equal( XBOW_STATUS_OK, reset_property( flow, key, temporary ) );
}


void test_set_property( void** state )
{
	char* values[] = { "avalue" };
	char buffer[ strlen( *values ) + sizeof( key ) + 1 ];

	sprintf( buffer, "%s=%s", key, *values );

	expect_string( dladm_parse_flow_props, str, buffer );
	will_return( dladm_parse_flow_props, NULL );
	will_return( dladm_parse_flow_props, DLADM_STATUS_OK );
	
	expect_string( dladm_set_flowprop, flow, flow );
	expect_string( dladm_set_flowprop, key, key );
	will_return( dladm_set_flowprop, DLADM_STATUS_OK );

	assert_int_equal( XBOW_STATUS_OK, set_property( flow, key, values, 1, temporary ) );
}


void test_get_flows_info_with_empty_input( void** state )
{
	char* links[] = { NULL };

	// Check no interaction with *adm functions takes place.

	get_flows_info( links );
}


void test_get_flows_info_all_links( void** state )
{
	int links_len = 0;

	// No links in the system to make the test short.
	
	will_return( dladm_walk_datalink_id, sizeof( links_len ) );
	will_return( dladm_walk_datalink_id, &links_len );
	will_return( dladm_walk_datalink_id, DLADM_STATUS_OK );

	will_return( dladm_walk_datalink_id, 0 );
	will_return( dladm_walk_datalink_id, DLADM_STATUS_OK );

	get_flows_info( NULL );
}


void test_get_flow_info_one_flow( void** state )
{
	char* links[] = { "alink", "onemore", NULL };
	datalink_id_t link_ids[ LEN( links ) - 1 ] = { 13, 15 };
	char* ips[] = { "1.2.3.4", "4.3.2.1" };
	dladm_flow_attr_t attrs[] = { { .fa_linkid = link_ids[ 0 ],
	                                .fa_flowname = "aflow",
	                                .fa_flow_desc = { .fd_mask = FLOW_IP_LOCAL | FLOW_IP_PROTOCOL | FLOW_ULP_PORT_LOCAL,
	                                                  .fd_protocol = IPPROTO_TCP,
	                                                  .fd_local_port = 12 } },

	                              { .fa_linkid = link_ids[ 1 ],
	                                .fa_flowname = "anotherflow",
	                                .fa_flow_desc = { .fd_mask = FLOW_IP_REMOTE | FLOW_IP_DSFIELD | FLOW_ULP_PORT_REMOTE,
	                                                  .fd_dsfield = 1,
	                                                  .fd_dsfield_mask = 5,
	                                                  .fd_remote_port = 15 } } };
	int one = 1;
	int flows_len = LEN( attrs );

	STATIC_CHECK( LEN( links ) - 1 == LEN( link_ids ) );
	STATIC_CHECK( LEN( links ) - 1 == LEN( attrs ) );

	for ( size_t i = 0; i < LEN( links ) - 1; ++i )
	{
		expect_string( dladm_name2info, link, links[ i ] );
		will_return( dladm_name2info, ( void* )( link_ids + i ) );
		will_return( dladm_name2info, DLADM_STATUS_OK );

		expect_value( dladm_walk_flow, link_id, link_ids[ i ] );
		will_return( dladm_walk_flow, sizeof( flows_len ) );  // We want to write to arg.
		will_return( dladm_walk_flow, &flows_len );
		will_return( dladm_walk_flow, DLADM_STATUS_OK );
	}

	// Mocks used by collect_flow_attrs.

	for ( size_t i = 0; i < LEN( links ) - 1; ++i )
	{
		expect_string( dladm_name2info, link, links[ i ] );
		will_return( dladm_name2info, ( void* )( link_ids + i ) );
		will_return( dladm_name2info, DLADM_STATUS_OK );

		expect_value( dladm_walk_flow, link_id, link_ids[ i ] );
		will_return( dladm_walk_flow, sizeof( one ) );
		will_return( dladm_walk_flow, &one );
		will_return( dladm_walk_flow, DLADM_STATUS_OK );

		expect_any( dladm_walk_flow, link_id );
		will_return( dladm_walk_flow, ( void* )( -1 * sizeof( *attrs ) ) );  // This time we write to *arg.
		will_return( dladm_walk_flow, ( attrs + i ) );
		will_return( dladm_walk_flow, DLADM_STATUS_OK );
	}

	// First flow.

	expect_any( dladm_flow_attr_ip2str, attr );
	will_return( dladm_flow_attr_ip2str, strlen( ips[ 0 ] ) );
	will_return( dladm_flow_attr_ip2str, ips[ 0 ] );

	expect_value( dladm_proto2str, protocol, IPPROTO_TCP );
	will_return( dladm_proto2str, "tcp" );

	expect_string( dladm_walk_flowprop, flow, attrs[ 0 ].fa_flowname );
	will_return( dladm_walk_flowprop, 0 );  // Don't write any properties.
	will_return( dladm_walk_flowprop, DLADM_STATUS_OK );

	// Second one.
	
	expect_any( dladm_flow_attr_ip2str, attr );
	will_return( dladm_flow_attr_ip2str, strlen( ips[ 1 ] ) );
	will_return( dladm_flow_attr_ip2str, ips[ 1 ] );

	expect_string( dladm_walk_flowprop, flow, attrs[ 1 ].fa_flowname );
	will_return( dladm_walk_flowprop, 0 );  // Don't write any properties.
	will_return( dladm_walk_flowprop, DLADM_STATUS_OK );

	flow_infos_t* infos = get_flows_info( links );

	// Some simple checks.

	assert_int_equal( LEN( attrs ), infos->len );

	for ( size_t i = 0; i < LEN( attrs ); ++i )
	{
		assert_string_equal( attrs[ i ].fa_flowname, infos->flow_infos[ i ]->name );
		assert_string_equal( links[ i ], infos->flow_infos[ i ]->link );
		assert_int_equal( 0, infos->flow_infos[ i ]->props->len );
	}

	free_flow_infos( infos );
}


void test_init( void** state )
{
	will_return( dladm_open, DLADM_STATUS_OK );
	
	assert_true( XBOW_STATUS_OK == init() );
}


void test_init_failed( void** state )
{
	will_return( dladm_open, DLADM_STATUS_BADARG );

	assert_false( XBOW_STATUS_OK == init() );
}


#if 0
void test_get_properties( void** state )
{
	expect_string( dladm_walk_flowprop, flow, flow );

	key_value_pairs_t* kvps = get_properties( flow );

	free_key_value_pairs( kvps );
}
#endif

