#ifndef FLOW_MOCK_H
#define FLOW_MOCK_H

#include <libdladm.h>
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

#endif

