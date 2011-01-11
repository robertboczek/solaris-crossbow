package org.jims.modules.crossbow;

import java.io.File;
import java.lang.reflect.Method;
import org.jims.modules.crossbow.etherstub.EtherstubManager;
import org.jims.modules.crossbow.publisher.FlowMBeanPublisher;
import org.jims.modules.crossbow.flow.FlowManager;
import org.jims.modules.crossbow.jna.JNAEtherstubHelper;
import org.jims.modules.crossbow.jna.JNAFlowHelper;
import org.jims.modules.crossbow.jna.JNANicHelper;
import org.jims.modules.crossbow.jna.JNAVNicHelper;
import org.jims.modules.crossbow.lib.EtherstubHelper;
import org.jims.modules.crossbow.lib.FlowHelper;
import org.jims.modules.crossbow.lib.NicHelper;
import org.jims.modules.crossbow.lib.VNicHelper;
import org.jims.modules.crossbow.link.NicManager;
import org.jims.modules.crossbow.link.VNicManager;
import org.jims.modules.crossbow.link.validators.LinkValidator;
import org.jims.modules.crossbow.link.validators.RegexLinkValidator;
import org.jims.modules.crossbow.publisher.EtherstubMBeanPublisher;
import org.jims.modules.crossbow.publisher.NicMBeanPublisher;
import org.jims.modules.crossbow.publisher.VNicMBeanPublisher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.timer.Timer;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.jna.JNALinkHelper;
import org.jims.modules.crossbow.zones.ZoneCopier;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;


/**
 * This is a bootstrap class used to initialize the Crossbow module.
 *
 * @author cieplik
 */
public class CrossbowStarter implements CrossbowStarterMBean {

	public CrossbowStarter() throws Exception {

		MBeanServer server = null;

		// Get the JIMS MBean server.

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

			logger.warn( "JIMS mbean servered was not found" );
			return;

		}

		// Validators

		LinkValidator linkValidator = new RegexLinkValidator();

		// Initialize native wrappers.

		// TODO: v remove reflection

		Class klass = Class.forName( "org.jims.common.ManagementCommons" );
		String libraryPath = ( String ) klass.getMethod( "getJimsTemporaryDir" ).invoke( null );

		Method extractContentToDirectory = Class.forName( "org.jims.common.JarExtractor" )
			.getMethod( "extractContentToDirectory", String.class, String.class, Class.class );


		for ( String lib : Arrays.asList( JNAEtherstubHelper.LIB_NAME,
		                                  JNAFlowHelper.LIB_NAME,
		                                  JNALinkHelper.LIB_NAME ) ) {

			String destFileName = libraryPath + File.separator + lib;

			extractContentToDirectory.invoke( null, "/" + lib,
			                                        destFileName,
			                                        JNAEtherstubHelper.class );

		}

		FlowHelper flowadm = new JNAFlowHelper( libraryPath );
		NicHelper nicHelper = new JNANicHelper( libraryPath, linkValidator );
		VNicHelper vnicHelper = new JNAVNicHelper( libraryPath, linkValidator );
		EtherstubHelper etherstubHelper = new JNAEtherstubHelper( libraryPath );

		// Create managers.

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );
		flowManager.setPublisher( new FlowMBeanPublisher( server ) );

		NicManager nicManager = new NicManager();
		nicManager.setNicHelper( nicHelper );
		nicManager.setPublisher( new NicMBeanPublisher( server ) );

		VNicManager vNicManager = new VNicManager();
		vNicManager.setVNicHelper( vnicHelper );
		vNicManager.setPublisher( new VNicMBeanPublisher( server ) );

		EtherstubManager etherstubManager = new EtherstubManager();
		etherstubManager.setEtherstHelper( etherstubHelper );
		etherstubManager.setPublisher( new EtherstubMBeanPublisher( server ) );

		// Create FlowAccounting.

		//FlowAccounting flowAccounting = new FlowAccounting();

		// Create Timer.

		Timer timer = new Timer();
		timer.addNotification("discovery", "discover", null, new Date(), 5000);
		timer.addNotificationListener(flowManager, null, null);
		timer.addNotificationListener(nicManager, null, null);
		timer.addNotificationListener(vNicManager, null, null);
		timer.addNotificationListener(etherstubManager, null, null);
		timer.start();

                // Create ZoneCopier MBean
                ZoneCopierMBean zoneCopier = new ZoneCopier();

		// Register MBeans.

		server.registerMBean(flowManager, new ObjectName("Crossbow:type=FlowManager"));
		// mbs.registerMBean( flowAccounting, new ObjectName( "Crossbow:type=FlowAccounting" ) );
		server.registerMBean(nicManager, new ObjectName("Crossbow:type=NicManager"));
		server.registerMBean(vNicManager, new ObjectName("Crossbow:type=VNicManager"));
		server.registerMBean(etherstubManager, new ObjectName("Crossbow:type=EtherstubManager"));
		server.registerMBean(timer, new ObjectName("Crossbow:type=Timer"));
                server.registerMBean(zoneCopier, new ObjectName("Crossbow:type=ZoneCopier"));

	}


	private static final Logger logger = Logger.getLogger( CrossbowStarter.class );

}
