#include <flow/aux.h>

#include <test/common.h>
#include <test/flow/aux.h>


void test_strend( void** state )
{
	char string[] = "astring";
	char empty_string[] = "";

	assert_true( ( string + sizeof( string ) - 1 ) == strend( string ) );
	assert_true( empty_string == strend( empty_string ) );
}

