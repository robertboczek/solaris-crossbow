#include <string.h>

#include <test/common.h>
#include <test/mock.h>


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

