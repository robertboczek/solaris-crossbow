#ifndef FLOW_MOCK_H
#define FLOW_MOCK_H

#include <libdladm.h>
#include <libdlflow.h>
#include <libdllink.h>


dladm_status_t dladm_datalink_id2info( dladm_handle_t handle, datalink_id_t id,
                                       uint32_t* flag, datalink_class_t* link_class,
                                       uint32_t* media, char* link, size_t len );


dladm_status_t dladm_name2info( dladm_handle_t handle, const char* link,
                                datalink_id_t* linkidp, uint32_t* flagp,
                                datalink_class_t* classp, uint32_t* mediap );


dladm_status_t dladm_flow_remove( dladm_handle_t handle, char* flow,
                                  boolean_t temporary, const char* root );


dladm_status_t dladm_get_flowprop( dladm_handle_t handle, const char* flow,
                                   uint32_t type, const char* propname,
                                   char** values, uint_t* values_len );


dladm_status_t dladm_parse_flow_attrs( char* str, dladm_arg_list_t** listp,
                                       boolean_t novalues );


dladm_status_t dladm_parse_flow_props( char* str, dladm_arg_list_t** listp,
                                       boolean_t novalues );


dladm_status_t dladm_flow_add( dladm_handle_t handle, datalink_id_t linkid,
                               dladm_arg_list_t* attrlist,
                               dladm_arg_list_t* proplist, char* flowname,
                               boolean_t temporary, const char* root );


dladm_status_t dladm_set_flowprop( dladm_handle_t handle, const char* flow,
                                   const char* key, char** values,
                                   uint_t values_len, uint_t persist_opt,
                                   char** root );


/**
 * mock sequence: ( sizeof data to write, pointer to data )
 */
void dladm_flow_attr_ip2str( dladm_flow_attr_t* attr, char* buffer, size_t buffer_len );


dladm_status_t dladm_walk_flow( int ( *fn )( dladm_handle_t, dladm_flow_attr_t*, void* ),
                                dladm_handle_t handle, datalink_id_t link_id,
                                void* arg, boolean_t persist );


dladm_status_t dladm_walk_flowprop( int ( *func )( void*, const char* ),
                                    const char* flow, void* arg );


dladm_status_t dladm_open( dladm_handle_t* handle );

#endif

