#include <link/functor.h>


int count( dladm_handle_t handle, datalink_id_t link_id, void* counter )
{
	++( *( ( int* ) counter ) );

	return DLADM_WALK_CONTINUE;
}

