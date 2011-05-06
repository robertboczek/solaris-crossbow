package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.junit.Test;


/**
 *
 * @author cieplik
 */
public class Worker40SimpleRouterModelAdditionIT extends WorkerITBase {

	@Test
	public void go() throws Exception {

		ObjectModel model = ModelHelper.getSimpleRouterModel( "R" );

		Actions actions = new Actions();

		for ( Appliance app : model.getAppliances() ) {
			actions.put( app, Actions.Action.ADD );
		}

		for ( Interface iface : model.getInterfaces() ) {
			actions.put( iface, Actions.Action.ADD );
		}

		for ( Switch s : model.getSwitches() ) {
			actions.put( s, Actions.Action.ADD );
		}


		worker.instantiate( model, actions, new Assignments() );

	}

}