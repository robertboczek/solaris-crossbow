package org.jims.modules.crossbow.gui.dialogs;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.Gui;
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.progress.notification.ProgressNotification;

public class ProgressShell extends ProgressMonitorDialog {

	private static final Logger logger = Logger.getLogger(ProgressShell.class);

	private static final int MAX_VALUE = 100;

	private Text logsText;
	private ProgressBar progressBar;
	private ComponentProxyFactory componentProxyFactory;
	private CrossbowNotificationMBean crossbowNotificationMBean;

	private String logs;
	private ProgressNotification progressNotification;

	private ProgressThread progressThread = null;

	private boolean running = true;

	private Display display;

	/**
	 * Create the shell.
	 * 
	 * @param display
	 * @param componentProxyFactory
	 */
	public ProgressShell(Gui shell, ComponentProxyFactory componentProxyFactory, Display display) {
		super(shell);

		this.display = display;
		this.componentProxyFactory = componentProxyFactory;

		registerListenerAtMBSC();
	}

	private void registerListenerAtMBSC() {

		logger.info("Getting CrossbowNotificationMBean");

		try {
			crossbowNotificationMBean = componentProxyFactory.createProxy( CrossbowNotificationMBean.class );
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

		this.setCancelable(true);
		this.setBlockOnOpen(true);

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

		return parent;

	}

	private void getLogs() {

		logger.info("Starting new ProgressThread");
		progressThread = new ProgressThread();
		progressThread.start();
	}

	private class ProgressThread extends Thread {

		public void run() {

			if (crossbowNotificationMBean != null) {

				while (running) {

					if (ProgressShell.this.logs == null
							&& ProgressShell.this.progressNotification == null) {

						logger.debug("Checking progress and logs at CrossbowNotificationMBean");

						String logs = crossbowNotificationMBean.getNewLogs();

						ProgressNotification progressNotification = crossbowNotificationMBean
								.getProgress();

						ProgressShell.this.progressNotification = progressNotification;
						ProgressShell.this.logs = logs;

						update();
					}
					try {
						Thread.sleep(2000);
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

		logger.debug("Updating logs and progress...");

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				if (logs != null && logsText != null) {
					if (!logs.equals("")) {
						logsText.setText(logs + logsText.getText());
						logger.info("New logs: '" + logs + "' were added");
					}
					logs = null;
				}

				if (progressNotification != null && progressBar != null) {

					// progressBar.setMaximum(progressNotification.getMax());
					progressBar.setSelection(progressNotification.getCurrent()
							* MAX_VALUE / progressNotification.getMax());

					logger.info("Progress " + progressNotification.getCurrent()
							+ " out of " + progressNotification.getMax()
							+ " was updated");

					// okienko powinno sie zamknac po zakonczeniu
					// procesu deployementu

					if (progressNotification.getCurrent() == progressNotification
							.getMax()) {
						logger
								.info("Setting progressThread running flag to false");
						running = false;

						logger.info("Stopping ProgressShell thread");
					}

					progressNotification = null;
				}
			}

		});
	}

}
