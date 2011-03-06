package org.jims.modules.crossbow.gui.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

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
	
	protected String repoId, resourceId;
	protected List<IpAddress> interfaces = new LinkedList<IpAddress>();

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
	
	public void addNewIpAddress(){
		this.interfaces.add(new IpAddress());
	}

	

	public List<IpAddress> getInterfaces() {
		return interfaces;
	}
	
	public IpAddress findIpAddress(GraphConnectionData graphConnectionData){
		for(IpAddress ipAddress : this.interfaces){
			if(ipAddress.getGraphConnectionData() != null && ipAddress.getGraphConnectionData().equals(graphConnectionData)){
				return ipAddress;
			}
		}
		return null;
	}

	/**
	 * Removes graphConnectionData object as the connection between Nodes 
	 * 
	 * @param graphConnectionData
	 * @return 
	 */
	public void removeEndpoint(GraphConnectionData graphConnectionData){
		
		IpAddress ipAddress = null;
		while((ipAddress = findIpAddress(graphConnectionData)) != null){
			ipAddress.setGraphConnectionData(null);
		}
	}
	
	/**
	 * Adds graphConnectionData object as the connection between Nodes 
	 * 
	 * @param graphConnectionData
	 */
	public void addEndpoint(GraphConnectionData graphConnectionData){
		
	}
}
