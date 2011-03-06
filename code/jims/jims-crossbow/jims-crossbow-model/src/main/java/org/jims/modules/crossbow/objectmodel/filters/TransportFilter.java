package org.jims.modules.crossbow.objectmodel.filters;


/**
 * Transport filter.
 * Possible transport protocols: tcp, udp, sctp, icmp, icmpv6
 *
 * @author cieplik
 */
public class TransportFilter extends Filter {

	public TransportFilter( String protocol ) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}


	private String protocol;

}
