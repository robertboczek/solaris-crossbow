package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.VlanInterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.gds.notification.WorkerNodeAddedNotification;
import org.jims.modules.gds.notification.WorkerNodeRemovedNotification;


/**
 *
 * @author cieplik
 */
public class Supervisor implements SupervisorMBean, NotificationListener {

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

		// TODO  concurrent instantiation

		// Adjust the model.

		Collection< Map< Interface, String > > parts = splitRouters( model, actions, assignments );

		// Now, create VLAN interface assignments, if applicable.

		createVlanAssignments( parts, actions, assignments );

		// The model has been adjusted, instantiate the topology.

		synchronized ( workers ) {

			// Send parts of the model to corresponding workers.

			for ( Map.Entry< String, WorkerMBean > entry : workers.entrySet() ) {

				try {

					List< Object > objs = assignments.filterByTarget( entry.getKey() );
					entry.getValue().instantiate( model, actions.filterByKeys( objs ), assignments );

				} catch ( ModelInstantiationException ex ) {

					logger.error( "Error while instantiating the model. (worker: "
					              + entry.getKey() + ")", ex );

				}

			}

		}

	}


	@Override
	public Map< String, ObjectModel > discover() {

		WorkerMBean worker = workers.values().iterator().next();

		return worker.discover();

	}


	@Override
	public List< String > getWorkers() {

		synchronized ( workers ) {
			return new LinkedList< String >( workers.keySet() );
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


	Collection< Map< Interface, String > > splitRouters( ObjectModel model, Actions actions, Assignments assignments ) {

		logger.debug( "Splitting routers connecting multiple workers." );

		Collection< Map< Interface, String > > res = new LinkedList< Map< Interface, String > >();

		// Inspect the routers to see if they connect subnets with different assignments.
		// E.g.  [subnet A; assign: w0] -- R -- [subnet B; assign: w1]

		List< Appliance > torem = new LinkedList< Appliance >();
		List< Object > toreg = new LinkedList< Object >();

		for ( Appliance app : model.getAppliances() ) {

			if ( ApplianceType.ROUTER.equals( app.getType() ) ) {

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

					Map< Interface, String > desc = new HashMap< Interface, String >();

					int i = 1;
					for ( String target : targets ) {

						Appliance router = new Appliance( app.getResourceId(), app.getProjectId(),
						                                  app.getType(), app.getRepoId() );

						for ( Interface iface : app.getInterfaces() ) {
							if ( ( iface.getEndpoint() instanceof Switch )
							     && ( target.equals( assignments.get( ( Switch ) iface.getEndpoint() ) ) ) ) {
								router.addInterface( iface );
							}
						}

						// Add one more interface used for internal router communication.

						Interface vlan = new Interface( "INTRA0", app.getProjectId(), null,
						                                new IpAddress( "200.0.0." + i++, 24 ) );
						router.addInterface( vlan );

						toreg.add( vlan );
						actions.put( vlan, action );
						assignments.put( vlan, target );
						desc.put( vlan, target );

						toreg.add( router );
						actions.put( router, action );
						assignments.put( router, target );

					}

					res.add( desc );
					torem.add( app );

				}

			}

		}

		for ( Appliance app : torem ) {
			model.remove( app );
			actions.remove( app );
			assignments.remove( app );
		}

		model.registerAll( toreg );

		logger.debug( "Splitting done (splits: " + res.size() + ")." );

		return res;

	}


	void createVlanAssignments( Collection< Map< Interface, String > > parts, Actions actions, Assignments assignments ) {

		for ( Map< Interface, String > vlan : parts ) {

			if ( Actions.Action.ADD.equals( actions.get( vlan ) ) ) {

				int tag = -1;
				VlanInterfaceAssignment assign = null;

				for ( Map.Entry< Interface, String > entry : vlan.entrySet() ) {

					Interface iface = entry.getKey();

					if ( null == assignments.getAnnotation( iface ) ) {

						if ( -1 == tag ) {
							tag = 905;  // TODO  < this is temporary
							// tag = tagProvider.provide();  // TODO  uncomment
							assign = new VlanInterfaceAssignment( tag );  // nulls are default and set by the worker
						}

						assignments.putAnnotation( iface, assign );

						logger.info( "new assignment " + assign );

					}

				}

			}

		}

	}


	public AssignerMBean getAssigner() {
		return assigner;
	}

	public void setAssigner( AssignerMBean assigner ) {
		this.assigner = assigner;
	}

	void addWorker( String id, WorkerMBean worker ) {
		workers.put( id, worker );
	}


	private AssignerMBean assigner;
	private WorkerProvider workerProvider;
	private final Map< String, WorkerMBean > workers = new HashMap< String, WorkerMBean >();

	private static final Logger logger = Logger.getLogger( Supervisor.class );

}
