#include <test/common.h>
#include <test/flow/aux.h>


int main( int argc, char** argv )
{
	const UnitTest tests[] =
	{
		unit_test( test_strend ),
	};

	return run_tests( tests );
}

