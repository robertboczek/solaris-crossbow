package org.jims.modules.crossbow.gui.dialogs;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.infrastructure.progress.notification.JimsNotification;
import org.jims.modules.crossbow.infrastructure.progress.notification.LogNotification;
import org.jims.modules.crossbow.infrastructure.progress.notification.ProgressNotification;

public class ProgressShell extends Shell implements NotificationListener{
	
	private static final int MAX_VALUE = 100;
	
	private Text logsText;
	private ProgressBar progressBar;
	private JmxConnector jmxConnector;

	/**
	 * Create the shell.
	 * @param display
	 * @param jmxConnector 
	 */
	public ProgressShell(Display display, JmxConnector jmxConnector) {
		super(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
		
		this.jmxConnector = jmxConnector;
		
		registerListenerAtMBSC();
	}

	private void registerListenerAtMBSC() {
		
		MBeanServerConnection mbsc;
		try {
			mbsc = jmxConnector.getMBeanServerConnection();
			mbsc.addNotificationListener(new ObjectName("Crossbow:type=CrossbowNotification"), this, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			this.close();
		}
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Deploying progress");
		
		progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setMinimum(0);
		progressBar.setMaximum(MAX_VALUE);
		progressBar.setSelection(40);
		progressBar.setLocation(30, 50);
		progressBar.setSize(200, 30);
		
		Label logLabel = new Label(this, SWT.NONE);
		logLabel.setText("Details: ");
		logLabel.setLocation(30, 95);
		logLabel.setSize(50, 15);
		
		logsText = new Text(this, SWT.V_SCROLL | SWT.VERTICAL | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL);
		logsText.setText("");
		logsText.setLocation(25, 120);
		logsText.setSize(250, 80);

	}

	@Override
	public void handleNotification(Notification notification, Object arg1) {
		
		if(notification.getUserData() != null && notification.getUserData() instanceof JimsNotification) {
			System.err.println("Got notification from supervisor");
			if(notification.getUserData() instanceof LogNotification) {
				LogNotification logNotification = ((LogNotification)notification.getUserData());
				logsText.setText(logNotification.getLog() + " " + logNotification.getLog() + "\n" + logsText.getText());
			} else if(notification.getUserData() instanceof ProgressNotification) {
				ProgressNotification progressNotification = (ProgressNotification) notification.getUserData();
				progressBar.setMaximum(progressNotification.getMax());
				progressBar.setSelection(progressNotification.getCurrent());
				
				//okienko powinno sie zamknac po zakonczeniu procesu deployementu
				if(progressNotification.getCurrent() == progressNotification.getMax()) {
					this.close();
				}
			}
		}
	}

}
