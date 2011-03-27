package org.jims.modules.crossbow.infrastructure.progress;

import java.io.Serializable;

import javax.management.NotificationBroadcaster;

public interface WorkerProgressMBean extends Serializable, NotificationBroadcaster{
	
	public void sendLogNotification(String log);
	
	public void sendTaskCompletedNotification();

}
