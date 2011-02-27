package org.jims.modules.crossbow.gui.data;

import java.io.Serializable;

/**
 * Class containg data about connection between two graphNodes
 * 
 * @author robert
 *
 */
public class GraphConnectionData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2646151015857279432L;
	
	protected String bandwidth, priority;
	private GraphNodeData leftNode;
	private GraphNodeData rightNode;

	public GraphConnectionData(GraphNodeData graphNodeData,
			GraphNodeData graphNodeData2) {
		leftNode = graphNodeData;
		rightNode = graphNodeData2;
	}

	public GraphNodeData getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(GraphNodeData leftNode) {
		this.leftNode = leftNode;
	}

	public GraphNodeData getRightNode() {
		return rightNode;
	}

	public void setRightNode(GraphNodeData rightNode) {
		this.rightNode = rightNode;
	}

	public String getBandwidth() {
		return bandwidth;
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
	
	public GraphNodeData getSecondEndpoint(GraphNodeData graphNodeData){
		if(graphNodeData == rightNode)
			return leftNode;
		else if(graphNodeData == leftNode)
			return rightNode;
		return null;
	}
}
