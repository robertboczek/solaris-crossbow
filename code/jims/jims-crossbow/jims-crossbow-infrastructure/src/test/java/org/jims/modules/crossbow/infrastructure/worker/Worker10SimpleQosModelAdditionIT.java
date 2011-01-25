package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.junit.Test;


/**
 *
 * @author cieplik
 */
public class Worker10SimpleQosModelAdditionIT extends WorkerITBase {

	@Test
	public void go() {

		ObjectModel model = ModelHelper.getSimpleQosModel( projectId, SEP );

		String etherstubId = projectId + SEP + model.getSwitches().get( 0 ).getResourceId();

		Assignments assignments = new Assignments();
		assignments.setAssignment( model.getPorts().get( 0 ), etherstubId );

		worker.instantiate( model, new Actions(), assignments );

	}

}