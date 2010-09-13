#include <libdladm.h>

#include <common/defs.h>

#include <flow/flowadm_wrapper.h>

#include <test/common.h>


dladm_status_t dladm_flow_remove( dladm_handle_t handle, char* flow,
                                  boolean_t temporary, const char* root )
{
	check_expected( flow );
	check_expected( temporary );

	return ( dladm_status_t ) mock();
}


void test_removing_flow( void** state )
{
	char flow[] = "flow";
	boolean_t temporary = 1;

	expect_string( dladm_flow_remove, flow, flow );
	expect_value( dladm_flow_remove, temporary, temporary );
	will_return( dladm_flow_remove, DLADM_STATUS_OK );

	assert_true( XBOW_STATUS_OK == remove_flow( flow, temporary ) );
}

