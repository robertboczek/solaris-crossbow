package agh.msc.xbowbase.flow;

import java.util.List;


/**
 *
 * @author cieplik
 */
public interface FlowManagerMBean {

	public List< String > getFlows();

	public void discover();

	public void create( FlowMBean flow );

	public void remove( String flowName, boolean temporary );

}
