package org.jims.modules.crossbow.objectmodel.resources;

import java.util.LinkedList;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;


/**
 * Interface endpoint object
 * 
 * @author robert boczek
 */
public class Interface extends Endpoint {

    public Interface(String resourceId, String projectId) {
        super(resourceId, projectId);
    }

    public Interface(String resourceId, String projectId,
		            Endpoint endpoint,
		            List<Policy> policiesList, IpAddress ipAddress) {
        this(resourceId, projectId);
        this.policiesList = policiesList;
				this.endpoint = endpoint;
				this.ipAddress = ipAddress;
    }

	public Interface( String resourceId, String projectId, Endpoint endpoint, IpAddress ipAddress ) {
		this( resourceId, projectId, endpoint, new LinkedList< Policy >(), ipAddress );
	}


    /**
     * Get the value of policiesList
     *
     * @return the value of policiesList
     */
    public List<Policy> getPoliciesList() {
        return policiesList;
    }

    /**
     * Set the value of policiesList
     *
     * @param policiesList new value of policiesList
     */
    public void setPoliciesList(List<Policy> policiesList) {
        this.policiesList = policiesList;
    }


		public void addPolicy( Policy policy ) {
			policiesList.add( policy );
			policy.setInterface( this );
		}


    public void setIpAddress(IpAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public IpAddress getIpAddress() {
        return ipAddress;
    }

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(Endpoint endpoint) {
		this.endpoint = endpoint;
		if(endpoint != null)
			endpoint.update( this );
	}

	public Appliance getAppliance() {
		return appliance;
	}

	public void setAppliance( Appliance appliance ) {

		this.appliance = appliance;

		// TODO-DAWID v uncomment

		// if ( ( null != appliance ) && ( ! appliance.getInterfaces().contains( this ) ) ) {
		// 	appliance.addInterface( this );
		// }

	}

		@Override
		public void update( Endpoint e ) {
			this.endpoint = e;
		}

	@Override
	public boolean equals( Object o ) {

		if ( this == o ) {
			return true;
		} else if ( o instanceof Interface ) {
			return this.getUUID().equals( ( ( Interface ) o ).getUUID() );
		}

		return false;

	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.ipAddress != null ? this.ipAddress.hashCode() : 0);
		hash = 97 * hash + (this.appliance != null ? this.appliance.hashCode() : 0);
		return hash;
	}


		private List< Policy  > policiesList = new LinkedList< Policy >();
		private IpAddress ipAddress;
		private Endpoint endpoint;
		private Appliance appliance;

}
