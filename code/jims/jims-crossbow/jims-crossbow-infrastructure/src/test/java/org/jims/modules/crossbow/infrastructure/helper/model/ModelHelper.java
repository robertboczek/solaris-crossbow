package org.jims.modules.crossbow.infrastructure.helper.model;

import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
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


	public static ObjectModel getSimpleQosModel( String projectId, String SEP ) {

		ObjectModel model = getSimpleModel( projectId, SEP );

		// Add QoS parameters

		model.getPorts().get( 0 ).addPolicy(
			new PriorityPolicy( PriorityPolicy.Priority.HIGH,
			                    new IpFilter( new IpAddress( "1.2.3.4", 24 ), IpFilter.Location.LOCAL ) )
		);

		model.addPolicies( model.getPorts().get( 0 ).getPoliciesList().toArray( new Policy[ 1 ] ) );

		return model;

	}

}
