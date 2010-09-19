#include <libdladm.h>
#include <libdllink.h>
#include <libdlvnic.h>
#include <libdlstat.h>

#include <kstat.h>

#include <link/link.h>
#include <common/defs.h>

#include <test/common.h>


dladm_status_t dladm_name2info(dladm_handle_t handle, const char *vnic,
			    datalink_id_t *link_id, uint32_t *flags, datalink_class_t *link,
			    uint32_t *flag2){

	check_expected( vnic );

	return ( dladm_status_t ) mock();
}

dladm_status_t	dladm_vnic_delete(dladm_handle_t handle, datalink_id_t link_id,
			    uint32_t flags){	

	check_expected( flags );

	return ( dladm_status_t ) mock();

}

boolean_t dladm_valid_linkname(const char *vnic){

	check_expected( vnic );
	
	return ( dladm_status_t ) mock();

}

dladm_status_t dladm_vnic_create(dladm_handle_t handle, const char *vnic,
			    datalink_id_t link, vnic_mac_addr_type_t macaddress, uchar_t *a,
			    uint_t e, int *d, uint_t k, uint16_t h, vrid_t l, int b,
			    datalink_id_t *f, dladm_arg_list_t *g, uint32_t flags){

	check_expected( vnic );
	check_expected( flags );

	return ( dladm_status_t ) mock();

}

dladm_status_t	dladm_walk(dladm_walkcb_t *p, dladm_handle_t handle, void *pointer,
			    datalink_class_t data_link_class, datalink_media_t b, uint32_t flags){

	check_expected( flags );
	check_expected( data_link_class );

	link_names_t* link_names =  (link_names_t*)pointer;
	link_names->array[0] = (char*)malloc(7);
	link_names->array[1] = (char*)malloc(7);

	strcpy(link_names->array[0], "vnic1");
	strcpy(link_names->array[1], "vnic2");
	link_names->array[2] = NULL;

	link_names->number_of_elements = 2;

	return ( dladm_status_t ) mock();

}
/*
dladm_status_t	dladm_get_linkprop(dladm_handle_t handle, datalink_id_t link_id,
			    dladm_prop_type_t a, const char *parameter, char **value, uint_t *b)
{

	check_expected( parameter );	

	if(strcmp(parameter, "priority") == 0){
		strcpy(*value, "high");
	}else if(strcmp(parameter, "mtu") == 0){
		strcpy(*value, "1500");
	}

	return ( dladm_status_t ) mock();

}

kstat_t	*dladm_kstat_lookup(kstat_ctl_t *ctl, const char *link, int a,
			    const char *etherstub, const char *b)
{

	check_expected( etherstub );

	return ( kstat_t* ) mock();

}

void dladm_get_stats(kstat_ctl_t *ctl, kstat_t *t, pktsum_t *stats)
{


	stats->ierrors = 1567;
	stats->ipackets = 2000;
	

}

kstat_ctl_t *kstat_open(void)
{

	return ( kstat_ctl_t* ) mock();

}

int kstat_close(kstat_ctl_t *ctl)
{

	return (int) mock();
	
}

dladm_status_t	dladm_set_linkprop(dladm_handle_t handle, datalink_id_t link,
			    const char *property, char **value, uint_t a, uint_t flags)
{

	check_expected( property );
	check_expected( flags );	

	return ( dladm_status_t ) mock();

}

*/
void test_successful_removing_vnic( void** state )
{
	char vnic[] = "vnic1";
	boolean_t temporary = 0; //persistent
	dladm_status_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	expect_string( dladm_name2info, vnic, vnic );

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

	expect_string( dladm_name2info, vnic, vnic );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_value( dladm_vnic_delete, flags, flags);

	will_return( dladm_vnic_delete, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == delete_vnic( vnic, temporary ) );

}

void test_removing_invalidname_vnic( void** state )
{
	char vnic[] = "invalidname232";
	boolean_t temporary = 0; //persistent

	expect_string( dladm_name2info, vnic, vnic );

	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_true( XBOW_STATUS_INVALID_NAME == delete_vnic( vnic, temporary ) );

}

void test_successful_creating_vnic( void** state )
{
	char vnic[] = "vnic1";
	char parent[] = "e100g0";
	boolean_t temporary = 0; //persistent
	uint32_t flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST;

	expect_string( dladm_name2info, vnic, parent );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_valid_linkname, vnic, vnic );

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

	expect_string( dladm_name2info, vnic, parent );

	will_return( dladm_name2info, DLADM_STATUS_OK );


	expect_string( dladm_valid_linkname, vnic, vnic );

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

	expect_string( dladm_name2info, vnic, parent );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_valid_linkname, vnic, vnic );

	will_return( dladm_valid_linkname, 0 );

	assert_true( XBOW_STATUS_INVALID_NAME == create_vnic( vnic, temporary, parent ) );

}

