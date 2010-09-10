#include <arpa/inet.h>
#include <net/if.h>
#include <netinet/in.h>
#include <stropts.h>
#include <sys/sockio.h>
#include <unistd.h>

#include <stdlib.h>
#include <string.h>

#include <common/defs.h>
#include <common/mappings.h>

#include "ip.h"


int set_netmask( char* link, char* mask )
{
	struct sockaddr_in netmask = { 0 };
	int rc = XBOW_STATUS_OK;

	if ( ! inet_aton( mask, &( netmask.sin_addr ) ) )
	{
		rc = XBOW_STATUS_INVALID_VALUE;
	}
	else
	{
		struct lifreq lifr = { 0 };
		int s;

		// Fill lifr's link name and netmask.

		strncpy( lifr.lifr_name, link, sizeof( lifr.lifr_name ) );

		netmask.sin_family = AF_INET;
		memcpy( &lifr.lifr_addr, &netmask, sizeof( netmask ) );

		// Try to open socket.

		if ( -1 == (( s = socket( AF_INET, SOCK_DGRAM, 0 )) ) )
		{
			rc = XBOW_STATUS_UNKNOWN_ERR;
		}
		else
		{
			// Try to set netmask.

			if ( ioctl( s, SIOCSLIFNETMASK, &lifr ) < 0 )
			{
				rc = XBOW_STATUS_IOCTL_ERR;
			}
			
			close( s );
		}
	}

	return rc;
}


char* get_netmask( char* link )
{
	int s;
	size_t netmask_len = INET6_ADDRSTRLEN;
	char* netmask = malloc( netmask_len );

	memset( netmask, '\0', netmask_len );

	// Try to create socket.

	if ( -1 == (( s = socket( AF_INET, SOCK_DGRAM, 0 ) )) )
	{
		// TODO-DAWID: error handling
	}
	else
	{
		struct lifreq lifr = { 0 };
		strncpy( lifr.lifr_name, link, sizeof( lifr.lifr_name ) );

		// Retrieve netmask.

		if ( ioctl( s, SIOCGLIFNETMASK, &lifr ) < 0 )
		{
			// TODO-DAWID: error handling
		}
		else
		{
			struct in_addr* addr = &( ( ( struct sockaddr_in* ) &lifr.lifr_addr )->sin_addr );

			// Convert netmask to string.

			if ( NULL == inet_ntop( AF_INET, addr, netmask, netmask_len ) )
			{
				// TODO-DAWID: error handling
				
				*netmask = '\0';
			}
		}

		close( s );
	}

	return netmask;
}


int plumb( char* link )
{
	ifconfig_plumb( link );
}


int is_plumbed( char* link )
{
	int s;
	int plumbed = 0;

	// Try to create socket.

	if ( -1 == (( s = socket( AF_INET, SOCK_DGRAM, 0 ) )) )
	{
		// TODO-DAWID: error handling
	}
	else
	{
		struct lifreq lifr = { 0 };
		strncpy( lifr.lifr_name, link, sizeof( lifr.lifr_name ) );

		plumbed = ( ioctl( s, SIOCGLIFNETMASK, &lifr ) >= 0 );

		close( s );
	}

	return plumbed;
}

int set_ip_address(char *link, char *address)
{
	int s;
	struct lifreq lifr = { 0 };

	strncpy( lifr.lifr_name, link, sizeof( lifr.lifr_name ) - 1 );
	inet_aton( address, &( ( struct sockaddr_in* ) &lifr.lifr_addr )->sin_addr );
	lifr.lifr_addr.ss_family = AF_INET;

	s = socket(AF_INET, SOCK_DGRAM, 0);

	if (s == -1){
		//couldn't create a socket
		return XBOW_STATUS_OPERATION_FAILURE;
	}

	if (ioctl(s, SIOCSLIFADDR, (caddr_t)&lifr) < 0) {
		//couldn't set the ip address
		return XBOW_STATUS_OPERATION_FAILURE;		
	}

	return XBOW_STATUS_OK;
}

char* get_ip_address(char *link)
{

	int s;
	struct lifreq lifr;
	struct sockaddr_in	*sin;

	(void) strncpy(lifr.lifr_name, link, strlen(link)+1);

	s = socket(AF_INET, SOCK_DGRAM, 0);

	if (s == -1){
		//couldn't create socket
		return NULL;
	}

	if (ioctl(s, SIOCGLIFADDR, (caddr_t)&lifr) < 0) {
		//couldn't read ip address value
		return NULL;
		
	}	

	sin = (struct sockaddr_in *)&lifr.lifr_addr;

	if(sin != NULL){
		return inet_ntoa(sin->sin_addr);
	}else{
		return NULL;
	}
}

