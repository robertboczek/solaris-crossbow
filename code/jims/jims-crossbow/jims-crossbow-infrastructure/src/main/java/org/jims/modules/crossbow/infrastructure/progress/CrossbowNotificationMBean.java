package org.jims.modules.crossbow.infrastructure.progress;

import java.io.Serializable;

import javax.management.NotificationBroadcaster;
import javax.management.NotificationListener;

/**
 * Bean do notyfikowania o postepach w procesie deploymentu
 * 
 * @author Robert Boczek
 *
 */
public interface CrossbowNotificationMBean extends NotificationBroadcaster, Serializable, NotificationListener{

}
