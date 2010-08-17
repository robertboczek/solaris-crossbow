package agh.msc.xbowbase.link;

import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.link.util.NicToNicInfoTranslator;
import agh.msc.xbowbase.publisher.Publisher;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;


/**
 * The class implements NicManagerMBean functionality.
 *
 * @author cieplik
 */
public class NicManager implements NicManagerMBean, NotificationListener {

	/**
	 * @see  NicManagerMBean#getNicsList()
	 */
	@Override
	public List< String > getNicsList() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}


	/**
	 * @see  NicManagerMBean#discover()
	 */
	@Override
	public void discover() {

		if ( publisher != null ) {

			synchronized ( publisher ) {

				List< NicInfo > nicsInfo = nicHelper.getNicsInfo();

				// logger.debug( nicsInfo.size() + " nic(s) discovered." );

				for ( NicInfo nicInfo : nicsInfo ) {

					// Create new Flow object, initialize and register it.

					Nic nic = NicToNicInfoTranslator.toNic( nicInfo );
					nic.setNicHelper( nicHelper );

					publisher.publish( nic );

				}

				// Unpublish flows user deleted manually.

				Set< String > published = new HashSet< String >();
				for ( Object nic : publisher.getPublished() ) {
					published.add( ( ( Nic ) nic ).getName() );
				}

				Set< String > discovered = new HashSet< String >();
				for ( Object nicInfo : nicsInfo ) {
					discovered.add( ( ( NicInfo ) nicInfo ).name );
				}

				published.removeAll( discovered );
				for ( Object nicName : published ) {

					try {
						publisher.unpublish( ( String ) nicName );
					} catch ( NotPublishedException e ) {
						// logger.fatal( "Error while removing stale flows.", e );
					}

				}

			}

		}

	}


	/**
	 * Executes discover() in response to notification.
	 *
	 * @see  NotificationListener#handleNotification( javax.management.Notification, java.lang.Object )
	 */
	@Override
	public void handleNotification( Notification ntfctn, Object o ) {
		discover();
	}


	public void setPublisher( Publisher publisher ) {
		this.publisher = publisher;
	}


	public void setNicHelper( NicHelper nicHelper ) {
		this.nicHelper = nicHelper;
	}


	Publisher publisher;
	NicHelper nicHelper;

}
