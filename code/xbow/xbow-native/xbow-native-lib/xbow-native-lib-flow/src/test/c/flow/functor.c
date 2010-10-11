#include <libdladm.h>
#include <libdlflow.h>

#include <string.h>

#include <common/defs.h>
#include <flow/functor.h>
#include <flow/memory.h>
#include <flow/types.h>

#include <mock/cmockery.h>
#include <mock/common.h>
#include <mock/flow.h>
#include <mock/link.h>

#include <test/flow/functor.h>


static dladm_handle_t handle;


void test_count_functor( void** state )
{
	int counter = 0;

	// Count to 3
	
	for ( int i = 0; i < 3; ++i )
	{
		assert_true( DLADM_WALK_CONTINUE == count( handle, NULL, &counter ) );
		assert_int_equal( i+1, counter );
	}
}


void test_get_attrs_functor( void** state )
{
	dladm_flow_attr_t flow_attrs[] = { { .fa_flowname = "flow0", .fa_mask = 13 },
	                                   { .fa_nattr = 123, .fa_linkid = 2 } };
	dladm_flow_attr_t buffer[ LEN( flow_attrs ) ];
	dladm_flow_attr_t* it = buffer;

	for ( int i = 0; i < LEN( buffer ); ++i )
	{
		assert_true( DLADM_WALK_CONTINUE == get_attrs( handle, flow_attrs + i, &it ) );
		assert_int_equal( i+1, it - buffer );
		assert_int_equal( 0, memcmp( flow_attrs + i, buffer + i, sizeof( *buffer ) ) );
	}
}


void test_get_props_functor( void** state )
{
	key_value_pairs_t* kvps = malloc_key_value_pairs( 20 );
	get_props_arg_t arg = { .flow = "aflow",
                          .key_value_pair_it = kvps->key_value_pairs };
	const char* propnames[] = { "prop0", "prop1" };
	const char* values[] = { "val0", NULL };

	for ( int i = 0; i < LEN( propnames ); ++i )
	{
		expect_string( dladm_get_flowprop, propname, propnames[ i ] );
		expect_string( dladm_get_flowprop, flow, arg.flow );

		will_return( dladm_get_flowprop, values );
		will_return( dladm_get_flowprop, ( size_t[] ){ LEN( values ) - 1 } );
		will_return( dladm_get_flowprop, DLADM_STATUS_OK );

		assert_true( DLADM_WALK_CONTINUE == get_props( &arg, propnames[ i ] ) );
		assert_string_equal( propnames[ i ], kvps->key_value_pairs[ i ]->key );
		assert_string_equal( values[ 0 ], kvps->key_value_pairs[ i ]->value );

		assert_int_equal( i+1, arg.key_value_pair_it - kvps->key_value_pairs );
	}

	free_key_value_pairs( kvps );
}


void test_get_props_functor_no_props( void** state )
{
	key_value_pairs_t* kvps = malloc_key_value_pairs( 20 );
	get_props_arg_t arg = { .flow = "aflow",
                          .key_value_pair_it = kvps->key_value_pairs };
	const char* propname = "prop0";
	const char* values[] = { NULL };

	expect_string( dladm_get_flowprop, propname, propname );
	expect_string( dladm_get_flowprop, flow, arg.flow );

	will_return( dladm_get_flowprop, values );
	will_return( dladm_get_flowprop, ( size_t[] ){ LEN( values ) - 1 } );
	will_return( dladm_get_flowprop, DLADM_STATUS_OK );

	assert_true( DLADM_WALK_CONTINUE == get_props( &arg, propname ) );
	assert_true( kvps->key_value_pairs == arg.key_value_pair_it );

	free_key_value_pairs( kvps );
}


void test_collect_link_names_functor( void** state )
{
	const char* link_names[] = { "link0", "link1" };
	datalink_id_t link_ids[ LEN( link_names ) ] = { 0, 1 };
	char buffer[ LEN( link_names ) ][ MAXLINKNAMELEN ];
	char* it = *buffer;

	for ( int i = 0; i < LEN( link_names ); ++i )
	{
		will_return( dladm_datalink_id2info, link_names[ i ] );
		will_return( dladm_datalink_id2info, DLADM_STATUS_OK );

		expect_value( dladm_datalink_id2info, id, link_ids[ i ] );

		assert_true( DLADM_WALK_CONTINUE == collect_link_names( handle, link_ids[ i ], &it ) );
		assert_int_equal( ( i+1 ) * MAXLINKNAMELEN, it - *buffer );
		assert_string_equal( link_names[ i ], buffer[ i ] );
	}
}


void test_count_links_functor( void** state )
{
	int counter = 0;
	
	// Count to 3
	
	for ( int i = 0; i < 3; ++i )
	{
		assert_true( DLADM_WALK_CONTINUE == count_links( handle, NULL, &counter ) );
		assert_int_equal( i+1, counter );
	}
}

