package org.jims.modules.crossbow.gui.data;

import java.io.Serializable;

import org.jims.modules.crossbow.gui.statistics.StatisticAnalyzer.EndpointStatistic;
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;

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

	private Endpoint endp2;
	private Endpoint endp1;

	private EndpointStatistic statistic1;
	private EndpointStatistic statistic2;

	private String text;

	public GraphConnectionData(Object leftNode,
			Object rightNode, Endpoint endp1, Endpoint endp2) {
		this.leftNode = leftNode;
		this.rightNode = rightNode;
		
		this.endp1 = endp1;
		this.endp2 = endp2;
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
	
	public Endpoint getEndp2() {
		return endp2;
	}

	public Endpoint getEndp1() {
		return endp1;
	}

	/*public Object getSecondEndpoint(Object object){
		if(object == rightNode)
			return leftNode;
		else if(object == leftNode)
			return rightNode;
		return null;
	}*/
	
	public void setEndp1Statistic(EndpointStatistic statistic) {
		this.statistic1 = statistic;
	}
	
	public void setEndp2Statistic(EndpointStatistic statistic) {
		this.statistic2 = statistic;
	}

	public EndpointStatistic getStatistic1() {
		return statistic1;
	}

	public void setStatistic1(EndpointStatistic statistic1) {
		this.statistic1 = statistic1;
	}

	public EndpointStatistic getStatistic2() {
		return statistic2;
	}

	public void setStatistic2(EndpointStatistic statistic2) {
		this.statistic2 = statistic2;
	}

	public void setToolTip(String string) {
		this.text = string;
	}

	@Override
	public String toString() {
		return text;
	}
	
	
}
