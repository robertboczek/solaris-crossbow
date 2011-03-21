package org.jims.modules.crossbow.objectmodel.filters;


/**
 * Transport filter.
 * Possible transport protocols: tcp, udp, sctp, icmp, icmpv6
 *
 * @author cieplik
 */
public class TransportFilter extends Filter {

	public enum Transport {
		TCP,
		UDP,
		SCTP,
		ICMP,
		ICMPV6
	}


	public TransportFilter( Transport transport ) {
		this.transport = transport;
	}

	public Transport getTransport() {
		return transport;
	}

	public void setTransport( Transport transport ) {
		this.transport = transport;
	}


	private Transport transport;

}
