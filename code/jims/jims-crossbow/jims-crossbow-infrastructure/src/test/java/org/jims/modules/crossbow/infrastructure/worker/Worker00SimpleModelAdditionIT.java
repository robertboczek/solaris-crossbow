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

		actions.put( model.getAppliances().get( 0 ), Actions.Action.ADD );
		actions.put( model.getInterfaces().get( 0 ), Actions.Action.ADD );
		actions.put( model.getSwitches().get( 0 ), Actions.Action.ADD );

		worker.instantiate( model, actions, new Assignments() );

	}

}