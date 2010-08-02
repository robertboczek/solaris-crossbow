#ifndef ETHERSTUB_WRAPPER_H
#define ETHERSTUB_WRAPPER_H

#include <libdladm.h>
#include <libdllink.h>
#include <string.h>
#include <stropts.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/ethernet.h>
#include <sys/param.h>
#include <sys/mac.h>
#include <netinet/vrrp.h>


typedef enum {
	VNIC_MAC_ADDR_TYPE_UNKNOWN = -1,
	VNIC_MAC_ADDR_TYPE_FIXED,
	VNIC_MAC_ADDR_TYPE_RANDOM,
	VNIC_MAC_ADDR_TYPE_FACTORY,
	VNIC_MAC_ADDR_TYPE_AUTO,
	VNIC_MAC_ADDR_TYPE_PRIMARY,
	VNIC_MAC_ADDR_TYPE_VRID
} vnic_mac_addr_type_t;


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

#endif
