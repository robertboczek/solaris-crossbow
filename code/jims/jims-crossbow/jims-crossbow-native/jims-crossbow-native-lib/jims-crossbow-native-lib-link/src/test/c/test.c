#include <common/mappings.h>

#include <mock/cmockery.h>
#include <mock/common.h>
#include <mock/link.h>

#include <test/link/functor.h>
#include <test/link/nic.h>
#include <test/link/link_wrapper.h>


int main( int argc, char** argv )
{
	const UnitTest tests[] =
	{
		unit_test( test_successful_removing_vnic ),
		unit_test( test_removing_invalidname_vnic ),
		unit_test( test_temporal_removing_vnic ),
		unit_test( test_creating_vnic_with_invalid_parent_link_name ),
		unit_test( test_successful_creating_vnic ),
		unit_test( test_temporal_creating_vnic ),
		unit_test( test_temporal_creating_vnic_with_invalid_name ),
		unit_test( test_creating_vnic_with_too_long_name ),
		unit_test( test_getting_link_names ),
		unit_test( test_getting_link_names_when_opeartion_fails ),
		unit_test( test_successful_getting_parameter_value ),
		unit_test( test_getting_parameter_value_of_link_with_wrong_name ),
		unit_test( test_getting_parameter_value_when_operation_fails ),
		unit_test( test_successful_getting_property_value ),
		unit_test( test_getting_property_value_of_link_with_wrong_name ),
		unit_test( test_getting_property_value_when_operation_fails ),
		unit_test( test_getting_statistic_value_when_operation_fails ),
		unit_test( test_successful_getting_statistic_value ),
		unit_test( test_getting_statistic_value_of_link_with_wrong_name ),
		unit_test( test_setting_property_value_of_link_with_wrong_name ),
		unit_test( test_successful_setting_property_value ),
		unit_test( test_setting_property_value_when_operation_fails ),

		/* functor.c */
		unit_test( test_count_functor ),

		/* link.c */
		unit_test( test_init ),
		unit_test( test_init_failed ),
		unit_test( test_get_nic_infos_no_nics ),
	};

	init_mapping();

	return run_tests( tests );
}

