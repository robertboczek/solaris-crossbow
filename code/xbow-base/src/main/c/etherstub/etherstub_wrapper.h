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
 * \param  name	      etherstub name
 * \param  temporary  determines is the change temporary ( 0 - temporary, != 0 persistent )
 * \param  rootDir    indicates the root dir catalog see man dladm for details
 *
 * \return  0         on success
 * \return  non-zero  otherwise ( 1 - couldn't delete, 2 - invalid etherstub name )
*/
int delete_etherstub( char* name, int temporary, char *rootDir );

/**
 * \brief  Creates an etherstub.
 *
 * \param  name	      etherstub name
 * \param  temporary  determines is the change temporary ( 0 - temporary, != 0 persistent )
 * \param  rootDir    indicates the root dir catalog see man dladm for details
 *
 * \return  0         on success
 * \return  non-zero  otherwise (  )
*/
int create_etherstub( char* name, int temporary, char *rootDir );

/**
 * \brief  Returns list of existing etherstubs.
 * \param  names	will contain all etherstubs names
 * \param  number_of_etherstubs  will contains numer of etherstubs
 *
 * \return  0         on success
 * \return  non-zero  otherwise (  )
*/
int get_etherstub_names( char*** names, int *number_of_etherstubs);

#endif
