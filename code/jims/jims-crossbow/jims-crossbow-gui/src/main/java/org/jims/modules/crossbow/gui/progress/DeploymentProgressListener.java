package org.jims.modules.crossbow.gui.deploy.progress;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.jims.modules.crossbow.infrastructure.progress.notification.JimsNotification;

public class DeploymentProgressListener implements NotificationListener {

	@Override
	public void handleNotification(Notification notification, Object arg1) {

		Object message = notification.getUserData();
		if (message instanceof JimsNotification) {
			System.out.println("Otrzymalem notyfikacje o progressie w deploymencie");
		}

	}

}
