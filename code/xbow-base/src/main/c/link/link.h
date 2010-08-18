#ifndef LINK_WRAPPER_H
#define LINK_WRAPPER_H

#include "types.h"


/**
 * \brief  Initializes the library.
 *
 * \return  XBOW_STATUS_OK           on success
 * \return  XBOW_STATUS_UNKNOWN_ERR  otherwise
 */
int init();


/**
 * \brief  Retrieves specific NIC info.
 *
 * Returns a pointer to nic_info_t structure filled with
 * data for a NIC.
 *
 * \param  name  NIC name
 *
 * \return  pointer to nic_info_t filled with data.
 *
 * \warning  The caller is responsible for freeing returned pointer
 *           with free_nic_info function.
 */
nic_info_t* get_nic_info( char* name );


/**
 * \brief  Retrieves NICs info.
 *
 * Returns a pointer to nic_infos_t structure filled with
 * data for all NICs created in the system.
 *
 * \return  pointer to nic_infos_t filled with data.
 *
 * \warning  The caller is responsible for freeing returned pointer
 *           with free_nic_infos function.
 */
nic_infos_t* get_nic_infos( void );

#endif

