package org.jims.modules.crossbow.objectmodel.filters;

/**
 * Filter filtring traffic by port number
 *
 * @author robert boczek
 */
public class PortFilter extends Filter{

    private Protocol protocol;
    private int port;
    protected Location location;

    public PortFilter(Protocol protocol, int port, Location location) {
        this.protocol = protocol;
        this.port = port;
        this.location = location;
    }

    public enum Protocol{
        TCP, UDP, SCTP;
    }

    /**
     * Get the value of location
     *
     * @return the value of location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param location new value of location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the value of port
     *
     * @return the value of port
     */
    public int getPort() {
        return port;
    }


		public String getProtocolAsString() {

			switch ( protocol ) {

				case SCTP: return "sctp";
				case TCP:  return "tcp";
				default:   return "udp";

			}

		}

    /**
     * Set the value of port
     *
     * @param port new value of port
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Get the value of protocol
     *
     * @return the value of protocol
     */
    public Protocol getProtocol() {
        return protocol;
    }

    /**
     * Set the value of protocol
     *
     * @param protocol new value of protocol
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

}
