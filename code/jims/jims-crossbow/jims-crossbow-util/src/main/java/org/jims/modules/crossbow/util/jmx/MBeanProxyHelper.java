package org.jims.modules.crossbow.util.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;


public class MBeanProxyHelper {
	
	public MBeanProxyHelper( String url, Map< Class< ? extends Object >, String > mappings ) {
		this.mbUrl = url;
		objectNames.putAll( mappings );
	}
	
	
	@SuppressWarnings( "unchecked" )
	public < T > T createProxy( Class< T > klass ) {
		
		if ( refreshMBeanServerConnection() || ( null == proxies.get( klass ) ) ) {
			proxies.put( klass, createProxy( klass, mbsc ) );
		}
		
		return ( T ) proxies.get( klass );
		
	}
	
	
	public < T > T createProxy( Class< T > klass, MBeanServerConnection mbsc ) {
		
		T res = null;
		
		try {
			res = JMX.newMBeanProxy( mbsc, new ObjectName( objectNames.get( klass ) ), klass );
		} catch ( Exception e ) {
			logger.error( "Could not create proxy for " + klass, e );
		}
				
		return res;
		
	}
	

	private boolean refreshMBeanServerConnection() {

		boolean actuallyRefreshed = false;

		if ( connectionDirty ) {

			// JMX address or port changed. Refresh server connection.

			jmxConnector = ( null != mbUrl ) ? new JmxConnector( mbUrl )
			                                 : new JmxConnector(mbServer, Integer.parseInt(mbPort));

			try {
				mbsc = jmxConnector.getMBeanServerConnection();
			} catch (Exception e) {
				logger.error( "Couldn't get MBeanServerConnection", e );
			}

			connectionDirty = false;
			actuallyRefreshed = true;

		}

		return actuallyRefreshed;

	}

	public void setUrl( String url ) {
		mbUrl = url;
		connectionDirty = true;
	}

	public String getMbServer() {
		return mbServer;
	}

	public void setMbServer(String mbServer) {
		this.mbServer = mbServer;
		connectionDirty = true;
	}

	public String getMbPort() {
		return mbPort;
	}

	public void setMbPort(String mbPort) {
		this.mbPort = mbPort;
		connectionDirty = true;
	}
	
	public JmxConnector getJmxConnector() {
		return this.jmxConnector;
	}

	private String mbServer = "", mbPort = "";
	private String mbUrl;

	private boolean connectionDirty = true;

	private JmxConnector jmxConnector;
	private MBeanServerConnection mbsc;
	
	private Map< Class< ? extends Object >, Object > proxies
		= new HashMap< Class< ? extends Object >, Object >();
	private Map< Class< ? extends Object >, String > objectNames
		= new HashMap< Class< ? extends Object >, String >();

	private static final Logger logger = Logger
		.getLogger(MBeanProxyHelper.class);

}
