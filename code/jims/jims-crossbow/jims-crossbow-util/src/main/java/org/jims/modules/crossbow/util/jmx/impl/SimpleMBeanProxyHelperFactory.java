package org.jims.modules.crossbow.util.jmx.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.util.jmx.MBeanProxyHelper;
import org.jims.modules.crossbow.util.jmx.MBeanProxyHelperFactory;


/**
 *
 * @author cieplik
 */
public class SimpleMBeanProxyHelperFactory implements MBeanProxyHelperFactory {

	@Override
	public MBeanProxyHelper getComponentProxyHelper() {
		return getComponentProxyHelper( null );
	}


	@Override
	public MBeanProxyHelper getComponentProxyHelper( String url ) {

		Map< Class< ? extends Object >, String > objectNames = new HashMap< Class< ? extends Object >, String >();

		try {

			objectNames.put( Class.forName( XBOW_INFRASTRUCTURE_PREFIX + ".supervisor.SupervisorMBean" ), "Crossbow:type=Supervisor" );
			objectNames.put( Class.forName( XBOW_INFRASTRUCTURE_PREFIX + ".appliance.RepoManagerMBean" ), "Crossbow:type=RepoManager" );
			objectNames.put( Class.forName( XBOW_INFRASTRUCTURE_PREFIX + ".progress.CrossbowNotificationMBean" ), "Crossbow:type=CrossbowNotification" );
			objectNames.put( Class.forName( XBOW_INFRASTRUCTURE_PREFIX + ".gatherer.StatisticsGathererMBean" ), "Crossbow:type=StatisticsGatherer" );

		} catch ( ClassNotFoundException ex ) {
			logger.fatal( "Error while initializing component proxy helper. "
			              + " VERY BAD THINGS may happen since now.", ex );
		}

		return new MBeanProxyHelper( url, objectNames );

	}


	@Override
	public MBeanProxyHelper getManagerProxyFactory() {
		return getManagerProxyFactory( null );
	}


	@Override
	public MBeanProxyHelper getManagerProxyFactory( String url ) {

		Map< Class< ? extends Object >, String > objectNames = new HashMap< Class< ? extends Object >, String >();

		try {

			objectNames.put( Class.forName( "org.jims.modules.solaris.solaris10.mbeans.GlobalZoneMonitoringMBean" ), "solaris10.monitoring.global:type=ZoneMonitor,role=monitoring" );
			objectNames.put( Class.forName( "org.jims.modules.crossbow.vlan.VlanManagerMBean" ), "Crossbow:type=VlanManager" );

		} catch ( ClassNotFoundException ex ) {
			logger.fatal( "Error while initializing component proxy helper. "
			              + " VERY BAD THINGS may happen since now.", ex );
		}

		return new MBeanProxyHelper( url, objectNames );

	}


	private static final String XBOW_INFRASTRUCTURE_PREFIX = "org.jims.modules.crossbow.infrastructure";
	// private static final String XBOW_MBEAN_PREFIX = "org.?jims.?modules.?crossbow.?infrastructure";

	private static final Logger logger = Logger.getLogger( SimpleMBeanProxyHelperFactory.class );

}
