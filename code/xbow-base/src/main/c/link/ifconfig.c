#include <sys/socket.h>
#include <sys/types.h>
#include <sys/sockio.h>
#include <sys/errno.h>
#include <errno.h>
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
#include <sys/errno.h>
#include <errno.h>
#include <libinetutil.h>
#include <stdio.h>
#include <unistd.h>


/*
 * plumbing
 */

#define ARP_MOD_NAME "arp"


static int
open_arp_on_udp(char *udp_dev_name)
{
	int fd;
	int a;

	if ((fd = open(udp_dev_name, O_RDWR)) == -1) {
	printf( "open\n" );
		// Perror2("open", udp_dev_name);
		return (-1);
	}
	errno = 0;
	while ( (( a = ioctl(fd, I_POP, 0) )) != -1)
		;
	if ( (errno != EINVAL) && ( errno != 0 ) ) {
	printf( "pop %d, %d\n", errno, a );
		// Perror2("pop", udp_dev_name);
	} else if (ioctl(fd, I_PUSH, ARP_MOD_NAME) == -1) {
	printf( "arp PUSH\n" );

		// Perror2("arp PUSH", udp_dev_name);
	} else {
		return (fd);
	}
	(void) close(fd);
	printf( "end\n" );

	return (-1);
}


static int
strioctl(int s, int cmd, void *buf, int buflen)
{
	struct strioctl ioc;

	(void) memset(&ioc, 0, sizeof (ioc));
	ioc.ic_cmd = cmd;
	ioc.ic_timout = 0;
	ioc.ic_len = buflen;
	ioc.ic_dp = buf;
	return (ioctl(s, I_STR, (char *)&ioc));
}



