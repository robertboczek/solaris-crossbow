package org.jims.modules.crossbow.gui.threads;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.jims.modules.crossbow.gui.Gui;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;

/**
 * Tester polaczenia
 * 
 * @author robert
 * 
 */
public class ConnectionTester extends Thread {

	private static final int DELAY = 15000;
	private static final Logger logger = Logger
			.getLogger(ConnectionTester.class);

	private Gui gui;
	private boolean run = true;
	private boolean connected = false;

	private JmxConnector jmxConnector;
	private Display display;

	public ConnectionTester(Gui gui, Display display) {

		this.gui = gui;
		this.display = display;

		this.start();

	}

	public void stopThread() {
		this.run = false;
	}

	public boolean getConnected() {
		return connected;
	}

	public void run() {

		while (run) {

			// testuje polaczenie z jmx'owym mbean serverem
			try {

				try {

					jmxConnector = new JmxConnector(gui.getConnectionAddress(),
							Integer.parseInt(gui.getConnectionPort()));

					logger.debug("Trying to connect to "
							+ jmxConnector.getUrl());

					jmxConnector.getMBeanServerConnection();

					logger.debug("Gui is connected");

					connected = true;

				} catch (NumberFormatException e) {
					connected = false;
					logger.error("Port number must be positive integer");
				} catch (Exception e) {
					connected = false;
					logger.debug("Not connected");
				}
				
				display.asyncExec(new Runnable() {
					public void run() {
						if (connected) {
							gui.setText("Connected");
						} else {
							gui.setText("Not connected");
						}
					}
				});

				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Interrupted exception");
			}
		}

	}

}
