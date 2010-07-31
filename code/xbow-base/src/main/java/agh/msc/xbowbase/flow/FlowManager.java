package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.flow.util.FlowToFlowInfoTranslator;
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

		if ( publisher != null ) {

			for ( FlowInfo flowInfo : flowadm.getFlowsInfo() ) {

				System.out.println( flowInfo.name );

				Flow flow = FlowToFlowInfoTranslator.toFlow( flowInfo );
				flow.setFlowadm( flowadm );

				publisher.publish( flow );

			}

		}

	}


	@Override
	public void create( FlowMBean flow ) {

		flowadm.create( FlowToFlowInfoTranslator.toFlowInfo( ( Flow ) flow ) );
		publisher.publish( flow );

	}


	@Override
	public void remove( String flowName, boolean temporary ) {
		flowadm.remove( flowName );
	}


	@Override
	public void handleNotification( Notification ntfctn, Object o ) {
		discover();
	}


	public void setFlowadm( Flowadm flowadm ) {
		this.flowadm = flowadm;
	}


	public void setPublisher( Publisher publisher ) {
		this.publisher = publisher;
	}


	private Flowadm flowadm = null;
	private Publisher publisher = null;

}
