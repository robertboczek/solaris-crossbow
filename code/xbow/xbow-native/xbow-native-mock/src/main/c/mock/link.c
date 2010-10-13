#include <libdllink.h>
#include <sys/vnic.h>

#include <string.h>

#include <mock/cmockery.h>
#include <mock/common.h>
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


dladm_status_t dladm_walk( dladm_walkcb_t* p, dladm_handle_t handle, void* arg,
                           datalink_class_t data_link_class, datalink_media_t b, uint32_t flags )
{
	check_expected( flags );
	check_expected( data_link_class );

	int data_len = ( int ) mock();
	if ( 0 != data_len )
	{
		fill_buffer( arg, mock(), data_len );
	}

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_vnic_delete( dladm_handle_t handle, datalink_id_t link_id,
                                  uint32_t flags )
{
	check_expected( flags );

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_get_linkprop( dladm_handle_t handle, datalink_id_t link_id,
                                   dladm_prop_type_t a, const char* parameter,
                                   char** value, uint_t* b )
{
	check_expected( parameter );	

	strcpy( *value, mock() );

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_set_linkprop( dladm_handle_t handle, datalink_id_t link,
                                   const char* property, char** value, uint_t a,
                                   uint_t flags )
{
	check_expected( property );
	check_expected( flags );

	return ( dladm_status_t ) mock();
}


boolean_t dladm_valid_linkname( const char* link )
{
	check_expected( link );
	
	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_vnic_create( dladm_handle_t handle, const char* vnic, datalink_id_t linkid,
                                  vnic_mac_addr_type_t mac_addr_type, uchar_t* mac_addr, uint_t mac_len,
                                  int* mac_slot, uint_t mac_prefix_len, uint16_t vid, vrid_t vrid,
                                  int af, datalink_id_t* vnic_id_out, dladm_arg_list_t* proplist,
                                  uint32_t flags )
{
	check_expected( vnic );
	check_expected( flags );

	return ( dladm_status_t ) mock();
}

