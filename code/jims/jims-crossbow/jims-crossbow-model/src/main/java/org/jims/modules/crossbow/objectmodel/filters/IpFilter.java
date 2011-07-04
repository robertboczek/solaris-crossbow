package org.jims.modules.crossbow.objectmodel.filters;

import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;

/**
 * IpFilter filter
 * @author robert boczek
 */
public class IpFilter extends Filter{

	public enum Location {
		LOCAL,
		REMOTE
	}

    public IpFilter(IpAddress address, Location location) {
        this.address = address;
        this.location = location;
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

    public IpAddress getAddress() {
        return address;
    }

    public void setAddress(IpAddress ipAddress) {
        this.address = ipAddress;
    }

		private Location location;
		private IpAddress address;

}
