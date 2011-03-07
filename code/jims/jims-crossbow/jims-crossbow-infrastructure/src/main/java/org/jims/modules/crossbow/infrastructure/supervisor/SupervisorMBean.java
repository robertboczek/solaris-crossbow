package org.jims.modules.crossbow.infrastructure.supervisor;

import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public interface SupervisorMBean {

	public void instantiate( ObjectModel model, Actions actions );
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments );

	public ObjectModel discover();

}
