#ifndef FLOW_FLOWADM_TEST_H
#define FLOW_FLOWADM_TEST_H

void test_removing_flow( void** state );

void alloc_info( void** state );
void free_info( void** state );

void test_create_invalid_attributes( void** state );
void test_create_invalid_properties( void** state );
void test_create_invalid_name( void** state );
void test_create_flow( void** state );

#endif

