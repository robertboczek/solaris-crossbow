package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
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


	public void addWorker( String id, WorkerMBean worker ) {
		workers.put( id, worker );
	}

	public AssignerMBean getAssigner() {
		return assigner;
	}

	public void setAssigner( AssignerMBean assigner ) {
		this.assigner = assigner;
	}

	private AssignerMBean assigner;
	private WorkerProvider workerProvider;
	private final Map< String, WorkerMBean > workers = new HashMap< String, WorkerMBean >();

	private static final Logger logger = Logger.getLogger( Supervisor.class );

}
