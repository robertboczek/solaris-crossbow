package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public class Supervisor implements SupervisorMBean {

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
	private Map< String, WorkerMBean > workers = new HashMap< String, WorkerMBean >();

}
