#include <stdarg.h>
#include <stddef.h>
#include <setjmp.h>
#include <google/cmockery.h>

#include <libdladm.h>

#include <common/defs.h>


void test_ok_status_mapping( void** state )
{
	assert_int_equal( XBOW_STATUS_OK, map_status( DLADM_STATUS_OK ) );
}


void test_incorrect_dladm_status_mapping( void** state )
{
	assert_int_equal( XBOW_STATUS_UNKNOWN_ERR, map_status( -1 ) );
	assert_int_equal( XBOW_STATUS_UNKNOWN_ERR, map_status( XBOW_STATUS_LEN_ ) );
}


int main( int argc, char** argv )
{
	// Initialize mapping (once).
	
	init_mapping();

	const UnitTest tests[] =
	{
		unit_test( test_ok_status_mapping ),
		unit_test( test_incorrect_dladm_status_mapping ),
	};

	return run_tests( tests );
}

