
package org.jims.modules.crossbow.objectmodel.policy;

/**
 * Policy specifing bandwidth
 *
 * @author robert boczek
 */
public class BandwidthPolicy extends Policy{

    private int limit;

    public BandwidthPolicy(int limit) {
        this.limit = limit;
    }

    /**
     * Get the value of limit
     *
     * @return the value of limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the value of limit
     *
     * @param limit new value of limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }


}
