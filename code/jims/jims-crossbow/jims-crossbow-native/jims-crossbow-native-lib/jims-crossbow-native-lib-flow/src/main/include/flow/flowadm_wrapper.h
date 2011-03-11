#ifndef FLOWADM_WRAPPER_H
#define FLOWADM_WRAPPER_H

#include "types.h"


/**
 * \brief  Initializes the library.
 *
 * \return  XBOW_STATUS_OK           on success
 * \return  XBOW_STATUS_UNKNOWN_ERR  otherwise
 */
int init();


/**
 * \brief  Creates new flow.
 *
 * \param  flow_info  flow descriptor
 * \param  temporary  determines whether the flow is temporary
 *
 * \return  XBOW_STATUS_OK              on success
 * \return  XBOW_STATUS_PROP_PARSE_ERR  properties validation failed
 * \return  XBOW_STATUS_UNKNOWN_ERR     other error
 */
int create( flow_info_t* flow_info, int temporary );


/**
 * \brief  Removes a flow.
 *
 * \param  flow       flow name
 * \param  temporary  determines is the change temporary
 *
 * \return  XBOW_STATUS_OK           on success
 * \return  XBOW_STATUS_NOTFOUND     specified flow was not found
 * \return  XBOW_STATUS_UNKNOWN_ERR  otherwise
 */
int remove_flow( char* flow, int temporary );


/**
 * \brief  Sets flow properties.
 *
 * \param  flow        flow name
 * \param  key         property key
 * \param  values      array of values to be set
 * \param  values_len  number of elements values array has
 * \param  temporary   determines if the operation is temporary
 *
 * \return  XBOW_STATUS_OK           on success
 * \return  XBOW_STATUS_UNKNOWN_ERR  otherwise
 */
int set_property( char* flow,
                  char* key, char* values[], unsigned int values_len,
                  int temporary );


/**
 * \brief  Retrieves flows info.
 *
 * Returns a pointer to flow_infos_t structure filled with
 * data for flows created over links specified by link_name.
 *
 * If NULL == link_name, data for all flows is returned.
 *
 * \param  link_name  NULL-terminated array of link names
 *                    or NULL pointer
 *
 * \return  pointer to flow_infos_t filled with data for specified flows.
 *
 * \warning  The caller is responsible for freeing returned pointer
 *           with free_flow_infos function.
 */
flow_infos_t* get_flows_info( char* link_name[] );


/**
 * \brief  Resets flow property.
 *
 * \param  flow       flow name
 * \param  key        property key
 * \param  temporary  determines whether the operation is temporary
 *
 * \return  XBOW_STATUS_OK           on success
 * \return  XBOW_STATUS_UNKNOWN_ERR  otherwise
 */
int reset_property( char* flow, char* key, int temporary );


/**
 * \brief  Returns properties for a flow.
 *
 * \param  flow  flow name
 *
 * \return  a pointer to key_value_pairs_t that contains
 *          flow's properties
 *
 * \warning  The caller is responsible for freeing returned pointer
 *           with free_key_value_pairs function.
 */
key_value_pairs_t* get_properties( char* flow );


/*
 * Accounting-specific functions.
 */

#if 0
int enable_accounting();

int disable_accounting();
#endif

flow_statistics_t* get_statistics( char* flow, char* stime );

#endif

