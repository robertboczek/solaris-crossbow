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
public class Worker02SimpleModelSwitchRemovalIT extends WorkerITBase {

	@Test
	public void go() {

		ObjectModel model = ModelHelper.getSimpleModel( projectId, SEP );
		model.getPorts().clear();

		Actions actions = new Actions();
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.REM );

		worker.instantiate( model, actions, new Assignments() );

	}

}