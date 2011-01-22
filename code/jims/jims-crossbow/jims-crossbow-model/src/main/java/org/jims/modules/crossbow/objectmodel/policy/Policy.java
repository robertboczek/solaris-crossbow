
package org.jims.modules.crossbow.objectmodel.policy;

import java.io.Serializable;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.filters.Filter;

/**
 * Class describing QOS policy
 *
 * @author robert boczek
 */
public abstract class Policy implements Serializable{

    private List<Filter> filtersList;

    public Policy(List<Filter> filtersList) {
        this.filtersList = filtersList;
    }

    public Policy() {
    }

    /**
     * Get the value of filtersList
     *
     * @return the value of filtersList
     */
    public List<Filter> getFiltersList() {
        return filtersList;
    }

    /**
     * Set the value of filtersList
     *
     * @param filtersList new value of filtersList
     */
    public void setFiltersList(List<Filter> filtersList) {
        this.filtersList = filtersList;
    }

}
