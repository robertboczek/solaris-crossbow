#ifndef LINK_IP_H
#define LINK_IP_H

#include <link/types.h>


/**
 * \brief  Sets new ip address to link.
 *
 * \param  link     link name
 * \param  address  new address in a string format
 *                  ( for example: '192.168.0.13' )
 *
 * \return  XBOW_STATUS_OK                 on success
 * \return  XBOW_STATUS_OPERATION_FAILURE  when operation failed
 */
int set_ip_address(char* link, char* address);

#endif
