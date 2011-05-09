package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.List;
import java.util.Map;
import org.jims.modules.crossbow.infrastructure.Pair;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public interface SupervisorMBean {

	public void instantiate( ObjectModel model, Actions actions ) throws ModelInstantiationException;
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments );

	public Map< String, Pair< ObjectModel, Assignments > > discover();

	public List< String > getWorkers();

}
