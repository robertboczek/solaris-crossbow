package org.jims.modules.crossbow.infrastructure.supervisor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jims.modules.crossbow.infrastructure.supervisor.vlan.VlanTagProvider;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.util.struct.Pair;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.ApplianceAnnotation;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.InterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.VlanApplianceAnnotation;
import org.jims.modules.crossbow.objectmodel.VlanInterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.RoutingTable;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.gds.notification.WorkerNodeAddedNotification;

import org.jims.modules.gds.notification.WorkerNodeRemovedNotification;


/**
 *
 * @author cieplik
 */
public class Supervisor implements SupervisorMBean, NotificationListener {

	private static class InstantiationTask implements Runnable {

		public InstantiationTask( WorkerMBean worker, String workerId,
		                          ObjectModel model, Actions actions, Assignments assignments ) {
			this.worker = worker;
			this.workerId = workerId;
			this.model = model;
			this.actions = actions;
			this.assignments = assignments;
		}


		@Override
		public void run() {

			try {
				worker.instantiate( model, actions, assignments );
			} catch ( ModelInstantiationException ex ) {
				logger.error( "Error while instantiating the model. (worker: "
				              + workerId + ")", ex );
			}

			logger.info( "Instantiation finished (worker: " + workerId + ")." );

		}

		private final WorkerMBean worker;
		private final String workerId;

		private final ObjectModel model;
		private final Actions actions;
		private final Assignments assignments;

	}


	public Supervisor( WorkerProvider workerProvider, AssignerMBean assigner ) {
		this.workerProvider = workerProvider;
		this.assigner = assigner;
	}


	@Override
	public void instantiate( ObjectModel model, Actions actions ) throws ModelInstantiationException {

		Assignments assignments = assigner.assign( model );
		WorkerMBean worker = workers.values().iterator().next();

		worker.instantiate( model, actions, assignments );

	}


