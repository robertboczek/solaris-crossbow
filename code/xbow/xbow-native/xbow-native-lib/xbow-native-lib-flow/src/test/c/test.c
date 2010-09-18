#include <common/mappings.h>

#include <test/common.h>
#include <test/flow/aux.h>
#include <test/flow/flowadm_wrapper.h>


int main( int argc, char** argv )
{
	const UnitTest tests[] =
	{
		unit_test( test_strend ),
		unit_test( test_removing_flow ),
	};

	init_mapping();

	return run_tests( tests );
}

