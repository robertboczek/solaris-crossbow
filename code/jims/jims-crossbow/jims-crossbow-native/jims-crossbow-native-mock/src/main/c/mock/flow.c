#include <string.h>

#include <mock/cmockery.h>
#include <mock/common.h>
#include <mock/flow.h>


dladm_status_t dladm_flow_remove( dladm_handle_t handle, char* flow,
                                  boolean_t temporary, const char* root )
{
	check_expected( flow );
	check_expected( temporary );

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

