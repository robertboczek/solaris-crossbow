#include <libdladm.h>
#include <libdllink.h>
#include <libdlvnic.h>

#include <stdlib.h>
#include <string.h>

#include <link/link.h>
#include <common/defs.h>

#include <mock/cmockery.h>
#include <mock/kstat.h>
#include <mock/link.h>


void test_successful_removing_vnic( void** state )
{
	char vnic[] = "vnic1";
	boolean_t temporary = 0; //persistent
	dladm_status_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_vnic_delete, flags, flags);

	will_return( dladm_vnic_delete, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == delete_vnic( vnic, temporary ) );

}

void test_temporal_removing_vnic( void** state )
{
	char vnic[] = "vnic1";
	boolean_t temporary = 1; //temporal
	dladm_status_t flags = DLADM_OPT_ACTIVE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_vnic_delete, flags, flags);

	will_return( dladm_vnic_delete, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == delete_vnic( vnic, temporary ) );

}

void test_removing_invalidname_vnic( void** state )
{
	char vnic[] = "invalidname232";
	boolean_t temporary = 0; //persistent
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_true( XBOW_STATUS_INVALID_NAME == delete_vnic( vnic, temporary ) );

}

void test_successful_creating_vnic( void** state )
{
	char vnic[] = "vnic1";
	char parent[] = "e100g0";
	boolean_t temporary = 0; //persistent
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, parent );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_valid_linkname, link, vnic );

	will_return( dladm_valid_linkname, 1 );

	expect_value( dladm_vnic_create, flags, flags );
	expect_string( dladm_vnic_create, vnic, vnic );

	will_return( dladm_vnic_create, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == create_vnic( vnic, temporary, parent ) );

}

void test_temporal_creating_vnic( void** state )
{
	char vnic[] = "vnic1";
	char parent[] = "e1000g0";
	boolean_t temporary = 1; //temporal
	uint32_t flags = DLADM_OPT_ACTIVE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, parent );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );


	expect_string( dladm_valid_linkname, link, vnic );

	will_return( dladm_valid_linkname, 1 );

	expect_value( dladm_vnic_create, flags, flags );
	expect_string( dladm_vnic_create, vnic, vnic );

	will_return( dladm_vnic_create, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == create_vnic( vnic, temporary, parent ) );

}

void test_temporal_creating_vnic_with_invalid_name( void** state )
{
	char vnic[] = "invalidname1";
	char parent[] = "e100g0";
	boolean_t temporary = 1; //temporal
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, parent );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_valid_linkname, link, vnic );

	will_return( dladm_valid_linkname, 0 );

	assert_true( XBOW_STATUS_INVALID_NAME == create_vnic( vnic, temporary, parent ) );

}

void test_creating_vnic_with_too_long_name( void** state )
{
	char vnic[] = "vnicvnicvnicnvicnvicnivncvnicvnicvnic";
	boolean_t temporary = 0; //persistent
	char parent[] = "e1000g0";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, parent );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );


	assert_true( XBOW_STATUS_TOO_LONG_NAME == create_vnic( vnic, temporary, parent ) );

}

void test_creating_vnic_with_invalid_parent_link_name( void** state )
{
	char vnic[] = "vnic1";
	boolean_t temporary = 0; //persistent
	char parent[] = "invalidparentlinkname";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, parent );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_FAILED );


	assert_true( XBOW_STATUS_INVALID_PARENT_NAME == create_vnic( vnic, temporary, parent ) );

}


void test_getting_link_names( void** state )
{
	int link_type = 0; // i want vnic names
	uint32_t flags = DLADM_OPT_ACTIVE;
	datalink_class_t data_link_class = DATALINK_CLASS_VNIC;
	char* links[] = { "vnic1", "vnic2" };

	link_names_t link_names = { .number_of_elements = LEN( links ) };
	
	link_names.array = malloc( sizeof( *link_names.array ) * ( LEN( links ) + 1 ) );

	for ( int i = 0; i < LEN( links ); ++i )
	{
		link_names.array[ i ] = malloc( strlen( links[ i ] ) + 1 );
		strcpy( link_names.array[ i ], links[ i ] );
	}
	link_names.array[ LEN( links ) ] = NULL;

	expect_value( dladm_walk, data_link_class, data_link_class );
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, sizeof( link_names ) );
	will_return( dladm_walk, &link_names );
	will_return( dladm_walk, DLADM_STATUS_OK );

	char** names = get_link_names( link_type );

	int i;
	for ( i = 0; NULL != names[ i ]; ++i )
	{
		assert_string_equal( links[ i ], names[ i ] );
	}

	assert_int_equal( LEN( links ), i ); 
}

