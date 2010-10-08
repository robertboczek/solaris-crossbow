#include <common/mappings.h>

#include <test/common.h>
#include <test/flow/aux.h>
#include <test/flow/flowadm_wrapper.h>
#include <test/flow/functor.h>


int main( int argc, char** argv )
{
	const UnitTest tests[] =
	{
		/* aux.c */
		unit_test( test_strend ),

		unit_test_setup_teardown( test_flatten_empty_kvps, alloc_kvps, free_kvps ),
		unit_test_setup_teardown( test_flatten_one_kvp, alloc_kvps, free_kvps ),
		unit_test_setup_teardown( test_flatten_two_kvps, alloc_kvps, free_kvps ),

		/* flowadm_wrapper.h */
		unit_test( test_removing_flow ),
		unit_test_setup_teardown( test_create_invalid_attributes, alloc_info, free_info ),
		unit_test_setup_teardown( test_create_invalid_properties, alloc_info, free_info ),
		unit_test_setup_teardown( test_create_invalid_name, alloc_info, free_info ),
		unit_test_setup_teardown( test_create_flow, alloc_info, free_info ),
		unit_test( test_reset_property ),
		unit_test( test_reset_property_persistently ),
		unit_test( test_reset_property_invalid_key ),
		unit_test( test_set_property ),
		unit_test( test_get_flows_info_with_empty_input ),
		unit_test( test_get_flow_info_one_flow ),

		unit_test( test_get_flows_info_all_links ),
		unit_test( test_init ),
		unit_test( test_init_failed ),

		/* functor.c */
		unit_test( test_count_functor ),
		unit_test( test_get_attrs_functor ),
		unit_test( test_count_links_functor ),
		unit_test( test_get_props_functor ),
		unit_test( test_collect_link_names_functor ),
		unit_test( test_get_props_functor_no_props ),
	};

	init_mapping();

	return run_tests( tests );
}

