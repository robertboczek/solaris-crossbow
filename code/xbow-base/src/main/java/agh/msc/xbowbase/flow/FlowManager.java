package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.lib.Flowadm;
import java.util.Arrays;
import java.util.List;
import javax.management.Notification;
import javax.management.NotificationListener;


/**
 *
 * @author cieplik
 */
public class FlowManager implements FlowManagerMBean, NotificationListener {

	@Override
	public List< String > getFlows() {
		return Arrays.asList( flowadm.getNames() );
	}


	@Override
	public void discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public void create(FlowMBean flow) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public void remove( String flowName, boolean temporary ) {
		flowadm.remove( flowName );
	}


	@Override
	public void handleNotification( Notification ntfctn, Object o ) {

		System.out.println( "GOT NOTIFICATION" );

	}


	public void setFlowadm( Flowadm flowadm ) {
		this.flowadm = flowadm;
	}


	Flowadm flowadm = null;

}
