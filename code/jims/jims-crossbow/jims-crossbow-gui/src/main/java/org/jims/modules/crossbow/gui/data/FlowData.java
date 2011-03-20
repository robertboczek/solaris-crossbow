package org.jims.modules.crossbow.gui.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Class describing flow
 * 
 * @author robert
 *
 */
public class FlowData {
	
	private Set<Transport> transportList = new HashSet<Transport>();
	private String flowName;
	private String bandwidth, priority;
	private String localPort, remotePort;
	

	public enum Transport{
		TCP, UDP
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public Set<Transport> getTransportSet() {
		return transportList;
	}

	public void addTransport(Transport transport){
		this.transportList.add(transport);
	}
	
	public void removeTransport(Transport transport){
		this.transportList.remove(transport);
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public String getLocalPort() {
		return localPort;
	}

	public void setLocalPort(String localPort) {
		this.localPort = localPort;
	}

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

	public void setBandwidth(String bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getFlowName() {
		return flowName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((flowName == null) ? 0 : flowName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FlowData other = (FlowData) obj;
		if (flowName == null) {
			if (other.flowName != null)
				return false;
		} else if (!flowName.equals(other.flowName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FlowData [flowName=" + flowName + "]";
	}
}
