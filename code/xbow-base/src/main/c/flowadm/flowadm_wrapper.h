#ifndef FLOWADM_WRAPPER_H
#define FLOWADM_WRAPPER_H

#include <libdlflow.h>

#include "types.h"


/**
 * \brief  Initializes the library.
 *
 * \return  0        on success
 * \return  non-zero otherwise
 */
int init();


/**
 * \brief  Creates new flow.
 *
 * \param  flow_info  flow descriptor
 */
int create( flow_info_t* flow_info );


/**
 * \brief  Removes a flow.
 *
 * \param  flow       flow name
 * \param  temporary  determines is the change temporary
 *
 * \return  0         on success
 * \return  non-zero  otherwise
 */
int remove_flow( char* flow, int temporary );


/**
 * \brief Retrieves flows' attributes.
 *
 * Allocates and fills *flow_attrs array with attributes
 * for flows assigned do link_name. *len is filled with flows count.
 *
 * \param  link_name
 * \param  flow_attrs
 * \param  len
 */
void collect_flow_attrs( char* link_name,
                         dladm_flow_attr_t** flow_attrs, int* len );


int set_property( char* flow,
                  char* key, char* values[], unsigned int values_len,
                  int temporary );


flow_infos_t* get_flows_info( char* link_name[] );


int reset_property( char* flow, char* key, int temporary );


key_value_pairs_t* get_properties( char* flow );


/*
 * Accounting-specific functions.
 */

int enable_accounting();

int disable_accounting();

#endif

