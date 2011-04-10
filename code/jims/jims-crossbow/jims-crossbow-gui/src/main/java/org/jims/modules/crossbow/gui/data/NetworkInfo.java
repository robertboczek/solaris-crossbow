package org.jims.modules.crossbow.gui.data;

import java.io.Serializable;

import org.jims.modules.crossbow.objectmodel.ObjectModel;

public class NetworkInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2660415260913198032L;
	
	private ObjectModel objectModel;
	private String address;
	private String port;
	private String projectId;
	
	public NetworkInfo(ObjectModel objectModel, String address, String port,
			String projectId) {
		
		this.objectModel = objectModel;
		this.address = address;
		this.port = port;
		this.projectId = projectId;
	}

	public ObjectModel getObjectModel() {
		return objectModel;
	}

	public String getAddress() {
		return address;
	}

	public String getPort() {
		return port;
	}

	public String getProjectId() {
		return projectId;
	}
	

}
