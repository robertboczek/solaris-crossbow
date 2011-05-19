package org.jims.modules.crossbow.objectmodel.filters.address;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * IpAddress class describing address
 *
 * @author robert boczek
 */
public class IpAddress implements Serializable {

    private String address;
    private int netmask;

    public IpAddress(String address, int netmask) {
        this.address = address;
        this.netmask = netmask;
    }

		public static IpAddress fromString( String s ) {

			Matcher m = addrPattern.matcher( s );

			if ( m.matches() ) {
				int mask = ( null == m.group( 2 ) ) ? 32 : Integer.parseInt( m.group( 2 ) );
				return new IpAddress( m.group( 1 ), mask );
			}

			return null;

		}


    /**
     * Get the value of netmask
     *
     * @return the value of netmask
     */
    public int getNetmask() {
        return netmask;
    }

    /**
     * Set the value of netmask
     *
     * @param netmask new value of netmask
     */
    public void setNetmask(int netmask) {
        this.netmask = netmask;
    }

    /**
     * Get the value of address
     *
     * @return the value of address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the value of address
     *
     * @param address new value of address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns String representation of address
     *
     * @return String representation of address
     */
		@Override
    public String toString() {
	return address + "/" + netmask;
    }
	

	@Override
	public boolean equals( Object o ) {

		if ( this == o ) {

			return true;

		} else if ( o instanceof IpAddress ) {

			IpAddress addr = ( IpAddress ) o;
			return this.address.equals( addr.address ) && ( this.netmask == addr.netmask );
		
		} else {

			return false;

		}
	
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 29 * hash + (this.address != null ? this.address.hashCode() : 0);
		hash = 29 * hash + this.netmask;
		return hash;
	}


	private static Pattern addrPattern
		= Pattern.compile( "((?:\\d{1,3}\\.){3}\\d{1,3})(?:/(\\d{1,2}))?" );

}
