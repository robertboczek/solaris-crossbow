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
public class Worker11SimpleQosModelRemovalIT extends WorkerITBase {

	@Test
	public void go() throws Exception {

		ObjectModel model = ModelHelper.getSimpleQosModel( projectId, SEP );

		String etherstubId = projectId + SEP + model.getSwitches().get( 0 ).getResourceId();

		Actions actions = new Actions();

		actions.put( model.getSwitches().get( 0 ), Actions.Action.REM );
		actions.put( model.getInterfaces().get( 0 ), Actions.Action.REM );
		actions.put( model.getPolicies().get( 0 ), Actions.Action.REM );

		worker.instantiate( model, actions, new Assignments() );

	}

}