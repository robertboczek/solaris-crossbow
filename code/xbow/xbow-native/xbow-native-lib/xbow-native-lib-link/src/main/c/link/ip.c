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

#include <link/ip.h>



#include <errno.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netdb.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <libdlpi.h>
#include <libinetutil.h>
#include <stdio.h>


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


int get_netmask( char* link, buffer_t* buffer )
{
	int s;
	int rc = XBOW_STATUS_OK;

	memset( buffer->buffer, '\0', buffer->len );

	// Try to create socket.

	if ( -1 == (( s = socket( AF_INET, SOCK_DGRAM, 0 ) )) )
	{
		rc = XBOW_STATUS_UNKNOWN_ERR;
	}
	else
	{
		struct lifreq lifr = { 0 };
		strncpy( lifr.lifr_name, link, sizeof( lifr.lifr_name ) );

		// Retrieve netmask.

		if ( ioctl( s, SIOCGLIFNETMASK, &lifr ) < 0 )
		{
			rc = XBOW_STATUS_IOCTL_ERR;
		}
		else
		{
			struct in_addr* addr = &( ( ( struct sockaddr_in* ) &lifr.lifr_addr )->sin_addr );

			// Convert netmask to string.

			if ( NULL == inet_ntop( AF_INET, addr, buffer->buffer, buffer->len ) )
			{
				rc = XBOW_STATUS_UNKNOWN_ERR;
			}
		}

		close( s );
	}

	return rc;
}


static int open_arp_on_udp( char* udp_dev_name )
{
	int fd, rc = -1;

	if ( -1 != (( fd = open( udp_dev_name, O_RDWR ) )) )
	{
		#if 0
		// We ignore errno value (problems with JNA).
		errno = 0;
		#endif

		while ( -1 != ioctl( fd, I_POP, 0 ) ) {}

		#if 0
		if ( EINVAL != errno ) {} else 
		#endif
		
		if ( -1 != ioctl( fd, I_PUSH, "arp" ) )
		{
			rc = fd;
		}
	}

	if ( -1 == rc )
	{
		close( fd );
	}

	return rc;
}


