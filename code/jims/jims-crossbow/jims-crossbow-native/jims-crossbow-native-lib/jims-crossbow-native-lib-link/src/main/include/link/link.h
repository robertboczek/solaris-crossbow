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


/**
 * \brief  Removes a vnic
 *
 * \param  name	  	vnic name
 * \param  temporary  	determines whether the change will be temporary or persistent ( 0 - persistent, any other value - temporary )
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
*/
int delete_vnic( char* name, int temporary );


/**
 * \brief  Creates an vnic.
 *
 * \param  name	        vnic name
 * \param  temporary  	determines whether the change will be temporary or persistent ( 0 - persistent, any other value - temporary )
 * \param  parent	parent link name
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_INVALID_NAME when etherstub parent name was incorrect
 * \return  XBOW_STATUS_TOO_LONG_NAME when etherstub name was too long
 * \return  XBOW_STATUS_TOO_LONG_PARENT_NAME when etherstub parent name was too long
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
 *
*/
int create_vnic( char* name, int temporary, char *parent );


/**
 * \brief  Returns array of existing link names.
 * \param  link_type determines type of link ( 0 - vnic's, 1 - nic's )
 *
 * \return  will contain all link names or NULL if there is no links existing, 
 * 		caller is responsible for freeing the memory
*/
char** get_link_names( int link_type );


/**
 * \brief Returns requested link parameter
 * \param  name		link name
 * \param  property  	type of requested parameter
 *
 * \return  will contain value of requested parameter
 * 		caller is responsible for freeing the memory
*/
char* get_link_parameter( char *name, char* parameter);


/**
 * \brief Returns requested link statistics
 * \param  name		link name
 * \param  property  	type of requested statistic
 *
 * \return  will contain value of requested statistic
 * 		caller is responsible for freeing the memory
*/
char* get_link_statistic( char *name, char* property);


/**
 * \brief Sets requested link property
 * \param  name		link name
 * \param  property  	type of property to be set
 * \param  value	requested value of the property
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
 *
*/
int set_link_property( char *name, char* property, char *value );


/**
 * \brief Returns requested link property
 * \param  name		link name
 * \param  property  	type of property to be read
 *
 * \return  will contain values of requested property
 * 		caller is responsible for freeing the memory
*/
char* get_link_property( char *name, char* property);

/**
 * \brief  Creates an vnic.
 *
 * \param  name	        vnic name
 * \param  property	type of property to reset
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
 *
*/
int reset_prop( char *name, char* property);

#endif

