#include <link/functor.h>

#include <mock/cmockery.h>

#include <test/link/functor.h>


void test_count_functor( void** state )
{
	dladm_handle_t handle;
	int counter = 0;
	const int final = 5;

	for ( int i = 0; i < final; ++i )
	{
		assert_int_equal( DLADM_WALK_CONTINUE, count( handle, 0, &counter ) );
	}

	assert_int_equal( final, counter );
}

