#include <test/common.h>
#include <test/mock.h>

#include <string.h>


static void fill_buffer( void* target, void* source, int len )
{
	if ( len < 0 )
	{
		// Indirection.

		target = *( ( char** ) target );
		len = -len;
	}

	memcpy( target, source, len );
}


dladm_status_t dladm_walk_datalink_id( int ( *fn )( dladm_handle_t, datalink_id_t, void* ),
                                       dladm_handle_t handle, void* arg, datalink_class_t class,
                                       datalink_media_t dmedia, uint32_t flags )
{
	int data_len = ( int ) mock();
	if ( 0 != data_len )
	{
		fill_buffer( arg, mock(), data_len );
	}

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_open( dladm_handle_t* handle )
{
	return ( dladm_status_t ) mock();
}

