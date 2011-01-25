package org.jims.modules.crossbow.gui.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Graph node data containg data about switch
 * 
 * @author robert
 *
 */
public class SwitchGraphNodeData extends GraphNodeData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3202092016063876213L;
	
	List<GraphConnectionData> listOfGraphConnections;
	
	public SwitchGraphNodeData() {
		listOfGraphConnections = new LinkedList<GraphConnectionData>();
	}

	public List<GraphConnectionData> getListOfGraphConnections() {
		return listOfGraphConnections;
	}

	public void setListOfGraphConnections(
			List<GraphConnectionData> listOfGraphConnections) {
		this.listOfGraphConnections = listOfGraphConnections;
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
