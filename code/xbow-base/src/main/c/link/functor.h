#ifndef LINK_WRAPPER_FUNCTOR_H
#define LINK_WRAPPER_FUNCTOR_H

#include <libdladm.h>


/*
 * dladm_walk_datalink_id functors
 */

/**
 * \brief  dladm_walk_datalink_id functor that counts NICs.
 *
 * For each discovered NIC, increases contents of ( ( int* ) counter ) by 1.
 *
 * \param  counter  address of int counter
 *
 * \return  DLADM_WALK_CONTINUE  always
 */
int count( dladm_handle_t handle, datalink_id_t link_id, void* counter );

#endif

