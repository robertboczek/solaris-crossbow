package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.util.FlowToFlowInfoTranslator;
import agh.msc.xbowbase.lib.Flowadm;
import java.util.LinkedList;
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

		List< String > names = new LinkedList< String >();
		List< FlowInfo > flowsInfo = flowadm.getFlowsInfo();

		for ( FlowInfo flowInfo : flowsInfo ) {
			names.add( flowInfo.getName() );
		}

		return names;

	}


	@Override
	public void discover() {

		if ( publisher != null ) {

			for ( FlowInfo flowInfo : flowadm.getFlowsInfo() ) {

				Flow flow = FlowToFlowInfoTranslator.toFlow( flowInfo );
				flow.setFlowadm( flowadm );

				publisher.publish( flow );

			}

		}

	}


	@Override
	public void create( FlowMBean flow ) throws XbowException {

		flowadm.create( FlowToFlowInfoTranslator.toFlowInfo( ( Flow ) flow ) );
		publisher.publish( flow );

	}


	@Override
	public void remove( String flowName, boolean temporary ) throws XbowException {

		flowadm.remove( flowName, temporary );
		publisher.unpublish( flowName );
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
