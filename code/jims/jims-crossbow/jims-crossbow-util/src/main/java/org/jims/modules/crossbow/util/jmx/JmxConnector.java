package org.jims.modules.crossbow.util.jmx;

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

	public JmxConnector( String url ) {
		this.url = url;
	}
	
	public JmxConnector(String address, int port) {
		this.url = "service:jmx:rmi:///jndi/rmi://" + address + ":" + port + "/jims";
	}
	
	public String getUrl() {
		return url;
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
	
	
	private String url;
	private MBeanServerConnection mbsc;

}
