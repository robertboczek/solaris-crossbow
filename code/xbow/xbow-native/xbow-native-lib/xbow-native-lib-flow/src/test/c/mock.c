#include <string.h>

#include <test/common.h>
#include <test/mock.h>


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


dladm_status_t dladm_flow_remove( dladm_handle_t handle, char* flow,
                                  boolean_t temporary, const char* root )
{
	check_expected( flow );
	check_expected( temporary );

	return ( dladm_status_t ) mock();
}


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


dladm_status_t dladm_get_flowprop( dladm_handle_t handle, const char* flow,
                                   uint32_t type, const char* propname,
                                   char** values, uint_t* values_len )
{
	check_expected( flow );
	check_expected( propname );

	char** vals = mock();
	for ( int i = 0; NULL != vals[ i ]; ++i )
	{
		strncpy( values[ i ], vals[ i ], DLADM_STRSIZE );
	}

	*values_len = *( ( int* ) mock() );
	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_parse_flow_attrs( char* str, dladm_arg_list_t** listp,
                                       boolean_t novalues )
{
	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_parse_flow_props( char* str, dladm_arg_list_t** listp,
                                       boolean_t novalues )
{
	check_expected( str );

	*listp = mock();
	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_flow_add( dladm_handle_t handle, datalink_id_t linkid,
                               dladm_arg_list_t* attrlist,
                               dladm_arg_list_t* proplist, char* flowname,
                               boolean_t temporary, const char* root )
{
	check_expected( linkid );
	check_expected( flowname );
	check_expected( temporary );

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_set_flowprop( dladm_handle_t handle, const char* flow,
                                   const char* key, char** values,
                                   uint_t values_len, uint_t persist_opt,
                                   char** errprop )
{
	check_expected( flow );
	check_expected( key );

	return ( dladm_status_t ) mock();
}


void dladm_flow_attr_ip2str( dladm_flow_attr_t* attr, char* buffer, size_t buffer_len )
{
	check_expected( attr );

	size_t data_len = ( size_t ) mock();
	memcpy( buffer, mock(), data_len );
}


char* dladm_proto2str( uint8_t protocol )
{
	check_expected( protocol );
	return mock();
}


dladm_status_t dladm_walk_flow( int ( *fn )( dladm_handle_t, dladm_flow_attr_t*, void* ),
                                dladm_handle_t handle, datalink_id_t link_id,
                                void* arg, boolean_t persist )
{
	check_expected( link_id );

	int data_len = ( int ) mock();
	if ( 0 != data_len )
	{
		fill_buffer( arg, mock(), data_len );
	}

	return ( dladm_status_t ) mock();
}


dladm_status_t dladm_walk_flowprop( int ( *func )( void*, const char* ),
                                    const char* flow, void* arg )
{
	check_expected( flow );

	int data_len = ( int ) mock();
	if ( 0 != data_len )
	{
		fill_buffer( arg, mock(), data_len );
	}

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


dladm_status_t dladm_open( dladm_handle_t* handle )
{
	return ( dladm_status_t ) mock();
}

