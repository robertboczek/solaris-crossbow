package org.jims.modules.crossbow.gui.dialogs;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.progress.notification.ProgressNotification;

public class ProgressShell extends ProgressMonitorDialog {

	private static final int MAX_VALUE = 100;

	private Text logsText;
	private ProgressBar progressBar;
	private JmxConnector jmxConnector;
	private CrossbowNotificationMBean crossbowNotificationMBean;

	private Button closeButton;

	private String logs;
	private ProgressNotification progressNotification;

	private ProgressThread progressThread = null;

	private boolean running = true;

	/**
	 * Create the shell.
	 * 
	 * @param display
	 * @param jmxConnector
	 */
	public ProgressShell(Shell shell, JmxConnector jmxConnector) {
		super(shell);

		this.jmxConnector = jmxConnector;

		registerListenerAtMBSC();
	}

	private void registerListenerAtMBSC() {

		MBeanServerConnection mbsc;
		try {
			mbsc = jmxConnector.getMBeanServerConnection();
			crossbowNotificationMBean = JMX.newMBeanProxy(mbsc, new ObjectName(
					"Crossbow:type=CrossbowNotification"),
					CrossbowNotificationMBean.class);

			getLogs();

		} catch (Exception e) {
			e.printStackTrace();
			this.close();
		}
	}

	/**
	 * Create contents of the shell.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		this.setCancelable(false);
		this.setBlockOnOpen(false);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		progressBar = new ProgressBar(parent, SWT.SMOOTH);
		progressBar.setMinimum(0);
		progressBar.setMaximum(MAX_VALUE);
		progressBar.setLocation(30, 50);
		progressBar.setSize(200, 30);
		progressBar.setLayoutData(gridData);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		Label logLabel = new Label(parent, SWT.NONE);
		logLabel.setText("Details: ");
		logLabel.setLocation(30, 95);
		logLabel.setSize(50, 15);
		logLabel.setLayoutData(gridData);

		logsText = new Text(parent, SWT.V_SCROLL | SWT.VERTICAL | SWT.MULTI
				| SWT.WRAP | SWT.BORDER | SWT.H_SCROLL);
		logsText.setText("");
		logsText.setLocation(25, 120);
		logsText.setSize(250, 80);
		logsText.setLayoutData(gridData);
		logsText.setEditable(false);

		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);

		closeButton = new Button(parent, SWT.PUSH);
		closeButton.setText("Close");
		closeButton.setEnabled(false);

		closeButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				ProgressShell.this.close();
			}
		});

		return parent;

	}

	private void getLogs() {

		progressThread = new ProgressThread();
		progressThread.start();
	}

	private class ProgressThread extends Thread {

		public void run() {

			if (crossbowNotificationMBean != null) {

				while (running) {

					if (ProgressShell.this.logs == null && ProgressShell.this.progressNotification == null) {
						String logs = crossbowNotificationMBean.getNewLogs();

						ProgressNotification progressNotification = crossbowNotificationMBean
								.getProgress();

						ProgressShell.this.progressNotification = progressNotification;
						ProgressShell.this.logs = logs;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public synchronized void update() {

		System.out.println(logs);

		if (logs != null && logsText != null && !logs.equals("")) {
			logsText.setText(logs + logsText.getText());
			System.out.println(logs);
			
			logs = null;
		}

		if (progressNotification != null && progressBar != null) {
			// progressBar.setMaximum(progressNotification.getMax());
			progressBar.setSelection(progressNotification.getCurrent()
					* MAX_VALUE / progressNotification.getMax());

			System.out.println(progressNotification.getCurrent() + " "
					+ progressNotification.getMax());

			// okienko powinno sie zamknac po zakonczeniu
			// procesu
			// deployementu

			if (progressNotification.getCurrent() == progressNotification
					.getMax()) {
				running = false;
			}
			
			progressNotification = null;
		}

		

		if (!running) {
			closeButton.setEnabled(true);
		}

	}

}
