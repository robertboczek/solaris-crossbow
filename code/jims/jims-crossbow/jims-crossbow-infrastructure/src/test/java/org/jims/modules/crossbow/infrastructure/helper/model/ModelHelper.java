package org.jims.modules.crossbow.infrastructure.helper.model;

import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Machine;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class ModelHelper {

	public static ObjectModel getSimpleModel( String projectId, String SEP ) {

		String machineId = "MYSQL", portId = machineId + SEP + "LINK0", switchId = "SWITCH0";

		Machine m = new Machine( machineId, projectId );
		Port p = new Port( portId, projectId );
		Switch s = new Switch( switchId, projectId );

		m.addPort( p );
		p.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.addPorts( p );
		model.addSwitches( s );
		model.addMachines( m );

		return model;

	}

}
