#include <libdladm.h>

#include <string.h>

#include <common/defs.h>

#include <flow/flowadm_wrapper.h>
#include <flow/memory.h>

#include <test/common.h>
#include <test/mock.h>


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
	will_return( dladm_parse_flow_props, DLADM_STATUS_FAILED );

	assert_int_equal( XBOW_STATUS_PROP_PARSE_ERR, create( info, 0 ) );
}


void test_create_invalid_name( void** state )
{
	flow_info_t* info = *state;

	will_return( dladm_parse_flow_attrs, DLADM_STATUS_OK );
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
	char flow[] = "flow";
	boolean_t temporary = 1; 

	expect_string( dladm_flow_remove, flow, flow );
	expect_value( dladm_flow_remove, temporary, temporary );
	will_return( dladm_flow_remove, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == remove_flow( flow, temporary ) );
}

