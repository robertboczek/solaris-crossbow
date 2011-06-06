package org.jims.modules.crossbow.infrastructure.progress;

import java.io.Serializable;

import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;

import org.jims.modules.crossbow.infrastructure.progress.notification.ProgressNotification;

/**
 * Bean do notyfikowania o postepach w procesie deploymentu
 * 
 * @author Robert Boczek
 *
 */
public interface CrossbowNotificationMBean extends Serializable, NotificationListener {

	public ProgressNotification getTotalProgress();

	public ProgressNotification getProgress();

	public String getNewLogs();

	public String getTotalNewLogs();

	public void resetTotal(int numberOfNodes);

	public void reset();

}
