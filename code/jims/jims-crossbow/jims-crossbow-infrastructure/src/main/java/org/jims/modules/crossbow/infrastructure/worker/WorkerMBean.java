package org.jims.modules.crossbow.infrastructure.worker;

import java.util.Map;
import org.jims.modules.crossbow.util.struct.Pair;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public interface WorkerMBean {

	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) throws ModelInstantiationException;

	/**
	 * Discovers projects that have been instantiated before.
	 *
	 * @return  project -> object-model map
	 */
	public Map< String, Pair< ObjectModel, Assignments > > discover();


	public void _discover();

}
