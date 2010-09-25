#include <stdlib.h>
#include <string.h>

#include <flow/aux.h>
#include <flow/memory.h>

#include <test/common.h>
#include <test/flow/aux.h>


void test_strend( void** state )
{
	char string[] = "astring";
	char empty_string[] = "";

	assert_true( ( string + sizeof( string ) - 1 ) == strend( string ) );
	assert_true( empty_string == strend( empty_string ) );
}


void alloc_kvps( void** state )
{
	*state = malloc_key_value_pairs( 20 );
}

void free_kvps( void** state )
{
	free_key_value_pairs( *state );
}


void test_flatten_empty_kvps( void** state )
{
	key_value_pairs_t* kvps = *state;
	kvps->len = 0;
	char* flattened = flatten_key_value_pairs( kvps );

	assert_string_equal( "", flattened );

	free( flattened );
}


void test_flatten_one_kvp( void** state )
{
	key_value_pairs_t* kvps = *state;

	kvps->len = 1;
	strcpy( kvps->key_value_pairs[ 0 ]->key, "key" );
	strcpy( kvps->key_value_pairs[ 0 ]->value, "value" );

	char* flattened = flatten_key_value_pairs( kvps );

	assert_string_equal( "key=value", flattened );

	free( flattened );
}


void test_flatten_two_kvps( void** state )
{
	key_value_pairs_t* kvps = *state;

	kvps->len = 2;
	strcpy( kvps->key_value_pairs[ 0 ]->key, "key0" );
	strcpy( kvps->key_value_pairs[ 0 ]->value, "value0" );
	strcpy( kvps->key_value_pairs[ 1 ]->key, "key1" );
	strcpy( kvps->key_value_pairs[ 1 ]->value, "value1" );

	char* flattened = flatten_key_value_pairs( kvps );

	assert_string_equal( "key0=value0,key1=value1", flattened );

	free( flattened );
}

