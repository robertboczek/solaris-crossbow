#ifndef LINK_LINKADM_TEST_H
#define LINK_LINKADM_TEST_H

void test_successful_removing_vnic( void** state );

void test_temporal_removing_vnic( void** state );

void test_removing_invalidname_vnic( void** state );

void test_successful_creating_vnic( void** state );

void test_temporal_creating_vnic( void** state );

void test_temporal_creating_vnic_with_invalid_name( void** state );

void test_creating_vnic_with_too_long_name( void** state );

void test_creating_vnic_with_invalid_parent_link_name( void** state );

void test_getting_link_names( void** state );

void test_getting_link_names_when_opeartion_fails( void** state );
/*
void test_successful_getting_parameter_value( void** state );

void test_getting_parameter_value_of_etherstub_with_wrong_name( void** state );

void test_getting_parameter_value_when_operation_fails( void** state );

void test_successful_getting_property_value( void** state );

void test_getting_property_value_of_etherstub_with_wrong_name( void** state );

void test_getting_property_value_when_operation_fails( void** state );

void test_getting_statistic_value_when_operation_fails( void** state );

void test_successful_getting_statistic_value( void** state );

void test_getting_statistic_value_of_etherstub_with_wrong_name( void** state );

void test_setting_property_value_when_operation_fails( void** state );

void test_successful_setting_property_value( void** state );

void test_setting_property_value_of_etherstub_with_wrong_name( void** state );*/

#endif

