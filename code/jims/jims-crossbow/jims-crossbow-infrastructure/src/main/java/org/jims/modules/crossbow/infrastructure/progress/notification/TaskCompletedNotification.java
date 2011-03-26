package org.jims.modules.crossbow.infrastructure.progress.notification;

public class TaskCompletedNotification implements JimsNotification {
	
	private String ipAddress;

	public TaskCompletedNotification(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@Override
	public String getNodeIpAddress() {
		return this.ipAddress;
	}

}
