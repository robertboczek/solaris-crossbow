package org.jims.modules.crossbow.objectmodel.resources;

import java.util.LinkedList;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;


/**
 * Port endpoint object
 * 
 * @author robert boczek
 */
public class Port extends Endpoint{

    public Port(String resourceId, String projectId) {
        super(resourceId, projectId);
    }

    public Port(String resourceId, String projectId,
		            Endpoint endpoint,
		            List<Policy> policiesList, IpAddress ipAddress) {
        this(resourceId, projectId);
        this.policiesList = policiesList;
				this.endpoint = endpoint;
    }

    public List<Node> getNodesList() {
        return nodesList;
    }

    public void setNodesList(List<Node> nodesList) {
        this.nodesList = nodesList;
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
			policy.setPort( this );
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
			endpoint.update( this );
		}

		@Override
		public void update( Endpoint e ) {
			this.endpoint = e;
		}


		private List< Policy  > policiesList = new LinkedList< Policy >();
		private List<Node> nodesList;
		private IpAddress ipAddress;
		private Endpoint endpoint;

}
