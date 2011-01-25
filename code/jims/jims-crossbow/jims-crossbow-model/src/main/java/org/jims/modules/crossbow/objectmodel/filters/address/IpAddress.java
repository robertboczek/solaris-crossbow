package org.jims.modules.crossbow.objectmodel.filters.address;

import java.io.Serializable;


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

}
