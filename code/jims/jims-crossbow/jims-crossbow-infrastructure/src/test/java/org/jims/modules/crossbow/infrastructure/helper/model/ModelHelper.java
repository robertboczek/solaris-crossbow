package org.jims.modules.crossbow.infrastructure.helper.model;

import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class ModelHelper {

	/**
	 * Creates and returns simple model:
	 *
	 * M-P--S
	 *
	 * @param projectId
	 * @param SEP
	 * @return
	 */
	public static ObjectModel getSimpleModel( String projectId, String SEP ) {

		Appliance m = new Appliance( "MYSQL", projectId, ApplianceType.MACHINE );
		Interface p = new Interface( "LINK0", projectId );
		Switch s = new Switch( "SWITCH0", projectId );

		m.addInterface( p );
		p.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.register( p );
		model.register( s );
		model.register( m );

		return model;

	}


	public static ObjectModel getSimpleQosModel( String projectId, String SEP ) {

		ObjectModel model = getSimpleModel( projectId, SEP );

		// Add QoS parameters

		model.getPorts().get( 0 ).addPolicy( model.register(
			new PriorityPolicy( "APOLICY0",
			                    PriorityPolicy.Priority.HIGH,
			                    new IpFilter( new IpAddress( "1.2.3.4", 24 ), IpFilter.Location.LOCAL ) )
		) );

		return model;

	}


	public static ObjectModel getSimpleQoSAnyFilter( String projectId, String SEP ) {

		ObjectModel model = getSimpleModel( projectId, SEP );

		model.getPorts().get( 0 ).addPolicy( model.register(
			new PriorityPolicy( "somepolicy13", PriorityPolicy.Priority.LOW, new AnyFilter() )
		) );

		return model;

	}


	public static ObjectModel getApplianceModel( String projectId, String SEP ) {

		Appliance app = new Appliance( "MY.SERVER", projectId, ApplianceType.MACHINE );
		app.setRepoId( "dummy" );

		ObjectModel model = new ObjectModel();
		model.register( app );

		return model;

	}

}
