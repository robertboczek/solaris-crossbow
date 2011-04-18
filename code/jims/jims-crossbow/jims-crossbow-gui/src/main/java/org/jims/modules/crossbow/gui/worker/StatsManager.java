package org.jims.modules.crossbow.gui.worker;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneMonitoringMBean;


public class StatsManager implements ConnectionTester.ConnectionStatusListener {
	
	public static interface ConnectionProvider {
		public MBeanServerConnection provide( String url );
	}
	
	public static interface StatsHandler {
		public void handle( Map< String, GlobalZoneMonitoringMBean > monitors );
		public void stop();
	}

	
	public StatsManager( ComponentProxyFactory proxyFactory,
	                     ConnectionProvider provider,
	                     StatsHandler handler ) {
		
		this.proxyFactory = proxyFactory;
		this.provider = provider;
		this.handler = handler;
		
	}
	
	
	@Override
	public void connected( String server, int port ) {
		
		// Iterate through ZoneMonitor mbeans and create proxies.
		
		SupervisorMBean supervisor = proxyFactory.createProxy( SupervisorMBean.class );
		
		if ( null == supervisor ) {
			
			// TODO-DAWID  log error here
			
		} else {
			
			Map< String, GlobalZoneMonitoringMBean > monitors
				= new HashMap< String, GlobalZoneMonitoringMBean >();
		
			for ( String workerAddress : supervisor.getWorkers() ) {
				
				monitors.put( workerAddress,
				              proxyFactory.createProxy( GlobalZoneMonitoringMBean.class, 
				                                        provider.provide( workerAddress ) ) );
				
			}
			
			if ( null == handler ) {
				// TODO-DAWID  log error here
			} else {
				handler.handle( monitors );
			}
		
		}
		
	}
	

	@Override
	public void disconnected( String server, int port ) {
		handler.stop();
	}
	
	
	private ComponentProxyFactory proxyFactory;
	private ConnectionProvider provider;
	private StatsHandler handler;

}
