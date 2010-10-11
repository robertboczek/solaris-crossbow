#include <string.h>

#include <mock/cmockery.h>
#include <mock/common.h>


dladm_status_t dladm_open( dladm_handle_t* handle )
{
	return ( dladm_status_t ) mock();
}


char* dladm_proto2str( uint8_t protocol )
{
	check_expected( protocol );
	return mock();
}


void fill_buffer( void* target, void* source, int len )
{
	if ( len < 0 )
	{
		// Indirection.

		target = *( ( char** ) target );
		len = -len;
	}

	memcpy( target, source, len );
}

