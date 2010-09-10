#ifndef LINK_IP_H
#define LINK_IP_H


/**
 * \brief  Performs plumbing needed for IP to use link.
 *
 * \param  link  link to plumb
 *
 * \return  TODO-DAWID
 */
int plumb( char* link );


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
int set_ip_address(char *link, char *address);


/**
 * \brief  Returns ip address as a string.
 *
 * \param  link  link name
 *
 * \return  pointer to char array  containing ip address
 * \return  NULL                   when problems with reading occured 
 */
char* get_ip_address(char *link);


/**
 * \brief  Sets IPv4 netmask for link.
 *
 * \param  link  link name
 * \param  mask  netmask (in string format - e.g. 255.255.255.0)
 *
 * \return  XBOW_STATUS_OK             on success
 * \return  XBOW_STATUS_INVALID_VALUE  if the netmask has invalid format
 * \return  XBOW_STATUS_IOCTL_ERR      error while invoking ioctl()
 * \return  XBOW_STATUS_UNKNOWN_ERR    other error
 */
int set_netmask( char* link, char* mask );


/**
 * \brief  Retrieves IPv4 netmask set for link.
 *
 * \param  link  link name
 *
 * \return  string form of IPv4 netmask set for link
 * \return  NULL  on failure
 *
 * \warning  The caller is responsible for freeing the returned pointer
 *           with free function.
 */
char* get_netmask( char* link );

#endif

