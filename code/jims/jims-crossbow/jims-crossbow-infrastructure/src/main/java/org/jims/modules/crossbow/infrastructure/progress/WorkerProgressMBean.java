package org.jims.modules.crossbow.infrastructure.progress;

import java.io.Serializable;

public interface WorkerProgressMBean extends Serializable{
	
	public void sendLogNotification(String log);
	
	public void sendTaskCompletedNotification();

	//public void clearListeners();

}
