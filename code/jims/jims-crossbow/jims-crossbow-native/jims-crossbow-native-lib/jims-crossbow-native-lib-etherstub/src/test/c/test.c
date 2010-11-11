#include <common/mappings.h>

#include <mock/cmockery.h>

#include <test/etherstub/etherstub_wrapper.h>

int main( int argc, char** argv )
{
	const UnitTest tests[] =
	{
		unit_test( test_successful_removing_etherstub ),
		unit_test( test_removing_invalidname_etherstub ),
		unit_test( test_temporal_removing_etherstub ),
		unit_test( test_successful_creating_etherstub ),
		unit_test( test_temporal_creating_etherstub ),
		unit_test( test_temporal_creating_etherstub_with_invalid_name ),
		unit_test( test_creating_etherstub_with_too_long_name ),
		unit_test( test_getting_etherstub_names ),
		unit_test( test_getting_etherstub_names_when_opeartion_fails ),
		unit_test( test_successful_getting_parameter_value ),
		unit_test( test_getting_parameter_value_of_etherstub_with_wrong_name ),
		unit_test( test_getting_parameter_value_when_operation_fails ),
		unit_test( test_successful_getting_property_value ),
		unit_test( test_getting_property_value_of_etherstub_with_wrong_name ),
		unit_test( test_getting_property_value_when_operation_fails ),
		unit_test( test_getting_statistic_value_when_operation_fails ),
		unit_test( test_successful_getting_statistic_value ),
		unit_test( test_getting_statistic_value_of_etherstub_with_wrong_name ),
		unit_test( test_setting_property_value_of_etherstub_with_wrong_name ),
		unit_test( test_successful_setting_property_value ),
		unit_test( test_setting_property_value_when_operation_fails ),
	};

	init_mapping();

	return run_tests( tests );
}

