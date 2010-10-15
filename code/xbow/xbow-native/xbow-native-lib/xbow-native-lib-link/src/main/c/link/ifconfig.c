#include <sys/socket.h>
#include <sys/types.h>
#include <sys/sockio.h>
#include <stdio.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>
#include <netdb.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <libdlpi.h>
#include <stropts.h>
#include <sys/sockio.h>
#include <libinetutil.h>
#include <stdio.h>
#include <unistd.h>

#include <common/defs.h>


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


int ifplumb( const char* ifname, int af )
{
	int ip_muxid;
	uint_t dlpi_flags;
	dlpi_handle_t dh_ip;
	char* linkname = ifname;

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
											if ( -1 != ioctl( arp_fd, I_STR, &( struct strioctl ){ .ic_cmd = SIOCSLIFNAME, .ic_len = sizeof( lifr ), .ic_dp = &lifr } ) )
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
												}
												else
												{
													rc = XBOW_STATUS_OK;
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

	return rc;
}

