package org.jims.modules.solaris.solaris10.mbeans;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.registry.LocateRegistry;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;
import org.jims.agent.exception.JimsManagementException;
import org.jims.common.ManagementCommons;
import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.solaris.solaris10.fs.FsUtils;
import org.jims.modules.solaris.solaris10.helpers.ProjectManager;
import org.jims.modules.solaris.solaris10.helpers.ZoneManager;

/**
 * Zone agent MBean exposed through the JMX MLet service. 
 * It only intializes apropriate MBeans depends on zone which is running.
 * For global zone {@link 
 *  org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagement 
 *  GlobalZoneManagement} 
 * is used and for local zones {@link 
 *  org.jims.modules.solaris.solaris10.mbeans.LocalZoneManagement 
 *  LocalZoneManagement} 
 * 
 * @author bombel
 * @version $Id: ZoneAgent.java 1603 2006-11-21 10:10:36 +0100 (Tue, 21 Nov 2006) bombel $
 */
public class ZoneAgent extends SolarisManagementCommons 
	implements ZoneAgentMBean
{
	private static final Logger logger = Logger.getLogger(ZoneAgent.class);		 					
	
	protected Solaris10ExactMonitorMBean exacctMonitorMBean;
			
	protected ZoneManagementMBean zoneManagement;		 
	
	/**
	 * Indicates if the monitoring agent acts also as a Cluster Manager (Gateway)
	 */
	protected boolean isGateway;	
	
	/**
	 * Create and initialize 
	 * 
	 * @throws JimsManagementException
	 */
	public ZoneAgent() throws JimsManagementException
	{
		initialize();
	}
	
	public ContainsIpReturnCode containsIP(String ip) throws JimsManagementException
	{		
		return zoneManagement.containsIP(ip);
	}

	/**
	 * Initializes Mbeans responsible for global or local zone management
	 * 
	 * @see org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagement
	 * @see org.jims.modules.solaris.solaris10.mbeans.LocalZoneManagement
	 * 
	 * @throws JimsManagementException
	 */
	protected void initialize() throws JimsManagementException
	{
		try
		{				
			projectManager = ProjectManager.getInstance();
			zoneManager = ZoneManager.getInstance();
			
			MBeanServer mbs = ManagementCommons.getMBeanServer();
			ObjectName beanName = null;
			if ( zoneManager.isGlobalZone() )
			{	
				extractHelpers();
				
				zoneManagement = new GlobalZoneManagement();
				//beanName = new ObjectName( SolarisManagementCommons.assembleZoneMBeanName("global", true));
				beanName = SolarisManagementCommons.createZoneManagementObjectName("global");
				mbs.registerMBean( zoneManagement, beanName);                
			}
			else
			{
				String zoneName = zoneManager.getZoneName();
				zoneManagement = new LocalZoneManagement();
				//beanName = new ObjectName( SolarisManagementCommons.assembleZoneMBeanName(zoneName, true));				
				beanName = SolarisManagementCommons.createZoneManagementObjectName( zoneName );				
				mbs.registerMBean( zoneManagement, beanName);
			}
		    
			isGateway = isGateway(); 
			if ( isGateway )
			{
				logger.info("This Solaris agent is started within JIMS GW!");								
			}
			
		}
		catch(Exception ex)		
		{
			String errmsg = "Initialization of zone agent failed:" + ex.getMessage();
			logger.error(errmsg,ex);
			throw new JimsManagementException(errmsg,ex);
		}
	}

	/**
	 * Get MBean server connection identified by the JMX address.
	 * Connection pooling works only within JIMS Gateway, if invoked 
	 * in non Gateway then ClassNotFoundException might be thrown
	 * (MBSManager is specific for the Gateway)
	 *   
	 * @param jmxAddr
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException if non gateway environment
	 */
	protected MBeanServerConnection getMBSConnection( String jmxAddr ) throws IOException
	{
		JMXConnector jmxConnector = 
			org.jims.modules.sg.service.manager.MBSManager.getInstance().
									getConnector( jmxAddr );
		return jmxConnector.getMBeanServerConnection();		
	}
	
    /**
    * Extract helper scripts stored in the jar file and next save them 
    * into the JIMS temporary directory.
    */
	private void extractHelpers() throws Exception
    {
	    final String[] helpers = {"/zone/jims_svcwait.sh",
                                "/zone/jims_sysidwait_zhalt.sh",
                                "/zone/jims_zattach.sh",
                                "/zone/jims_zattach_ifaces.sh",
                                "/zone/jims_zboot.sh",
                                "/zone/jims_zconfig_ifaces.sh",
                                "/zone/jims_zcreate.sh",
                                "/zone/jims_zcreate_from_clone.sh",
                                "/zone/jims_zdelete.sh",
                                "/zone/jims_zdetach.sh",
                                "/zone/jims_zhalt.sh",
                                "/zone/jims_zinstall.sh",
                                "/zone/jims_zjimsagent.sh",
                                "/zone/jims_zmatch.sh",
                                "/zone/jims_zsetup_forwarding.sh",
                                "/zone/jims_zmanage_routes.sh",
                                "/zone/jims_zread_routes.sh",
                                "/accounting/jims_switchexacct.sh"};
		
	    String destDir = ManagementCommons.getJimsTemporaryDir();
	    
        for (String helper:helpers)
        {
        	String extractedHelperPath = destDir + File.separator + helper;
            logger.debug("Extracting helper=" + helper + " to " + extractedHelperPath);
            FsUtils.saveJaredHelper(helper, extractedHelperPath);                        
        }
    }
	
    /**
     * Registers Exacct mbean manager. This MBean should be only registered
     * in Global zone environment. 
     * 
     * @throws JimsManagementException
     * @throws IllegalStateException if non global zone environment
     */
    private void registerSolaris10AccountingMBean() throws JimsManagementException
    {
    	if ( !zoneManager.isGlobalZone() )
    	{
    		throw new IllegalStateException("Solaris Accounting mbean should be only registered within global zone!");
    	}
    	
    	String exacctMBeanName = assembleSolaris10ExacctMonitorName();
		try
		{
			ObjectName exacctName = new ObjectName(exacctMBeanName);						
			
			String logDirPath = getProperty("jims.agent.logdir.path");
			
			exacctMonitorMBean = new Solaris10ExactMonitor(logDirPath);
			ManagementCommons.getMBeanServer().registerMBean(exacctMonitorMBean , exacctName);
		}
		catch (NullPointerException e) 
		{
			String msg = "Internal error: ObjectName for Solaris10ExactMonitor MBean is null.";
			logger.error(msg + ": " + e);
			throw new JimsManagementException(msg, e);
		}
		catch (InstanceAlreadyExistsException e) 
		{
			String errmsg = "Trying to register Solaris10ExactMonitor, InstanceExists!";
			logger.error(errmsg,e);
			throw new JimsManagementException(errmsg);
		} 
		catch (MBeanRegistrationException e) 
		{
			String errmsg = "Mbean already exists, name=" + exacctMBeanName;
			logger.error(errmsg,e);
			throw new JimsManagementException(errmsg);
		} 
		catch (NotCompliantMBeanException e) 
		{
			String errmsg = "Solaris10ExactMonitor Mbean is not compilant, name=" + exacctMBeanName;
			logger.error(errmsg,e);
			throw new JimsManagementException(errmsg);
		} 
		catch (MalformedObjectNameException e) 
		{
			String errmsg = "Solaris10ExactMonitor Mbean name is malformed, name=" + exacctMBeanName;
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		}
		catch(NumberFormatException e)
		{
			String errmsg = "Solaris10ExactMonitor Mbean timer period time has incorrect format";
			logger.error(errmsg, e);
			throw new JimsManagementException(errmsg);
		}
    }
	
    public static void testCreateZoneFromSnapshots(ZoneInfo zone, String srcSnapshot, String host) throws Exception {
    	
    	try {
    		System.out.println( "Create zone " + zone);
    		
			int port = 7707; 

			String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jims";
			System.out.println(url);	

			JMXServiceURL jmxurl = new JMXServiceURL( url );

			//  Get JMX connector
			JMXConnector  jmxc = JMXConnectorFactory.connect(jmxurl,null);
			
			//  Get MBean server connection
			MBeanServerConnection  mbsc = jmxc.getMBeanServerConnection();
			
			GlobalZoneManagementMBean globalZoneMgmt = (GlobalZoneManagementMBean)
			ManagementCommons.getMBeanProxy(mbsc, 
					assembleZoneMBeanName("global", true), 
					GlobalZoneManagementMBean.class, false);
			System.out.println("Proxy to global zone agent obtained!");
			
			globalZoneMgmt.createZoneFromSnapshot( zone, srcSnapshot);
			System.out.println("Zone created! Booting zone ...");
			globalZoneMgmt.bootZone( zone.getName() );
			System.out.println( "Zone booted!" );
	
    	} catch (Exception e) {
    		throw e;
    	}
    }
    
	public static void main(String[] args)
	{
		// standalone MBS for testing this MBean
		
		try {
			
			ZoneInfo zone; 
			String srcSnapshot;
			String host;
			
			srcSnapshot 
			= "/mnt/repositories/appliances_repo/glassfish_node_template@Glassfish-2.1-SnapVer1.0";
			
			/**
			zone = new ZoneInfo("c05-node1");
			zone.setZonepath( "/zone_roots/c05-node1" );
			zone.setZfsPool( "zone_roots" );
			zone.setAutoboot( true );
			zone.setPhysical( "e1000g0" );
			zone.setAddress( "172.16.21.152" );
			zone.setPool( "glassfish-pool" );
			host = "172.16.21.15"; 
			testCreateZoneFromSnapshots( zone, srcSnapshot, host );
			*/
			
			zone = new ZoneInfo("c06-node1");
			zone.setZonepath( "/zone_roots/c06-node1" );
			zone.setZfsPool( "zone_roots" );
			zone.setAutoboot( true );
			zone.setPhysical( "e1000g0" );
			zone.setAddress( "172.16.21.162" );
			zone.setPool( "glassfish-pool" );
			host = "172.16.21.16"; 
			testCreateZoneFromSnapshots( zone, srcSnapshot, host );
						
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/**
		MBeanServer mbs = MBeanServerFactory.createMBeanServer();
		
		String host = null;
			
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			throw new RuntimeException("Could not retrieve localhost address");
		}
		
		int port = 9600; 

		String url = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/test";
		System.out.println(url);		
		
		try{
			LocateRegistry.createRegistry(port);
			
			JMXServiceURL jmxUrl = new JMXServiceURL(
				url);
			JMXConnectorServer cs =
				JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, mbs);

			cs.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}			
			
		ObjectName beanName = null;
		try {
			beanName = new ObjectName("test:id=testSolarisAgent");
		} catch (MalformedObjectNameException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (NullPointerException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		//Try to register isolate MBean with retrieved name in server
		try {
			mbs.registerMBean(new ZoneAgent(), beanName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		*/
	}
}
