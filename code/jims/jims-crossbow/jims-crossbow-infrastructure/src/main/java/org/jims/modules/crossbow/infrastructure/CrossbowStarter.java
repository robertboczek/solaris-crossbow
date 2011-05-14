package org.jims.modules.crossbow.infrastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManager;
import org.jims.modules.crossbow.infrastructure.assigner.Assigner;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGatherer;
import org.jims.modules.crossbow.infrastructure.supervisor.Supervisor;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotification;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgressMBean;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgress;
import org.jims.modules.crossbow.infrastructure.supervisor.WorkerProvider;
import org.jims.modules.crossbow.infrastructure.supervisor.vlan.ContiguousVlanTagProvider;
import org.jims.modules.crossbow.infrastructure.supervisor.vlan.VlanTagProvider;
import org.jims.modules.crossbow.infrastructure.worker.Worker;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;
import org.jims.modules.crossbow.util.jmx.MBeanProxyHelper;
import org.jims.modules.crossbow.util.jmx.MBeanProxyHelperFactory;
import org.jims.modules.crossbow.util.jmx.impl.SimpleMBeanProxyHelperFactory;
import org.jims.modules.crossbow.vlan.VlanMBean;
import org.jims.modules.crossbow.vlan.VlanManagerMBean;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;
import org.jims.modules.gds.notification.WorkerNodeAddedNotification;
import org.jims.modules.gds.notification.WorkerNodeRemovedNotification;
import org.jims.modules.sg.service.wnservice.WNDelegateMBean;
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

		VlanManagerMBean vlanManager = JMX.newMBeanProxy(
			server, new ObjectName( "Crossbow:type=VlanManager" ), VlanManagerMBean.class
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

		Worker worker = new Worker( vnicManager, etherstubManager, flowManager, vlanManager,
		                            globalZoneManagement, SolarisCommandFactory.getFactory( SolarisCommandFactory.SOLARIS10 ) );

		server.registerMBean( worker, new ObjectName( "Crossbow:type=XBowWorker" ) );

		WNDelegateMBean wnDelegate = JMX.newMBeanProxy(
			server, new ObjectName( "Core:name=WNDelegate" ), WNDelegateMBean.class
		);

		// StatisticsGatherer MBean

		StatisticsGatherer gatherer = new StatisticsGatherer();
		server.registerMBean( gatherer, new ObjectName( "Crossbow:type=StatisticsGatherer" ) );

		// Crossbow notification MBean
		WorkerProgressMBean workerProgress = new WorkerProgress();
		server.registerMBean(workerProgress, new ObjectName( "Crossbow:type=WorkerProgress" ) );

		// Crossbow notification MBean - @todo crossbowNotification musi sie 
		//zarejestrowac u kazdego WorkerProgressMBean'a
		CrossbowNotificationMBean crossbowNotification = new CrossbowNotification(wnDelegate);
		server.registerMBean(crossbowNotification, new ObjectName( "Crossbow:type=CrossbowNotification" ) );

		Assigner assigner = new Assigner();

		// Supervisor MBean

		WorkerProvider workerProvider = new JmxWorkerProvider( wnDelegate );

		final Supervisor supervisor = new Supervisor( workerProvider, assigner );


		class InfrastructureVlanTagProvider implements ContiguousVlanTagProvider.UsedTagsProvider, NotificationListener {

			public InfrastructureVlanTagProvider( MBeanProxyHelper componentProxyHelper,  WorkerProvider workerProvider ) {
				this.componentProxyHelper = componentProxyHelper;
				this.workerProvider = workerProvider;
			}

			@Override
			public Collection< Integer > provide() {

				Collection< Integer > res = new HashSet< Integer >();

				synchronized ( agents ) {
				
					for ( String url : agents ) {

						componentProxyHelper.setUrl( url );
						VlanManagerMBean vlanManager = componentProxyHelper.createProxy( VlanManagerMBean.class );

						for ( String vlan : vlanManager.getVlans() ) {

							try {
								int tag = vlanManager.getProxyFactory( vlan, VlanMBean.class ).create().getTag();
								logger.info( "Found tag: " + tag );
								res.add( tag );
							} catch ( EntityNotFoundException ex ) {
								logger.error( "VLAN not found (name: " + vlan + ")." );
							}

						}
					
					}
				
				}

				logger.info( res.size() + " VLAN tag(s) have already been reserved." );

				return res;

			}

			@Override
			public void handleNotification( Notification notification, Object handback ) {

				if ( ( notification instanceof WorkerNodeAddedNotification )
				     || ( notification instanceof WorkerNodeRemovedNotification ) ) {

					synchronized ( agents ) {
						agents.clear();
						agents.addAll( workerProvider.getWorkers().keySet() );
					}

				}

			}


			Collection< Integer > usedTags;
			private final Collection< String > agents = new LinkedList< String >();
			private final MBeanProxyHelper componentProxyHelper;
			private final WorkerProvider workerProvider;

		}


		MBeanProxyHelperFactory simpleProxyHelperFactory = new SimpleMBeanProxyHelperFactory();

		final InfrastructureVlanTagProvider usedTagsProvider = new InfrastructureVlanTagProvider(
			simpleProxyHelperFactory.getManagerProxyFactory(), workerProvider
		);

		VlanTagProvider tagProvider = new ContiguousVlanTagProvider(
			900, 931, usedTagsProvider
		);

		// TODO  ^ read the range from properties file!

		supervisor.setTagProvider( tagProvider );

		server.registerMBean( supervisor, new ObjectName( "Crossbow:type=Supervisor" ) );

		try {

			server.addNotificationListener( new ObjectName( "Core:name=GWEmitter" ), supervisor, null, null );
			server.addNotificationListener( new ObjectName( "Core:name=GWEmitter" ), usedTagsProvider, null, null );

		} catch ( InstanceNotFoundException ex ) {
			logger.info( "Ignoring InstanceNotFoundException. Probably not a gateway." );
		}

		// RepoManager

		RepoManager repoManager = new RepoManager();
		server.registerMBean( repoManager, new ObjectName( "Crossbow:type=RepoManager" ) );

	}


	private static final Logger logger = Logger.getLogger( CrossbowStarter.class );

}
