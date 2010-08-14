package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.publisher.Publisher;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.util.FlowToFlowInfoTranslator;
import agh.msc.xbowbase.lib.Flowadm;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.LinkedList;
import java.util.List;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;


/**
 * The class implements FlowManagerMBean functionality.
 *
 * @author cieplik
 */
public class FlowManager implements FlowManagerMBean, NotificationListener {

	/**
	 * @see  FlowManagerMBean#getFlows()
	 */
	@Override
	public List< String > getFlows() {

		List< String > names = new LinkedList< String >();
		List< FlowInfo > flowsInfo = flowadm.getFlowsInfo();

		for ( FlowInfo flowInfo : flowsInfo ) {
			names.add( flowInfo.getName() );
		}

		logger.debug( names.size() + " flow(s) present." );

		return names;

	}


	/**
	 * Discovers flows present in the system and, if a publisher has been set,
	 * publishes each discovered flow.
	 *
	 * @see FlowManagerMBean#discover()
	 */
	@Override
	public void discover() {

		if ( publisher != null ) {

			List< FlowInfo > flowsInfo = flowadm.getFlowsInfo();

			logger.debug( flowsInfo.size() + " flow(s) discovered." );

			for ( FlowInfo flowInfo : flowsInfo ) {

				// Create new Flow object, initialize and register it.

				Flow flow = FlowToFlowInfoTranslator.toFlow( flowInfo );
				flow.setFlowadm( flowadm );

				publisher.publish( flow );

			}

		}

	}


	/**
	 * @see  FlowManagerMBean#create(agh.msc.xbowbase.flow.FlowMBean)
	 */
	@Override
	public void create( FlowMBean flow ) throws XbowException {

		// Create the flow.

		flowadm.create( FlowToFlowInfoTranslator.toFlowInfo( ( Flow ) flow ) );
		logger.info( flow.getName() + " flow created." );

		// Publish, if publisher set.

		if ( publisher != null ) {

			publisher.publish( flow );
			logger.info( flow.getName() + " flow published." );

		}

	}


	/**
	 * Removes flow from the system and unpublishes, if publisher exists.
	 *
	 * @see  FlowManagerMBean#remove(java.lang.String, boolean)
	 */
	@Override
	public void remove( String flowName, boolean temporary ) throws XbowException {

		// Remove from the system.

		flowadm.remove( flowName, temporary );
		logger.info( flowName + " flow removed." );

		// Unpublish, if publisher exists.

		if ( publisher != null ) {

			try {

				publisher.unpublish( flowName );
				logger.info( flowName + " flow unpublished." );

			} catch ( NotPublishedException e ) {

				logger.error( "Error while unpublishing " + flowName + ".", e );

			}

		}

	}


	/**
	 * Executes discover() in response to notification.
	 *
	 * @see  NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
	 */
	@Override
	public void handleNotification( Notification ntfctn, Object o ) {
		discover();
	}


	/**
	 * @brief  Flow helper setter method.
	 *
	 * @param  flowadm  helper used to manipulate flows
	 */
	public void setFlowadm( Flowadm flowadm ) {
		this.flowadm = flowadm;
	}


	/**
	 * @brief  Publisher setter method.
	 *
	 * @param  publisher  object responsible for registering discovered and created flows
	 */
	public void setPublisher( Publisher publisher ) {
		this.publisher = publisher;
	}


	private Flowadm flowadm = null;
	private Publisher publisher = null;

	private static final Logger logger = Logger.getLogger( FlowManager.class );

}
