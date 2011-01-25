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
	
	protected String ipAddress, netmask, repoId, resourceId;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getNetmask() {
		return netmask;
	}

	public void setNetmask(String netmask) {
		this.netmask = netmask;
	}

	public String getRepoId() {
		return repoId;
	}

	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}

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
	 */
	public abstract void removeEndpoint(GraphConnectionData graphConnectionData);
	
	/**
	 * Adds graphConnectionData object as the connection between Nodes 
	 * 
	 * @param graphConnectionData
	 */
	public abstract void addEndpoing(GraphConnectionData graphConnectionData);
}