void test_creating_vnic_with_too_long_name( void** state )
{
	char vnic[] = "vnicvnicvnicnvicnvicnivncvnicvnicvnic";
	boolean_t temporary = 0; //persistent
	char parent[] = "e1000g0";

	expect_string( dladm_name2info, vnic, parent );

	will_return( dladm_name2info, DLADM_STATUS_OK );


	assert_true( XBOW_STATUS_TOO_LONG_NAME == create_vnic( vnic, temporary, parent ) );

}

void test_creating_vnic_with_invalid_parent_link_name( void** state )
{
	char etherstub[] = "vnic1";
	boolean_t temporary = 0; //persistent
	char parent[] = "invalidparentlinkname";

	expect_string( dladm_name2info, vnic, parent );

	will_return( dladm_name2info, DLADM_STATUS_FAILED );


	assert_true( XBOW_STATUS_INVALID_PARENT_NAME == create_vnic( etherstub, temporary, parent ) );

}


void test_getting_link_names( void** state )
{

	int link_type = 0; // i want vnic names
	uint32_t flags = DLADM_OPT_ACTIVE;
	datalink_class_t data_link_class = DATALINK_CLASS_VNIC;

	expect_value( dladm_walk, data_link_class, data_link_class);
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, DLADM_STATUS_OK );

	char **link_names = get_link_names( link_type );
	int i = 0;
	while(link_names[i] != NULL){
		i++;
	}
	assert_int_equal( 2, i ); 

	assert_string_equal( "vnic1", link_names[0] );
	assert_string_equal( "vnic2", link_names[1] );

}

void test_getting_link_names_when_opeartion_fails( void** state )
{
	int link_type = 0; // i want vnic names
	uint32_t flags = DLADM_OPT_ACTIVE;
	datalink_class_t data_link_class = DATALINK_CLASS_VNIC;

	expect_value( dladm_walk, data_link_class, data_link_class);
	expect_value( dladm_walk, flags, flags );

	will_return( dladm_walk, DLADM_STATUS_FAILED );

	char **link_names = get_link_names( link_type );

	assert_string_equal( NULL, link_names );

}
/*
void test_getting_parameter_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char parameter[] = "mtu";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_successful_getting_parameter_value( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "mtu";
	char value[] = "1500";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	

	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_getting_parameter_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "mtu";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	

	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_etherstub_parameter( etherstub, parameter )); 	

}

void test_getting_property_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char parameter[] = "priority";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_property( etherstub, parameter )); 	

}

void test_successful_getting_property_value( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "priority";
	char value[] = "high";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	

	will_return( dladm_get_linkprop, DLADM_STATUS_OK );

	assert_string_equal(value, get_etherstub_property( etherstub, parameter )); 	

}

void test_getting_property_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char parameter[] = "fdsfdff";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_get_linkprop, parameter, parameter );	

	will_return( dladm_get_linkprop, DLADM_STATUS_FAILED );

	assert_string_equal(NULL, get_etherstub_property( etherstub, parameter )); 	

}

void test_getting_statistic_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char statistic[] = "IPACKETS";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_string_equal( NULL, get_etherstub_statistic( etherstub, statistic )); 	

}

void test_successful_getting_statistic_value( void** state )
{

	char etherstub[] = "etherstub";
	char statistic[] = "IPACKETS";
	char value[] = "2000";

	kstat_t *stat = malloc( sizeof(kstat_t) );
	kstat_ctl_t *kcp = malloc( sizeof(kstat_ctl_t) );

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, kcp );	

	expect_string( dladm_kstat_lookup, etherstub, etherstub );

	will_return( dladm_kstat_lookup, stat );

	will_return( kstat_close, 0 );

	assert_string_equal(value, get_etherstub_statistic( etherstub, statistic )); 	

}

void test_getting_statistic_value_when_operation_fails( void** state )
{

	char etherstub[] = "etherstub";
	char statistic[] = "IPACKETS";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	will_return( kstat_open, NULL );

	assert_string_equal(NULL, get_etherstub_statistic( etherstub, statistic )); 	

}

void test_setting_property_value_of_etherstub_with_wrong_name( void** state )
{

	char etherstub[] = "vxcvxcvcx1";
	char property[] = "priority";
	char value[] = "high";

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_BADVAL );

	assert_int_equal( XBOW_STATUS_INVALID_NAME, set_etherstub_property( etherstub, property, value )); 	

}

void test_successful_setting_property_value( void** state )
{

	char etherstub[] = "etherstub";
	char property[] = "priority";
	char value[] = "high";
	uint32_t	flags = DLADM_OPT_ACTIVE | DLADM_OPT_PERSIST | DLADM_OPT_FORCE;

	expect_string( dladm_name2info, etherstub, etherstub );

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

	expect_string( dladm_name2info, etherstub, etherstub );

	will_return( dladm_name2info, DLADM_STATUS_OK );

	expect_string( dladm_set_linkprop, property, property );	
	expect_value( dladm_set_linkprop, flags, flags );

	will_return( dladm_set_linkprop, DLADM_STATUS_FAILED );

	assert_int_equal(XBOW_STATUS_OPERATION_FAILURE, set_etherstub_property( etherstub, property, value )); 	

}

*/
