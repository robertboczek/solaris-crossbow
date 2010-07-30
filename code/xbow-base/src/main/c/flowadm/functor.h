#ifndef FLOWADM_WRAPPER_FUNCTOR_H
#define FLOWADM_WRAPPER_FUNCTOR_H

#include <libdladm.h>
#include <libdlflow.h>


/*
 * dladm_walk_flow functors
 */

int count( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
           void* counter );


int get_attrs( dladm_handle_t handle, dladm_flow_attr_t* flow_attr,
               void* arg );


/*
 * dladm_walk_flowprop functors
 */

int get_props( void* arg, const char* propname );

#endif

