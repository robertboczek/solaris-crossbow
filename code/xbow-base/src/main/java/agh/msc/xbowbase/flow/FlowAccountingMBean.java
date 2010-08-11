package agh.msc.xbowbase.flow;

import java.util.Date;
import java.util.List;


/**
 *
 * @author cieplik
 */
public interface FlowAccountingMBean {

	public boolean isAccountingEnabled();

	public void enableAccounting();

	public void disableAccounting();

	public FlowUsage getUsage( String flowName );

	public List< FlowUsage > getUsage( String flowName, Date start, Date end );

}
