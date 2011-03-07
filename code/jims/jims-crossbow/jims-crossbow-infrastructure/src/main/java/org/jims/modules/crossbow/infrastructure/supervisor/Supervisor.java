package org.jims.modules.crossbow.infrastructure.supervisor;

import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public class Supervisor implements SupervisorMBean {

	@Override
	public void instantiate( ObjectModel model, Actions actions ) {
	}


	@Override
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) {
	}

	@Override
	public ObjectModel discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
