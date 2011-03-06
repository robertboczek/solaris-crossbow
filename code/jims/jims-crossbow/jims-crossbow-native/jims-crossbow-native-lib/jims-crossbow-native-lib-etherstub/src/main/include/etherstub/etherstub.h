#ifndef ETHERSTUB_WRAPPER_H
#define ETHERSTUB_WRAPPER_H

#include "types.h"
#include <common/defs.h>

/**
 * \brief  Inits the etherstub library
*/
int init();

/**
 * \brief  Removes an etherstub.
 *
 * \param  name	  	etherstub name
 * \param  temporary  	determines whether the change will be temporary or persistent ( 0 - persistent, any other value - temporary )
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
*/
int delete_etherstub( char* name, int temporary );

/**
 * \brief  Creates an etherstub.
 *
 * \param  name	        etherstub name
 * \param  temporary  	determines whether the change will be temporary or persistent ( 0 - persistent, any other value - temporary )
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_TOO_LONG_NAME when etherstub name was too long
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
*/
int create_etherstub( char* name, int temporary );

/**
 * \brief  Returns list of existing etherstubs.
 * \param  number_of_etherstubs  will contains numer of etherstubs
 *
 * \return  will contain all etherstubs names or NULL if there is no etherstubs existing, 
 * 		caller is responsible for freeing the memory
*/
char** get_etherstub_names( );

/**
 * \brief Returns requested etherstub parameter
 * \param  name		etherstub name
 * \param  property  	type of requested parameter
 *
 * \return  will contain value of requested parameter
 * 		caller is responsible for freeing the memory, when value couldn't be readed NULL is returned
*/
char* get_etherstub_parameter( char *name, char* parameter);

/**
 * \brief Returns requested etherstub statistics
 * \param  name		etherstub name
 * \param  property  	type of requested statistic
 *
 * \return  will contain value of requested statistic
 * 		caller is responsible for freeing the memory, when value couldn't be readed NULL is returned
*/
char* get_etherstub_statistic( char *name, char* property);
/**
 * \brief Sets requested etherstub property
 * \param  name		etherstub name
 * \param  property  	type of property to be set
 * \param  value	requested value of the property
 *
 * \return  XBOW_STATUS_OK on success
 * \return  XBOW_STATUS_TOO_LONG_NAME when etherstub name was too long
 * \return  XBOW_STATUS_INVALID_NAME when etherstub name was incorrect
 * \return  XBOW_STATUS_OPERATION_FAILURE when operation failed
*/
int set_etherstub_property( char *name, char* property, char *value );
/**
 * \brief Returns requested etherstub property
 * \param  name		etherstub name
 * \param  property  	type of property to be read
 *
 * \return  will contain values of requested property
 * 		caller is responsible for freeing the memory, when value couldn't be readed NULL is returned

*/
char* get_etherstub_property( char *name, char* property);
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
