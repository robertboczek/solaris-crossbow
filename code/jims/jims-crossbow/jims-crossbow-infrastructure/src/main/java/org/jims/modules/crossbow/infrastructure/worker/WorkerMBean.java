package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public interface WorkerMBean {

	public void instantiate( ObjectModel model, Actions actions, Assignments assignments );

	public void discover();

}
