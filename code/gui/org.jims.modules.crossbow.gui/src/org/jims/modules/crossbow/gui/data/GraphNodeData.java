package org.jims.modules.crossbow.gui.data;

import java.io.Serializable;

/**
 * Abstract class containing data typed in dialogs
 * 
 * @author robert
 *
 */
public abstract class GraphNodeData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4432277981205294737L;
	
	protected String resourceId;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	/**
	 * Removes graphConnectionData object as the connection between Nodes 
	 * 
	 * @param graphConnectionData
	 * @return 
	 */
	public abstract void removeEndpoint(GraphConnectionData graphConnectionData);
}
