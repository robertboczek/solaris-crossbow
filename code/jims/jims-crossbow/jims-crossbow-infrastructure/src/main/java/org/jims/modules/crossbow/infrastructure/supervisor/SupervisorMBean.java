package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.Map;
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

	public Map< String, ObjectModel > discover();

}
