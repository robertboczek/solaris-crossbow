package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.List;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.junit.Test;


/**
 *
 * @author cieplik
 */
public class Supervisor00SimplestRouterMultipleWorkersAdditionIT extends SupervisorITBase {

	@Test
	public void go() throws Exception {

		List< String > workers = supervisor.getWorkers();

		assert ( 1 < workers.size() );

		ObjectModel model = ModelHelper.getSimplestRouterModel( projectId );

		Actions actions = new Actions();

		actions.put( model.getAppliances().get( 0 ), Actions.Action.ADD );
		actions.put( model.getInterfaces().get( 0 ), Actions.Action.ADD );
		actions.put( model.getInterfaces().get( 1 ), Actions.Action.ADD );
		actions.put( model.getSwitches().get( 0 ), Actions.Action.ADD );
		actions.put( model.getSwitches().get( 1 ), Actions.Action.ADD );

		Assignments assignments = new Assignments();

		assignments.put( model.getRouters().get( 0 ), workers.get( 0 ) );

		assignments.put( model.getInterfaces().get( 0 ), workers.get( 0 ) );
		assignments.put( model.getInterfaces().get( 1 ), workers.get( 1 ) );

		assignments.put( model.getSwitches().get( 0 ), workers.get( 0 ) );
		assignments.put( model.getSwitches().get( 1 ), workers.get( 1 ) );

		supervisor.instantiate( model, actions, assignments );

	}

}