	@Override
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) {

		// Adjust the model.

		Map< Appliance, Collection< Interface > > parts = splitRouters( model, actions, assignments );

		// Now, create VLAN interface assignments, if applicable.

		createVlanAssignments( parts, actions, assignments );

		// The model has been adjusted, instantiate the topology.

		synchronized ( workers ) {

			// Send parts of the model to corresponding workers.

			List< Thread > threads = new LinkedList< Thread >();

			for ( Map.Entry< String, WorkerMBean > entry : workers.entrySet() ) {

				String workerId = entry.getKey();
				WorkerMBean worker = entry.getValue();

				List< Object > objs = assignments.filterByTarget( entry.getKey() );

				if ( 0 < objs.size() ) {

					Thread t = new Thread( new InstantiationTask(
						worker, workerId, model, actions.filterByKeys( objs ), assignments ) );

					t.start();

					threads.add( t );

				} else {
					logger.debug( "No model to instantiate for " + workerId );
				}

			}

			// Wait for all the workers till they finish instantiation.

			for ( Thread t : threads ) {
				try {
					t.join();
				} catch ( InterruptedException ex ) {
					logger.error( "Exception while joining thread.", ex );
				}
			}

		}

	}


	@Override
	public Map< String, Pair< ObjectModel, Assignments > > discover() {

		Map< String, Pair< ObjectModel, Assignments > > projects
			= new HashMap< String, Pair< ObjectModel, Assignments > >();

		synchronized ( workers ) {

			for ( Map.Entry< String, WorkerMBean > workerEntry : workers.entrySet() ) {

				WorkerMBean worker = workerEntry.getValue();
				String workerId = workerEntry.getKey();

				for ( Map.Entry< String, Pair< ObjectModel, Assignments > > entry : worker.discover().entrySet() ) {

					String project = entry.getKey();
					final ObjectModel part = entry.getValue().first;

					logger.info( project + " discovered on " + workerId );

					if ( null == projects.get( project ) ) {
						projects.put( project, new Pair< ObjectModel, Assignments >( new ObjectModel(), new Assignments() ) );
					}

					ObjectModel model = projects.get( project ).first;
					Assignments assignments = projects.get( project ).second;

					model.addAll( part );

					if ( null != entry.getValue().second ) {
						assignments.putAll( entry.getValue().second );
					}

					for ( Object o : new LinkedList< Object >() {{ addAll( part.getAppliances() );
					                                               addAll( part.getInterfaces() );
					                                               addAll( part.getPolicies() );
					                                               addAll( part.getSwitches() ); }} ) {
						assignments.put( o, workerId );
					}

				}

			}

		}

		for ( Map.Entry< String, Pair< ObjectModel, Assignments > > entry : projects.entrySet() ) {
			joinRouters( entry.getKey(), entry.getValue().first, entry.getValue().second );
		}

		return projects;

	}


	@Override
	public List< String > getWorkers() {
		synchronized ( workers ) {
			return new LinkedList< String >( workers.keySet() );
		}
	}

	public List< WorkerMBean > getWorkersMBean() {
		synchronized ( workers ) {
			return new LinkedList< WorkerMBean >( workers.values() );
		}
	}


	@Override
	public void handleNotification( Notification notification, Object handback ) {

		if ( ( notification instanceof WorkerNodeAddedNotification )
		     || ( notification instanceof WorkerNodeRemovedNotification ) ) {

			logger.info( "Refreshing the list of workers." );

			// Refresh the list of workers.

			synchronized ( workers ) {
				workers.clear();
				workers.putAll( workerProvider.getWorkers() );
			}

		}

	}


	void joinRouters( String project, ObjectModel model, Assignments assignments ) {

		Multimap< Integer, Interface > vlans = HashMultimap.create();

		for ( Appliance app : model.getAppliances( ApplianceType.ROUTER ) ) {

			for ( Interface iface : app.getInterfaces() ) {

				InterfaceAssignment annotation = assignments.getAnnotation( iface );

				if ( ( null != annotation ) && ( annotation instanceof VlanInterfaceAssignment ) ) {
					// The router is going to be joined.
					vlans.put( ( ( VlanInterfaceAssignment ) annotation ).getTag(), iface );
					assignments.removeAnnotation( iface );
				}

			}

		}

		if ( 0 < vlans.size() ) {
			logger.info( vlans.keySet().size() + " VLAN(s) / router(s) identified (project: "
			             + project + ")." );
		} else {
			logger.debug( "No VLAN / router indentified (project: " + project + ")." );
		}

		// We're now able to join the routers and remove VLAN interfaces entirely.

		for ( Map.Entry< Integer, Collection< Interface > > entry : vlans.asMap().entrySet() ) {

			Collection< Interface > ifaces = entry.getValue();
			Appliance part = ifaces.iterator().next().getAppliance();

			// Create new, consolidated router (the assignment doesn't matter)...

			Appliance router = new Appliance( part.getResourceId(), part.getProjectId(),
			                                  ApplianceType.ROUTER, part.getRepoId() );
			model.register( router );

			// ... mark it with annotation (VLAN tag assignment) ...

			assignments.putAnnotation( router, new VlanApplianceAnnotation( entry.getKey() ) );

			// ... and steal the interfaces.

			for ( Interface vlan : ifaces ) {

				// Iterate through the VLAN interfaces, retrieve the router and then
				// reassign all non-VLAN interfaces to the joined router.

				for ( Interface iface : vlan.getAppliance().getInterfaces() ) {
					if ( vlan != iface ) {
						router.addInterface( iface );
					}
				}

				model.remove( vlan.getAppliance() );

			}

		}

	}


	Map< Appliance, Collection< Interface > > splitRouters( ObjectModel model, Actions actions, Assignments assignments ) {

		logger.debug( "Splitting routers connecting multiple workers." );

		Map< Appliance, Collection< Interface > > res = new HashMap< Appliance, Collection< Interface > >();

		// Inspect the routers to see if they connect subnets with different assignments.
		// E.g.  [subnet A; assign: w0] -- R -- [subnet B; assign: w1]

		List< Object > toreg = new LinkedList< Object >();
		List< Object > torem = new LinkedList< Object >();

		Iterator< Appliance > it = model.getAppliances( ApplianceType.ROUTER ).iterator();
		while ( it.hasNext() ) {

			Appliance app = it.next();

			Set< String > targets = new HashSet< String >();
			Actions.Action action = actions.get( app );

			for ( Interface iface : app.getInterfaces() ) {
				if ( iface.getEndpoint() instanceof Switch ) {
					targets.add( assignments.get( ( Switch ) iface.getEndpoint() ) );
				}
			}

			if ( targets.size() > 1 ) {

				logger.info( "A router is going to be split (id: " + app.getResourceId()
				             + ", parts: " + targets.size() + ")." );

				// The router connects subnets assigned to different workers.
				// Create new router appliance for each worker.

				Collection< Interface > vlans = new LinkedList< Interface >();

				Map< Interface, Interface > ifaceToVlanMap = new HashMap< Interface, Interface >();
				List< Appliance > subRouters = new LinkedList< Appliance >();

				int i = 1;
				for ( String target : targets ) {

					Appliance router = new Appliance( app.getResourceId(), app.getProjectId(),
					                                  app.getType(), app.getRepoId() );

					// Add one more interface used for internal router communication.

					Interface vlan = new Interface( "INTRA0", app.getProjectId(), null,
					                                new IpAddress( "200.0.0." + i++, 24 ) );
					router.addInterface( vlan );

					for ( Interface iface : app.getInterfaces() ) {

						if ( ( iface.getEndpoint() instanceof Switch )
						     && ( target.equals( assignments.get( ( Switch ) iface.getEndpoint() ) ) ) ) {
							router.addInterface( iface );
						}

						if ( target.equals( assignments.get( ( Switch ) iface.getEndpoint() ) ) ) {
							ifaceToVlanMap.put( iface, vlan );
						}

					}

					toreg.add( vlan );
					actions.put( vlan, action );
					assignments.put( vlan, target );

					toreg.add( router );
					actions.put( router, action );
					assignments.put( router, target );

					vlans.add( vlan );
					subRouters.add( router );

				}

				// Now, refine routing tables.

				logger.info( "ifaceToVlanMap size: " + ifaceToVlanMap.size() );
				logger.info( "subRouters size: " + subRouters.size() );

				for ( Appliance subRouter : subRouters ) {

					RoutingTable routingTable = subRouter.getRoutingTable();

					for ( Map.Entry< Interface, Interface > entry : ifaceToVlanMap.entrySet() ) {

						Interface iface = entry.getKey();
						Interface vlan = entry.getValue();

						logger.info( assignments.get( iface ) );
						logger.info( assignments.get( subRouter ) );
					
						if ( ! assignments.get( subRouter ).equals( assignments.get( iface ) ) ) {
							routingTable.routeAdd( iface.getIpAddress(), vlan.getIpAddress() );
							// TODO  v make it at most debug
							logger.info( "Route to " + iface.getIpAddress() + " added (gateway: "
							             + vlan.getIpAddress() + ")." );
						}
					
					}

				}

				torem.add( app );
				actions.remove( app );
				assignments.remove( app );

				res.put( app, vlans );

			}

		}

		for ( Object o : torem ) {
			model.remove( ( Appliance ) o );
		}

		model.registerAll( toreg );

		logger.debug( "Splitting done (splits: " + res.size() + ")." );

		return res;

	}


	void createVlanAssignments( Map< Appliance, Collection< Interface > > parts,
	                            Actions actions, Assignments assignments ) {

		for ( Map.Entry< Appliance, Collection< Interface > > entry : parts.entrySet() ) {

			ApplianceAnnotation annotation = assignments.getAnnotation( entry.getKey() );

			if ( null == annotation ) {
				assignments.putAnnotation( entry.getKey(),
				                           new VlanApplianceAnnotation( tagProvider.provide() ) );
			}

			InterfaceAssignment interfaceAssignment = new VlanInterfaceAssignment(
				( ( VlanApplianceAnnotation ) assignments.getAnnotation( entry.getKey() ) ).getTag()
			);

			for ( Interface iface : entry.getValue() ) {
				assignments.putAnnotation( iface, interfaceAssignment );
			}

		}

	}


	public AssignerMBean getAssigner() {
		return assigner;
	}

	public void setAssigner( AssignerMBean assigner ) {
		this.assigner = assigner;
	}

	public void setTagProvider( VlanTagProvider tagProvider ) {
		this.tagProvider = tagProvider;
	}

	void addWorker( String id, WorkerMBean worker ) {
		workers.put( id, worker );
	}


	private AssignerMBean assigner;
	private WorkerProvider workerProvider;
	private final Map< String, WorkerMBean > workers = new HashMap< String, WorkerMBean >();
	private VlanTagProvider tagProvider;

	private static final Logger logger = Logger.getLogger( Supervisor.class );

}