int plumb( char* ifname )
{
	int ip_muxid;
	uint_t dlpi_flags;
	dlpi_handle_t dh_ip;
	char* linkname = ifname;
	int af = AF_INET;

	int rc = XBOW_STATUS_IOCTL_ERR;

	/*
	 * Always dlpi_open() with DLPI_NOATTACH because the IP and ARP module
	 * will do the attach themselves for DLPI style-2 links.
	 */
	dlpi_flags = DLPI_NOATTACH;

	if ( DLPI_SUCCESS != dlpi_open( linkname, &dh_ip, dlpi_flags ) )
	{
		rc = XBOW_STATUS_DLPI_ERR;
	}
	else
	{
		int ip_fd = dlpi_fd( dh_ip );

		if ( -1 != ioctl( ip_fd, I_PUSH, "ip" ) )
		{
			struct lifreq lifr = { 0 };

			/*
			 * Prepare to set IFF_IPV4/IFF_IPV6 flags as part of SIOCSLIFNAME.
			 * (At this point in time the kernel also allows an override of the
			 * IFF_CANTCHANGE flags.)
			 */
			if ( -1 != ioctl( ip_fd, SIOCGLIFFLAGS, &lifr ) )
			{
				if ( AF_INET6 == af )
				{
					lifr.lifr_flags |= IFF_IPV6;
					lifr.lifr_flags &= ~( IFF_BROADCAST | IFF_IPV4 );
				}
				else
				{
					lifr.lifr_flags |= IFF_IPV4;
					lifr.lifr_flags &= ~IFF_IPV6;
				}

				ifspec_t ifsp;

				/*
				 * The interface name could have come from the command-line;
				 * check it.
				 */
				if ( ! ifparse_ifspec( ifname, &ifsp ) || ifsp.ifsp_lunvalid )
				{
					rc = XBOW_STATUS_INVALID_NAME;
				}
				else
				{
#if 0
					if (create_ipmp_peer(af, ifname) == -1) {
						// TODO-DAWID: tylko warning
						(void) fprintf(stderr, "ifconfig: warning: cannot "
								"create %s IPMP group; %s will be removed from "
								"group\n", af == AF_INET ? "IPv4" : "IPv6", ifname);
					}
#endif

					lifr.lifr_ppa = ifsp.ifsp_ppa;
					strncpy( lifr.lifr_name, ifname, LIFNAMSIZ );

					if ( -1 != ioctl( ip_fd, SIOCSLIFNAME, &lifr ) )
					{
						/* Get the full set of existing flags for this stream */
						if ( -1 != ioctl( ip_fd, SIOCGLIFFLAGS, &lifr ) )
						{
							/*
							 * Open "/dev/udp" for use as a multiplexor to PLINK the
							 * interface stream under. We use "/dev/udp" instead of "/dev/ip"
							 * since STREAMS will not let you PLINK a driver under itself,
							 * and "/dev/ip" is typically the driver at the bottom of
							 * the stream for tunneling interfaces.
							 */

							int mux_fd = open_arp_on_udp( ( AF_INET6 == af ) ? "/dev/udp6" : "/dev/udp" );

							if ( -1 != mux_fd )
							{
								/* Check if arp is not needed */
								if ( lifr.lifr_flags & ( IFF_NOARP | IFF_IPV6 ) )
								{
									/*
									 * PLINK the interface stream so that ifconfig can exit
									 * without tearing down the stream.
									 */
									if ( -1 != (( ip_muxid = ioctl( mux_fd, I_PLINK, ip_fd ) )) )
									{
										rc = XBOW_STATUS_OK;
									}
								}
								else
								{
									/*
									 * This interface does use ARP, so set up a separate stream
									 * from the interface to ARP.
									 */
									dlpi_handle_t	dh_arp;

									if ( DLPI_SUCCESS != dlpi_open( linkname, &dh_arp, dlpi_flags ) )
									{
										rc = XBOW_STATUS_DLPI_ERR;
									}
									else
									{
										int arp_fd = dlpi_fd(dh_arp);

										if ( -1 != ioctl( arp_fd, I_PUSH, "arp" ) )
										{
											/*
											 * Tell ARP the name and unit number for this interface.
											 * Note that arp has no support for transparent ioctls.
											 */
											struct strioctl str = { .ic_cmd = SIOCSLIFNAME,
											                        .ic_len = sizeof( lifr ),
											                        .ic_dp = ( char* ) &lifr };

											if ( -1 != ioctl( arp_fd, I_STR, &str ) )
											{
												/*
												 * PLINK the IP and ARP streams so that ifconfig can exit
												 * without tearing down the stream.
												 */
												if ( -1 != (( ip_muxid = ioctl( mux_fd, I_PLINK, ip_fd ) )) )
												{
													if ( -1 == ioctl( mux_fd, I_PLINK, arp_fd ) )
													{
														ioctl( mux_fd, I_PUNLINK, ip_muxid );
													}
													else
													{
														rc = XBOW_STATUS_OK;
													}
												}
											}
										}

										dlpi_close( dh_arp );
									}
								}

								close( mux_fd );
							}
						}
					}
				}
			}
		}

		dlpi_close( dh_ip );
	}

	printf( "plumb: rc == %d\n", rc );

	return rc;
}


int is_plumbed( char* link )
{
	int s, plumbed = 0;

	// Try to create socket.

	if ( -1 != (( s = socket( AF_INET, SOCK_DGRAM, 0 ) )) )
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

int ifconfig_up( char *link, int up_down )
{
	struct lifreq lifr = { 0 };	/* local lifreq struct */
	int s;

	s = socket(AF_INET, SOCK_DGRAM, 0);

	(void) strncpy(lifr.lifr_name, link, strlen(link)+1);
	if (ioctl(s, SIOCGLIFFLAGS, (caddr_t)&lifr) < 0)
		return XBOW_STATUS_FLOW_INCOMPATIBLE;


	if (up_down == 0) {
		lifr.lifr_flags = lifr.lifr_flags & (~IFF_UP);
	}
	else{
		lifr.lifr_flags |= IFF_UP;
	}

	if (ioctl(s, SIOCSLIFFLAGS, (caddr_t)&lifr) < 0)
		return XBOW_STATUS_OPERATION_FAILURE;

	return XBOW_STATUS_OK;
}

int ifconfig_is_up( char *link )
{
	struct lifreq lifr;	/* local lifreq struct */
	int s;

	s = socket(AF_INET, SOCK_DGRAM, 0);

	(void) strncpy(lifr.lifr_name, link, strlen(link)+1);
	if (ioctl(s, SIOCGLIFFLAGS, (caddr_t)&lifr) < 0)
		return XBOW_STATUS_OPERATION_FAILURE;;


	return (lifr.lifr_flags & IFF_UP) > 0 ? 1 : 0;
}
