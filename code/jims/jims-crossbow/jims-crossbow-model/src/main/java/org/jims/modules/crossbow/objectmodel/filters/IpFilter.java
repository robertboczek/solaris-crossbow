
package org.jims.modules.crossbow.objectmodel.filters;

import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;

/**
 * IpFilter filter
 * @author robert boczek
 */
public class IpFilter extends Filter{

   
    private String location;
    private final IpAddress address;

    public IpFilter(IpAddress address, String location) {
        this.address = address;
        this.location = location;
    }

    /**
     * Get the value of location
     *
     * @return the value of location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the value of location
     *
     * @param location new value of location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public IpAddress getAddress() {
        return address;
    }
}
