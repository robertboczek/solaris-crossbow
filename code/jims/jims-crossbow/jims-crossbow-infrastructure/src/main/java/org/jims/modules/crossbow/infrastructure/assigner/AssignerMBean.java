package org.jims.modules.crossbow.infrastructure.assigner;

import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


/**
 *
 * @author cieplik
 */
public interface AssignerMBean {

	public Assignments assign( ObjectModel model );

}
