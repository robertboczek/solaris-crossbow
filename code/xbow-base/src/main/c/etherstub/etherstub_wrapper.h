#ifndef ETHERSTUB_WRAPPER_H
#define ETHERSTUB_WRAPPER_H

#include <libdladm.h>
#include <libdllink.h>
#include <libdlvnic.h>
#include <libdlstat.h>
#include <string.h>
#include <stropts.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/ethernet.h>
#include <sys/param.h>
#include <sys/mac.h>

#include <ofmt.h>
#include <netinet/vrrp.h>
#include "types.h"

int init();

/**
 * \brief  Removes an etherstub.
 *
 * \param  name	  	      	etherstub name
 * \param  persistence_type  	determines whether the change will be temporary or persistent
 *
 * \return  etherstub_return_type_t See types.h to see more details
*/
etherstub_return_type_t delete_etherstub( char* name, persistence_type_t persistence_type );

/**
 * \brief  Creates an etherstub.
 *
 * \param  name	             	etherstub name
 * \param  persistence_type  	determines whether the change will be temporary or persistent
 *
 * \return  etherstub_return_type_t See types.h to see more details
*/
etherstub_return_type_t create_etherstub( char* name, persistence_type_t persistence_type );

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
 * 		caller is responsible for freeing the memory
*/
char* get_etherstub_parameter( char *name, etherstub_parameter_type_t parameter);

/**
 * \brief Returns requested etherstub statistics
 * \param  name		etherstub name
 * \param  property  	type of requested statistic
 *
 * \return  will contain value of requested statistic
 * 		caller is responsible for freeing the memory
*/
char* get_etherstub_statistic( char *name, etherstub_statistic_type_t property);
/**
 * \brief Sets requested etherstub property
 * \param  name		etherstub name
 * \param  property  	type of property to be set
 * \param  value	requested value of the property
 *
 * \return  etherstub_return_type_t See types.h to see more details
*/
etherstub_return_type_t set_etherstub_property( char *name, etherstub_property_type_t property, char *value );
/**
 * \brief Returns requested etherstub property
 * \param  name		etherstub name
 * \param  property  	type of property to be read
 *
 * \return  will contain values of requested property
 * 		caller is responsible for freeing the memory

*/
char* get_etherstub_property( char *name, etherstub_property_type_t property);
#endif