package org.jims.modules.crossbow.infrastructure;

import java.util.ArrayList;
import java.util.Iterator;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

public class JimsMBeanServer {

	private static Logger logger = Logger.getLogger( JimsMBeanServer.class );
	private static MBeanServer server;

	/** Znajduje lokalny Jimsowy MBeanServer */
	public static MBeanServer findJimsMBeanServer() {

		if(server != null) {
			return server;
		}
	
		ArrayList servers = MBeanServerFactory.findMBeanServer( null );

		logger.info( "Registered servers number: " + servers.size() );
	
		Iterator it = servers.iterator();
		while ( it.hasNext() ) {
			MBeanServer testedServer = ( MBeanServer ) it.next();
				try {
					if ( testedServer.isRegistered( new ObjectName( "Information:class=OSCommon" ) ) ) {
						server = testedServer;
						break;
					}
				} catch ( Exception ex ) {
					logger.error( "Error while testing MBean Server", ex );
				}
	
			}

			if ( null == server ) {

				logger.error( "JIMS mbean servered was not found" );
				return null;

			}
		return server;
	}

}
