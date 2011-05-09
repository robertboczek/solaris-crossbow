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
		actions.put( model.getAppliances().get( 0 ), Actions.Action.ADD );
		actions.put( model.getAppliances().get( 1 ), Actions.Action.ADD );
		actions.put( model.getInterfaces().get( 0 ), Actions.Action.ADD );
		actions.put( model.getInterfaces().get( 1 ), Actions.Action.ADD );
		actions.put( model.getSwitches().get( 0 ), Actions.Action.ADD );
		actions.put( model.getPolicies().get( 0 ), Actions.Action.ADD );

		worker.instantiate( model, actions, new Assignments() );

	}

}