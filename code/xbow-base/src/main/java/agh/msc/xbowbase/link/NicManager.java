package agh.msc.xbowbase.link;

import java.util.List;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;

/**
 * The class implements NicManagerMBean functionality.
 *
 * @author cieplik
 */
public class NicManager implements NicManagerMBean, NotificationListener {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Nic.class);


	/**
	 * @see  NicManagerMBean#getNicsList()
	 */
	@Override
	public List< String > getNicsList() {
            //@todo use jna library to get list of nic names
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