int ifconfig_plumb( char* link )
{
	char* ifname = link;

	int	arp_muxid = -1, ip_muxid;
	int	mux_fd, ip_fd, arp_fd;
	int 	retval;
	char	*udp_dev_name;
	uint64_t flags;
	uint_t	dlpi_flags;
	dlpi_handle_t	dh_arp, dh_ip;

	struct lifreq lifr = { 0 };

	/*
	 * Always dlpi_open() with DLPI_NOATTACH because the IP and ARP module
	 * will do the attach themselves for DLPI style-2 links.
	 */
	dlpi_flags = DLPI_NOATTACH;

	retval = dlpi_open(link, &dh_ip, dlpi_flags);

	#if 0
	if (retval != DLPI_SUCCESS)
		Perrdlpi_exit("cannot open link", link, retval);
		#endif

	#define IP_MOD_NAME "ip"

	ip_fd = dlpi_fd(dh_ip);
	if (ioctl(ip_fd, I_PUSH, IP_MOD_NAME) == -1)
		Perror2_exit("I_PUSH", IP_MOD_NAME);

	/*
	 * Prepare to set IFF_IPV4/IFF_IPV6 flags as part of SIOCSLIFNAME.
	 * (At this point in time the kernel also allows an override of the
	 * IFF_CANTCHANGE flags.)
	 */
	lifr.lifr_name[0] = '\0';
	if (ioctl(ip_fd, SIOCGLIFFLAGS, (char *)&lifr) == -1)
		Perror0_exit("ifplumb: SIOCGLIFFLAGS");

		flags = lifr.lifr_flags | IFF_IPV4;
		flags &= ~IFF_IPV6;

	/*
	 * Set the interface name.  If we've been asked to generate the PPA,
	 * then find the lowest available PPA (only currently used for IPMP
	 * interfaces).  Otherwise, use the interface name as-is.
	 */
		ifspec_t ifsp;

		/*
		 * The interface name could have come from the command-line;
		 * check it.
		 */
		if (!ifparse_ifspec(ifname, &ifsp) || ifsp.ifsp_lunvalid)
			Perror2_exit("invalid IP interface name", ifname);

		/*
		 * Before we call SIOCSLIFNAME, ensure that the IPMP group
		 * interface for this address family exists.  Otherwise, the
		 * kernel will kick the interface out of the group when we do
		 * the SIOCSLIFNAME.
		 *
		 * Example: suppose bge0 is plumbed for IPv4 and in group "a".
		 * If we're now plumbing bge0 for IPv6, but the IPMP group
		 * interface for "a" is not plumbed for IPv6, the SIOCSLIFNAME
		 * will kick bge0 out of group "a", which is undesired.
		 */
		#if 0
		// TODO-DAWID:
		if (create_ipmp_peer(AF_INET, ifname) == -1) {
			(void) fprintf(stderr, "ifconfig: warning: cannot "
			    "create %s IPMP group; %s will be removed from "
			    "group\n", "IPv4", ifname);
		}
		#endif

		lifr.lifr_ppa = ifsp.ifsp_ppa;
		lifr.lifr_flags = flags;
		(void) strlcpy(lifr.lifr_name, ifname, LIFNAMSIZ);
		retval = ioctl(ip_fd, SIOCSLIFNAME, &lifr);

	if (retval == -1) {
		if (errno != EEXIST)
			Perror0_exit("SIOCSLIFNAME for ip");
		/*
		 * This difference between the way we behave for EEXIST
		 * and that with other errors exists to preserve legacy
		 * behaviour. Earlier when foreachinterface() and matchif()
		 * were doing the duplicate interface name checks, for
		 * already existing interfaces, inetplumb() returned "0".
		 * To preserve this behaviour, Perror0() and return are
		 * called for EEXIST.
		 */
		Perror0("SIOCSLIFNAME for ip");
		return (-1);
	}

	/* Get the full set of existing flags for this stream */
	if (ioctl(ip_fd, SIOCGLIFFLAGS, (char *)&lifr) == -1)
		Perror0_exit("ifplumb: SIOCGLIFFLAGS");

	/*
	 * Open "/dev/udp" for use as a multiplexor to PLINK the
	 * interface stream under. We use "/dev/udp" instead of "/dev/ip"
	 * since STREAMS will not let you PLINK a driver under itself,
	 * and "/dev/ip" is typically the driver at the bottom of
	 * the stream for tunneling interfaces.
	 */

	#define UDP_DEV_NAME "udp"

	udp_dev_name = UDP_DEV_NAME;

	if ((mux_fd = open_arp_on_udp(udp_dev_name)) == -1)
		exit(EXIT_FAILURE);

	/* Check if arp is not needed */
	if (lifr.lifr_flags & (IFF_NOARP|IFF_IPV6)) {
		/*
		 * PLINK the interface stream so that ifconfig can exit
		 * without tearing down the stream.
		 */
		if ((ip_muxid = ioctl(mux_fd, I_PLINK, ip_fd)) == -1)
			Perror0_exit("I_PLINK for ip");
		(void) close(mux_fd);
		return (lifr.lifr_ppa);
	}

	/*
	 * This interface does use ARP, so set up a separate stream
	 * from the interface to ARP.
	 */

	retval = dlpi_open(link, &dh_arp, dlpi_flags);
	if (retval != DLPI_SUCCESS)
		Perrdlpi_exit("cannot open link", link, retval);

	arp_fd = dlpi_fd(dh_arp);
	if (ioctl(arp_fd, I_PUSH, ARP_MOD_NAME) == -1)
		Perror2_exit("I_PUSH", ARP_MOD_NAME);

	/*
	 * Tell ARP the name and unit number for this interface.
	 * Note that arp has no support for transparent ioctls.
	 */
	if (strioctl(arp_fd, SIOCSLIFNAME, &lifr, sizeof (lifr)) == -1) {
		if (errno != EEXIST)
			Perror0_exit("SIOCSLIFNAME for arp");
		Perror0("SIOCSLIFNAME for arp");
		goto out;
	}

	/*
	 * PLINK the IP and ARP streams so that ifconfig can exit
	 * without tearing down the stream.
	 */
	if ((ip_muxid = ioctl(mux_fd, I_PLINK, ip_fd)) == -1)
		Perror0_exit("I_PLINK for ip");
	if ((arp_muxid = ioctl(mux_fd, I_PLINK, arp_fd)) == -1) {
		(void) ioctl(mux_fd, I_PUNLINK, ip_muxid);
		Perror0_exit("I_PLINK for arp");
	}

out:
	dlpi_close(dh_ip);
	dlpi_close(dh_arp);
	(void) close(mux_fd);
	return (lifr.lifr_ppa);
}