void test_getting_link_names_when_opeartion_fails( void** state )
{
	int link_type = 0; // i want vnic names
	uint32_t flags = DLADM_OPT_ACTIVE;
	datalink_class_t data_link_class = DATALINK_CLASS_VNIC;

	expect_value( dladm_walk, data_link_class, data_link_class);
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, 0 );
	will_return( dladm_walk, DLADM_STATUS_FAILED );

	char **link_names = get_link_names( link_type );

	assert_string_equal( NULL, link_names );
}

void test_getting_parameter_value_of_link_with_wrong_name( void** state )
{

	char vnic[] = "vxcvxcvcx1";
	char parameter[] = "mtu";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_link_parameter( vnic, parameter )); 	

}

void test_successful_getting_parameter_value( void** state )
{
	char vnic[] = "vnic1";
	char parameter[] = "mtu";
	char value[] = "1500";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, value );
	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_link_parameter( vnic, parameter )); 	
}

void test_getting_parameter_value_when_operation_fails( void** state )
{

	char vnic[] = "vnic1";
	char parameter[] = "mtu";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, "1500" );
	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_link_parameter( vnic, parameter )); 	

}

void test_getting_property_value_of_link_with_wrong_name( void** state )
{

	char vnic[] = "vxcvxcvcx1";
	char parameter[] = "priority";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_link_property( vnic, parameter )); 	

}

void test_successful_getting_property_value( void** state )
{

	char vnic[] = "vnic";
	char parameter[] = "priority";
	char value[] = "high";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, value );
	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_link_property( vnic, parameter )); 	

}

void test_getting_property_value_when_operation_fails( void** state )
{

	char vnic[] = "vnic";
	char parameter[] = "fdsfdff";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	
	will_return( dladm_get_linkprop, "" );
	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_link_property( vnic, parameter )); 	

}

void test_getting_statistic_value_of_link_with_wrong_name( void** state )
{

	char vnic[] = "vxcvxcvcx1";
	char statistic[] = "IPACKETS";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_link_statistic( vnic, statistic )); 	

}

void test_successful_getting_statistic_value( void** state )
{
	char vnic[] = "vnic";
	char statistic[] = "IPACKETS";
	char value[] = "2000";
	datalink_id_t linkid;
	kstat_t stat;
	kstat_ctl_t kcp;
	pktsum_t stats = { .ierrors = 1567, .ipackets = 2000 };

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, &kcp );	

	expect_string( dladm_kstat_lookup, vnic, vnic );

	will_return( dladm_kstat_lookup, &stat );

	will_return( dladm_get_stats, &stats );

	will_return( kstat_close, 0 );

	assert_string_equal(value, get_link_statistic( vnic, statistic )); 	
}

void test_getting_statistic_value_when_operation_fails( void** state )
{

	char vnic[] = "vnic";
	char statistic[] = "IPACKETS";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, NULL );

	assert_string_equal(NULL, get_link_statistic( vnic, statistic )); 	

}

void test_setting_property_value_of_link_with_wrong_name( void** state )
{

	char vnic[] = "vxcvxcvcx1";
	char property[] = "priority";
	char value[] = "high";
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_int_equal( XBOW_STATUS_INVALID_NAME, set_link_property( vnic, property, value )); 	

}

void test_successful_setting_property_value( void** state )
{

	char vnic[] = "vnic";
	char property[] = "priority";
	char value[] = "high";
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_set_linkprop, property, property );
	expect_value( dladm_set_linkprop, flags, flags );	

	will_return( dladm_set_linkprop, DLADM_STATUS_OK );

	assert_int_equal(XBOW_STATUS_OK, set_link_property( vnic, property, value )); 	

}

void test_setting_property_value_when_operation_fails( void** state )
{

	char vnic[] = "vnic";
	char property[] = "fdsfdff";
	char value[] = "high";
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;
	datalink_id_t linkid;

	expect_string( dladm_name2info, link, vnic );
	will_return( dladm_name2info, &linkid );
	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_set_linkprop, property, property );	
	expect_value( dladm_set_linkprop, flags, flags );

	will_return( dladm_set_linkprop, DLADM_STATUS_FAILED );

	assert_int_equal(XBOW_STATUS_OPERATION_FAILURE, set_link_property( vnic, property, value )); 	

}

