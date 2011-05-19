package org.jims.modules.crossbow.gui.threads;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;

/**
 * Tester polaczenia
 * 
 * @author robert
 * 
 */
public class ConnectionTester extends Thread {
	
	public static interface ConnectionStatusListener {
		public void connected( String server, int port );
		public void disconnected( String server, int port );
	}

	private static final int DELAY = 15000;
	private static final Logger logger = Logger
			.getLogger(ConnectionTester.class);

	private volatile boolean run = true;
	private boolean connected = false;

	private JmxConnector jmxConnector;
	private List< ConnectionStatusListener > listeners = new LinkedList< ConnectionStatusListener >();
	
	private String address = "", port = "";

	public ConnectionTester() {
		this.start();
	}
	
	public void addConnectedListener( ConnectionStatusListener l ) {
		listeners.add( l );
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

				boolean oldConnected = connected;
					
				try {

					jmxConnector = new JmxConnector( address, Integer.parseInt( port ) );

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
				
				if ( oldConnected != connected ) {
					
					if ( connected ) {
						for ( ConnectionStatusListener l : listeners ) {
							l.connected( address, Integer.parseInt( port ) );
						}
					} else {
						for ( ConnectionStatusListener l : listeners ) {
							l.disconnected( address, Integer.parseInt( port ) );
						}
					}
					
				}
				
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error("Interrupted exception");
			}
		}

	}
	
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress( String address ) {
		this.address = address;
	}
	
	
	public String getPort() {
		return port;
	}

	public void setPort( String port ) {
		this.port = port;
	}
	
}
