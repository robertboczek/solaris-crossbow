#ifndef FLOW_FLOWADM_TEST_H
#define FLOW_FLOWADM_TEST_H

void test_removing_flow( void** state );

void alloc_info( void** state );
void free_info( void** state );

void test_create_invalid_attributes( void** state );
void test_create_invalid_properties( void** state );
void test_create_invalid_name( void** state );
void test_create_flow( void** state );
void test_reset_property( void** state );
void test_reset_property_persistently( void** state );
void test_reset_property_invalid_key( void** state );
void test_set_property( void** state );
void test_get_flows_info_with_empty_input( void** state );
void test_get_flow_info_one_flow( void** state );
void test_init( void** state );
void test_init_failed( void** state );
void test_get_flows_info_all_links( void** state );

#endif

