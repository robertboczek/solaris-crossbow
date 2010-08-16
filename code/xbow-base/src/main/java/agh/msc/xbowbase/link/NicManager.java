package agh.msc.xbowbase.link;

import java.util.List;
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
		throw new UnsupportedOperationException( "Not supported yet." );
	}


	/**
	 * Executes discover() in response to notification.
	 *
	 * @see  NotificationListener#handleNotification( javax.management.Notification, java.lang.Object )
	 */
	@Override
	public void handleNotification( Notification ntfctn, Object o ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
