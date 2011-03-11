#ifndef FLOWADM_WRAPPER_FUNCTOR_H
#define FLOWADM_WRAPPER_FUNCTOR_H

#include <libdladm.h>
#include <libdlflow.h>


/*
 * dladm_walk_flow functors
 */

/**
 * \brief  dladm_walk_flow functor that counts flows
 *
 * For each flow, increases contents of ( ( int* ) counter ) by 1.
 *
 * \param  counter  address of int counter
 *
 * \return  DLADM_WALK_CONTINUE  always
 */
int count( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
           void* counter );


/**
 * \brief  dladm_walk_flow functor that retrieves flows' attributes
 *
 * Fills memory pointed by *( ( dladm_flow_attr_t** ) arg ) with
 * flow info data and advances the pointer by 1.
 *
 * \param  arg  pointer to array of dladm_flow_attr_t elements,
 *              which has to be allocated by caller and has to be
 *              big enough to hold data for all flows
 *
 * \return  DLADM_WALK_CONTINUE  always
 */
int get_attrs( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
               void* arg );


/*
 * dladm_walk_flowprop functors
 */

/**
 * \brief  Collects flow's properties.
 *
 * \param  arg  pointer to get_props_arg_t structure that has
 *              flow member filled with flow name and
 *              key_value_pair_it equal to first element
 *              of a key_value_pair_t** array
 *
 *              key_value_pair_it member is advanced after each call
 */
int get_props( void* arg, const char* propname );


/*
 * dladm_walk_datalink_id functors
 */

/**
 * \brief  Collects names of links present in the system.
 *
 * Fills *( ( char** ) arg ) buffer with names of links found in the system.
 * Advances the pointer by MAXLINKNAMELEN offset after each call.
 *
 * \param  arg  pointer to address of buffer that has to be
 *              big enough to hold all links' names
 */
int collect_link_names( dladm_handle_t handle,
                        datalink_id_t link_id, void* arg );


/**
 * \brief  dladm_walk_datalink_id functor that counts links
 *
 * For each link, increases contents of ( ( int* ) counter ) by 1.
 *
 * \param  counter  address of int counter
 *
 * \return  DLADM_WALK_CONTINUE  always
 */
int count_links( dladm_handle_t handle,
                 datalink_id_t link_id, void* counter );


int get_usage( dladm_usage_t* usage, void* arg );

#endif

