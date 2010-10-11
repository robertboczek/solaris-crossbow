#include <string.h>

#include <mock/cmockery.h>
#include <mock/link.h>


dladm_status_t dladm_datalink_id2info( dladm_handle_t handle, datalink_id_t id,
                                       uint32_t* flag, datalink_class_t* link_class,
                                       uint32_t* media, char* link, size_t len )
{
	check_expected( id );

	strncpy( link, mock(), len );
	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_name2info( dladm_handle_t handle, const char* link,
                                datalink_id_t* linkidp, uint32_t* flagp,
                                datalink_class_t* classp, uint32_t* mediap )
{
	check_expected( link );

	memcpy( linkidp, mock(), sizeof( *linkidp ) );

	return ( dladm_status_t ) mock();
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

