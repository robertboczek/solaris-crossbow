package org.jims.modules.crossbow.gui.worker;

import javax.management.MBeanServerConnection;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;


public class JmxConnectionProvider implements StatsManager.ConnectionProvider {
	
	@Override
	public MBeanServerConnection provide( String url ) {
		
		try {
			return new JmxConnector( url ).getMBeanServerConnection();
		} catch ( Exception e ) {
			logger.error( String.format( "Could not get MBean Server connection object (url: %s)", url ), e );
		}
		
		return null;
	}
	
	
	private final static Logger logger = Logger.getLogger( JmxConnectionProvider.class );

}
