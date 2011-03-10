
package org.jims.modules.crossbow.objectmodel.policy;

import java.io.Serializable;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.filters.Filter;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

/**
 * Class describing QoS policy
 *
 * @author robert boczek
 */
public abstract class Policy implements Serializable {

    public Policy() {
    }

		public Policy( String name, Filter filter ) {
			this.name = name;
			this.filter = filter;
		}

    /**
     * Get the value of filtersList
     *
     * @return the value of filtersList
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Set the value of filtersList
     *
     * @param filtersList new value of filtersList
     */
    public void setFilter( Filter filter ) {
        this.filter = filter;
    }


		public void setPort( Interface port ) {
			this.port = port;
		}

		public Interface getPort() {
			return port;
		}

		public String getName() {
			return name;
		}


		private String name;
		private Interface port;
		private Filter filter;

}
