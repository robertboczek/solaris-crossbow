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
public class Worker30CommunicationModelAdditionIT extends WorkerITBase {

	@Test
	public void go() throws Exception {

		ObjectModel model = ModelHelper.getSimpleSwitchAndPolicyModel( "COMM" );

		Actions actions = new Actions();
		actions.insert( model.getAppliances().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getAppliances().get( 1 ), Actions.ACTION.ADD );
		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getPorts().get( 1 ), Actions.ACTION.ADD );
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getPolicies().get( 0 ), Actions.ACTION.ADD );

		worker.instantiate( model, actions, new Assignments() );

	}

}