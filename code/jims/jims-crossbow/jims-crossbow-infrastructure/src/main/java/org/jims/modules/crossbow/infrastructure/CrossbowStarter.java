package org.jims.modules.crossbow.infrastructure;

import java.util.ArrayList;
import java.util.Iterator;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.infrastructure.worker.Worker;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;


/**
 * This is a bootstrap class used to initialize the Crossbow Infrastructure modules.
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


		FlowManagerMBean flowManager = JMX.newMBeanProxy(
			server, new ObjectName( "Crossbow:type=FlowManager" ), FlowManagerMBean.class
		);

		VNicManagerMBean vnicManager = JMX.newMBeanProxy(
			server, new ObjectName( "Crossbow:type=VNicManager" ), VNicManagerMBean.class
		);

		EtherstubManagerMBean etherstubManager = JMX.newMBeanProxy(
			server, new ObjectName( "Crossbow:type=EtherstubManager" ), EtherstubManagerMBean.class
		);

		ZoneCopierMBean zoneCopier = JMX.newMBeanProxy(
			server, new ObjectName( "Crossbow:type=ZoneCopier" ), ZoneCopierMBean.class
		);


		// Register MBeans.

		Worker worker = new Worker( vnicManager, etherstubManager, flowManager, zoneCopier );

		server.registerMBean( worker, new ObjectName( "Crossbow:type=XBowWorker" ) );

	}


	private static final Logger logger = Logger.getLogger( CrossbowStarter.class );

}