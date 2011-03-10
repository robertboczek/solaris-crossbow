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
public class Worker00SimpleModelAdditionIT extends WorkerITBase {

	@Test
	public void go() throws Exception {

		ObjectModel model = ModelHelper.getSimpleModel( projectId, SEP );

		Actions actions = new Actions();

		actions.insert( model.getAppliances().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.ADD );

		worker.instantiate( model, actions, new Assignments() );

	}

}