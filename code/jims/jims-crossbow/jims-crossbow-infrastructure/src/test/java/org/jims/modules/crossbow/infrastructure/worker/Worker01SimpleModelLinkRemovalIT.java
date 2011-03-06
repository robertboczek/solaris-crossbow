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
public class Worker01SimpleModelLinkRemovalIT extends WorkerITBase {

	@Test
	public void go() throws Exception {

		ObjectModel model = ModelHelper.getSimpleModel( projectId, SEP );
		model.getSwitches().clear();

		Actions actions = new Actions();
		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.REM );

		worker.instantiate( model, actions, new Assignments() );

	}

}