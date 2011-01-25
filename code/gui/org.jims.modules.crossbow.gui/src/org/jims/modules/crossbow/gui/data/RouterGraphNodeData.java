package org.jims.modules.crossbow.gui.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Graph node data containg data about router
 * 
 * @author robert
 *
 */
public class RouterGraphNodeData extends GraphNodeData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -588473040483666099L;
	
	List<GraphConnectionData> listOfGraphConnections;
	
	public RouterGraphNodeData() {
		listOfGraphConnections = new LinkedList<GraphConnectionData>();
	}

	public List<GraphConnectionData> getListOfGraphConnections() {
		return listOfGraphConnections;
	}

	@Override
	public void removeEndpoint(GraphConnectionData graphConnectionData) {
		listOfGraphConnections.remove(graphConnectionData);		
	}

	@Override
	public void addEndpoing(GraphConnectionData graphConnectionData) {
		this.listOfGraphConnections.add(graphConnectionData);		
	}
}
