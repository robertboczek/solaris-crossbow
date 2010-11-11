#include <libdladm.h>
#include <libdllink.h>
#include <libdlvnic.h>
#include <libdlstat.h>

#include <kstat.h>

#include <string.h>

#include <etherstub/etherstub.h>
#include <common/defs.h>

#include <mock/cmockery.h>
#include <mock/kstat.h>
#include <mock/link.h>


void test_successful_removing_etherstub( void** state )
{
	char etherstub[] = "etherstub1";
	boolean_t temporary = 0; //persistent
	dladm_status_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_vnic_delete, flags, flags);

	will_return( dladm_vnic_delete, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == delete_etherstub( etherstub, temporary ) );

}

void test_temporal_removing_etherstub( void** state )
{
	char etherstub[] = "etherstub1";
	boolean_t temporary = 1; //temporal
	dladm_status_t flags = DLADM_OPT_ACTIVE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_vnic_delete, flags, flags);

	will_return( dladm_vnic_delete, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == delete_etherstub( etherstub, temporary ) );

}

void test_removing_invalidname_etherstub( void** state )
{
	char etherstub[] = "invalidname232";
	boolean_t temporary = 0; //persistent
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_true( XBOW_STATUS_INVALID_NAME == delete_etherstub( etherstub, temporary ) );

}

void test_successful_creating_etherstub( void** state )
{
	char etherstub[] = "etherstub1";
	boolean_t temporary = 0; //persistent
	dladm_status_t flags = DLADM_OPT_ANCHOR | DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	expect_string( dladm_valid_linkname, link, etherstub );

	will_return( dladm_valid_linkname, 1 );

	expect_value( dladm_vnic_create, flags, flags );
	expect_string( dladm_vnic_create, vnic, etherstub );

	will_return( dladm_vnic_create, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == create_etherstub( etherstub, temporary ) );

}

void test_temporal_creating_etherstub( void** state )
{
	char etherstub[] = "etherstub1";
	boolean_t temporary = 1; //temporal
	dladm_status_t flags = DLADM_OPT_ANCHOR | DLADM_OPT_ACTIVE;

	expect_string( dladm_valid_linkname, link, etherstub );

	will_return( dladm_valid_linkname, 1 );

	expect_value( dladm_vnic_create, flags, flags);
	expect_string( dladm_vnic_create, vnic, etherstub );

	will_return( dladm_vnic_create, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == create_etherstub( etherstub, temporary ) );

}

void test_temporal_creating_etherstub_with_invalid_name( void** state )
{
	char etherstub[] = "invalidname1";
	boolean_t temporary = 1; //temporal

	expect_string( dladm_valid_linkname, link, etherstub );

	will_return( dladm_valid_linkname, 0 );

	assert_true( XBOW_STATUS_INVALID_NAME == create_etherstub( etherstub, temporary ) );

}

void test_creating_etherstub_with_too_long_name( void** state )
{
	char etherstub[] = "ethertstubethertstubethertstubethertstubethertstub";
	boolean_t temporary = 0; //persistent

	assert_true( XBOW_STATUS_TOO_LONG_NAME == create_etherstub( etherstub, temporary ) );

}

void test_getting_etherstub_names( void** state )
{
	uint32_t flags = DLADM_OPT_ACTIVE;
	char* etherstubs[] = { "ether1", "ether2" };
	etherstub_names_t names = { .number_of_elements = LEN( etherstubs ) };

	names.array = malloc( sizeof( *( names.array ) ) * ( LEN( etherstubs ) + 1 ) );

	for ( int i = 0; i < LEN( etherstubs ); ++i )
	{
		names.array[ i ] = malloc( strlen( etherstubs[ i ] ) + 1 );
		strcpy( names.array[ i ], etherstubs[ i ] );
	}

	names.array[ LEN( etherstubs ) ] = NULL;

	expect_any( dladm_walk, data_link_class );
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, sizeof( names ) );
	will_return( dladm_walk, &names );
	will_return( dladm_walk, DLADM_STATUS_OK );

	char **etherstub_names = get_etherstub_names();

	int i;
	for ( i = 0; NULL != etherstub_names[ i ]; ++i )
	{
		assert_string_equal( etherstubs[ i ], etherstub_names[ i ] );
	}

	assert_int_equal( LEN( etherstubs ), i );
}

void test_getting_etherstub_names_when_opeartion_fails( void** state )
{
	uint32_t flags = DLADM_OPT_ACTIVE;

	expect_any( dladm_walk, data_link_class );
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, 0 );
	will_return( dladm_walk, DLADM_STATUS_FAILED );

	char **etherstub_names = get_etherstub_names();

	assert_string_equal( NULL, etherstub_names );
}

void test_getting_parameter_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char parameter[] = "mtu";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );

	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_successful_getting_parameter_value( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "mtu";
	char value[] = "1500";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, value );
	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_getting_parameter_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "mtu";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, "" );
	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_getting_property_value_of_etherstub_with_wrong_name( void** state )
{
	char etherstub[] = "vxcvxcvcx1";
	char parameter[] = "priority";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_property( etherstub, parameter )); 	
}

void test_successful_getting_property_value( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "priority";
	char value[] = "high";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, value );
	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_etherstub_property( etherstub, parameter )); 	

}

void test_getting_property_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "fdsfdff";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, "" );
	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_etherstub_property( etherstub, parameter )); 	

}

void test_getting_statistic_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char statistic[] = "IPACKETS";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_statistic( etherstub, statistic )); 	

}

void test_successful_getting_statistic_value( void** state )
{
	char etherstub[] = "etherstub";
	char statistic[] = "IPACKETS";
	char value[] = "2000";
	datalink_id_t linkid;
	kstat_t stat;
	pktsum_t stats = { .ierrors = 1567, .ipackets = 2000 };
	kstat_ctl_t kcp;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, &kcp );	

	expect_string( dladm_kstat_lookup, vnic, etherstub );
	will_return( dladm_kstat_lookup, &stat );

	will_return( dladm_get_stats, &stats );

	will_return( kstat_close, 0 );

	assert_string_equal(value, get_etherstub_statistic( etherstub, statistic )); 	
}

void test_getting_statistic_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char statistic[] = "IPACKETS";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, NULL );

	assert_string_equal(NULL, get_etherstub_statistic( etherstub, statistic )); 	

}

void test_setting_property_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char property[] = "priority";
	char value[] = "high";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_int_equal( XBOW_STATUS_INVALID_NAME, set_etherstub_property( etherstub, property, value )); 	

}

void test_successful_setting_property_value( void** state )
{

	char etherstub[] = "etherstub";
	char property[] = "priority";
	char value[] = "high";
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_set_linkprop, property, property );
	expect_value( dladm_set_linkprop, flags, flags );	

	will_return( dladm_set_linkprop, DLADM_STATUS_OK );

	assert_int_equal(XBOW_STATUS_OK, set_etherstub_property( etherstub, property, value )); 	

}

void test_setting_property_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char property[] = "fdsfdff";
	char value[] = "high";
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, etherstub );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_set_linkprop, property, property );	
	expect_value( dladm_set_linkprop, flags, flags );

	will_return( dladm_set_linkprop, DLADM_STATUS_FAILED );

	assert_int_equal(XBOW_STATUS_OPERATION_FAILURE, set_etherstub_property( etherstub, property, value )); 	

}


