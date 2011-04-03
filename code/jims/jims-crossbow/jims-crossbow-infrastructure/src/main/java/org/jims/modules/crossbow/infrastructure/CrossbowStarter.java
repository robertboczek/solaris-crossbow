package org.jims.modules.crossbow.infrastructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManager;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManagerMBean;
import org.jims.modules.crossbow.infrastructure.assigner.Assigner;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGatherer;
import org.jims.modules.crossbow.infrastructure.supervisor.Supervisor;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotification;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgressMBean;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgress;
import org.jims.modules.crossbow.infrastructure.worker.Worker;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;
import org.jims.modules.solaris.commands.SolarisCommandFactory;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagementMBean;


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

		GlobalZoneManagementMBean globalZoneManagement = MBeanServerInvocationHandler.newProxyInstance(
			server,
			new ObjectName( "solaris10.management.global:type=ZoneManager,role=management" ),
			GlobalZoneManagementMBean.class,
			false
		);

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

		// RepoManagerMBean repoManager = JMX.newMBeanProxy(
		// 	server, new ObjectName( "Crossbow:type=RepoManager" ), RepoManagerMBean.class
		// );


		// Register MBeans.

		Worker worker = new Worker( vnicManager, etherstubManager, flowManager,
		                            globalZoneManagement, SolarisCommandFactory.getFactory( SolarisCommandFactory.SOLARIS10 ) );

		server.registerMBean( worker, new ObjectName( "Crossbow:type=XBowWorker" ) );

		// StatisticsGatherer MBean

		StatisticsGatherer gatherer = new StatisticsGatherer();
		gatherer.setvNicManager( vnicManager );

		server.registerMBean( gatherer, new ObjectName( "Crossbow:type=StatisticsGatherer" ) );

		// Crossbow notification MBean
		WorkerProgressMBean workerProgress = new WorkerProgress();
		server.registerMBean(workerProgress, new ObjectName( "Crossbow:type=WorkerProgress" ) );

		// Crossbow notification MBean - @todo crossbowNotification musi sie 
		//zarejestrowac u kazdego WorkerProgressMBean'a
		CrossbowNotificationMBean crossbowNotification = new CrossbowNotification(3); //3 etapy (usuniecie, instlacja, update ) * ilosc workerwow - narazie 1
		server.registerMBean(crossbowNotification, new ObjectName( "Crossbow:type=CrossbowNotification" ) );

		// Supervisor MBean
		Assigner assigner = new Assigner();

		Supervisor supervisor = new Supervisor();
		supervisor.setCrossbowNotificationMBean( crossbowNotification );

		supervisor.addWorker( "", worker );
		supervisor.setAssigner( assigner );

		server.registerMBean( supervisor, new ObjectName( "Crossbow:type=Supervisor" ) );

		// RepoManager

		RepoManager repoManager = new RepoManager();
		server.registerMBean( repoManager, new ObjectName( "Crossbow:type=RepoManager" ) );

	}


	private static final Logger logger = Logger.getLogger( CrossbowStarter.class );

}
