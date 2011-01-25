package org.jims.modules.crossbow.gui.data;

/**
 * Graph node data containg data about resource
 * 
 * @author robert
 *
 */
public class ResourceGraphNodeData extends GraphNodeData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5782297244472705784L;
	
	private GraphConnectionData graphConnectionData;

	public GraphConnectionData getGraphConnectionData() {
		return graphConnectionData;
	}

	public void setGraphConnectionData(GraphConnectionData graphConnectionData) {
		this.graphConnectionData = graphConnectionData;
	}

	@Override
	public void removeEndpoint(GraphConnectionData graphConnectionData) {
		graphConnectionData = null;
		
	}

	@Override
	public void addEndpoing(GraphConnectionData graphConnectionData) {
		this.graphConnectionData = graphConnectionData;
		
	}
}
