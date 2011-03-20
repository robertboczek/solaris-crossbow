package org.jims.modules.crossbow.gui.jmx;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Klasa odpowiedzialna za nawiazanie polaczenia ze zdalnym mbean serwerem
 * 
 * @author robert
 * 
 */
public class JmxConnector {

	private int port;
	private String address;

	private MBeanServerConnection mbsc;

	public JmxConnector(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getUrl() {
		return "service:jmx:rmi:///jndi/rmi://" + address + ":" + port
		+ "/jims";
	}

	public MBeanServerConnection getMBeanServerConnection() throws Exception {

		if (mbsc == null) {
			JMXServiceURL url = new JMXServiceURL(
					getUrl());

			JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
			mbsc = jmxc.getMBeanServerConnection();
		}

		return mbsc;
	}
	
	public String getMBeanServerConnectionDetails() throws IOException {
		return (mbsc != null) ? "Default domain: " + mbsc.getDefaultDomain() + " number of registered beans: " + mbsc.getMBeanCount() : "Not connected";
	}

}
