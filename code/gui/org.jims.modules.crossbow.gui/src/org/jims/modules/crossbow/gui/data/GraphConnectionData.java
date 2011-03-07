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
	
	private Object leftNode;
	private Object rightNode;

	public GraphConnectionData(Object leftNode,
			Object rightNode) {
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	public Object getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Object leftNode) {
		this.leftNode = leftNode;
	}

	public Object getRightNode() {
		return rightNode;
	}

	public void setRightNode(Object rightNode) {
		this.rightNode = rightNode;
	}
	
	public Object getSecondEndpoint(Object object){
		if(object == rightNode)
			return leftNode;
		else if(object == leftNode)
			return rightNode;
		return null;
	}
}
