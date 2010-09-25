#ifndef FLOW_AUX_TEST_H
#define FLOW_AUX_TEST_H

void test_strend( void** state );

void alloc_kvps( void** state );
void free_kvps( void** state );

void test_flatten_empty_kvps( void** state );
void test_flatten_one_kvp( void** state );
void test_flatten_two_kvps( void** state );

#endif

