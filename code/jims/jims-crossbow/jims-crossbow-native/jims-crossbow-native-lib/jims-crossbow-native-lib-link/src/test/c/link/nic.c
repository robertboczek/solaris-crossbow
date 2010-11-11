#include <common/defs.h>

#include <link/functor.h>
#include <link/link.h>
#include <link/memory.h>

#include <mock/cmockery.h>
#include <mock/common.h>
#include <mock/link.h>

#include <test/link/functor.h>


void test_init( void** state )
{
	will_return( dladm_open, DLADM_STATUS_OK );
	
	assert_true( XBOW_STATUS_OK == init() );
}


void test_init_failed( void** state )
{
	will_return( dladm_open, DLADM_STATUS_BADARG );

	assert_false( XBOW_STATUS_OK == init() );
}


void test_get_nic_infos_no_nics( void** state )
{
	int nic_count = 0;
	
	will_return( dladm_walk_datalink_id, sizeof( nic_count ) );
	will_return( dladm_walk_datalink_id, &nic_count );
	will_return( dladm_walk_datalink_id, DLADM_STATUS_OK );

	will_return( dladm_walk_datalink_id, 0 );  // Don't modify the iterator.
	will_return( dladm_walk_datalink_id, DLADM_STATUS_OK );

	nic_infos_t* infos = get_nic_infos();

	assert_true( NULL != infos );
	assert_int_equal( nic_count, infos->len );

	free_nic_infos( infos );
}

