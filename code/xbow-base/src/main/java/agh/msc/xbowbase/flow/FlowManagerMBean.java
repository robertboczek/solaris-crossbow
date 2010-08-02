package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.XbowException;
import java.util.List;


/**
 *
 * @author cieplik
 */
public interface FlowManagerMBean {

	public List< String > getFlows();

	public void discover();

	public void create( FlowMBean flow ) throws XbowException;

	public void remove( String flowName, boolean temporary ) throws XbowException;

}
