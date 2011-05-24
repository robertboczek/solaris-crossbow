package org.jims.modules.crossbow.gui.actions;

import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManagerMBean;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGathererMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneMonitoringMBean;

public class ComponentProxyFactory {
	
	public ComponentProxyFactory() {
		
		objectNames.put( SupervisorMBean.class, "Crossbow:type=Supervisor" );
		objectNames.put( RepoManagerMBean.class, "Crossbow:type=RepoManager" );
		objectNames.put( CrossbowNotificationMBean.class, "Crossbow:type=CrossbowNotification" );
		objectNames.put( StatisticsGathererMBean.class, "Crossbow:type=StatisticsGatherer" );
		objectNames.put( StatisticsGathererMBean.class, "Crossbow:type=StatisticsGatherer" );
		objectNames.put( WorkerMBean.class, "Crossbow:type=XBowWorker" );
		
		objectNames.put( GlobalZoneMonitoringMBean.class, "solaris10.monitoring.global:type=ZoneMonitor,role=monitoring" );
		
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

		if ((!ruMbServer.equals(mbServer)) || (!ruMbPort.equals(mbPort))) {

			// JMX address or port changed. Refresh server connection.

			jmxConnector = new JmxConnector(mbServer, Integer.parseInt(mbPort));

			try {
				mbsc = jmxConnector.getMBeanServerConnection();
			} catch (Exception e) {
				logger.error( "Couldn't get MBeanServerConnection", e );
			}

			actuallyRefreshed = true;

		}

		return actuallyRefreshed;

	}

	public String getMbServer() {
		return mbServer;
	}

	public void setMbServer(String mbServer) {
		this.mbServer = mbServer;
	}

	public String getMbPort() {
		return mbPort;
	}

	public void setMbPort(String mbPort) {
		this.mbPort = mbPort;
	}
	
	public JmxConnector getJmxConnector() {
		return this.jmxConnector;
	}

	private String mbServer = "", mbPort = "";
	private String ruMbServer = "", ruMbPort = ""; // Recently used values

	private JmxConnector jmxConnector;
	private MBeanServerConnection mbsc;
	
	private Map< Class< ? extends Object >, Object > proxies
		= new HashMap< Class< ? extends Object >, Object >();
	private Map< Class< ? extends Object >, String > objectNames
		= new HashMap< Class< ? extends Object >, String >();

	private static final Logger logger = Logger
		.getLogger(ComponentProxyFactory.class);

}
