package org.jims.modules.crossbow.infrastructure.helper.model;

import java.util.Arrays;
import java.util.LinkedList;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
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
		Interface i = new Interface( "LINK0", projectId );
		Switch s = new Switch( "SWITCH0", projectId );

		m.setRepoId( "dummy" );
		m.addInterface( i );
		i.setIpAddress( new IpAddress( "192.168.13.13", 24 ) );
		i.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.register( i );
		model.register( s );
		model.register( m );

		return model;

	}


	public static ObjectModel getSimpleQosModel( String projectId, String SEP ) {

		ObjectModel model = getSimpleModel( projectId, SEP );

		// Add QoS parameters

		model.getInterfaces().get( 0 ).addPolicy( model.register(
			new PriorityPolicy( "APOLICY0",
			                    PriorityPolicy.Priority.HIGH,
			                    new IpFilter( new IpAddress( "1.2.3.4", 24 ), IpFilter.Location.LOCAL ) )
		) );

		return model;

	}


	public static ObjectModel getSimpleQoSAnyFilter( String projectId, String SEP ) {

		ObjectModel model = getSimpleModel( projectId, SEP );

		model.getInterfaces().get( 0 ).addPolicy( model.register(
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


	public static ObjectModel getSimpleSwitchAndPolicyModel( String projectId ) {

		Switch s = new Switch( "SWITCH13", projectId );

		Policy p = new PriorityPolicy( "TEST.POLICY", PriorityPolicy.Priority.LOW,
		                               new IpFilter( new IpAddress( "192.169.18.2", 24 ), IpFilter.Location.REMOTE ) );
		Interface firstIface = new Interface( "IFACE0", projectId, s, new LinkedList< Policy >(), new IpAddress( "192.168.18.1", 24 ) );
		firstIface.addPolicy( p );

		Appliance app1 = new Appliance( "FIRST", projectId, ApplianceType.MACHINE, "dummy" );
		app1.addInterface( firstIface );

		Interface secondIface = new Interface( "IFACE0", projectId, s, new LinkedList< Policy >(), new IpAddress( "192.168.18.2", 24 ) );

		Appliance app2 = new Appliance( "SECOND", projectId, ApplianceType.MACHINE, "dummy" );
		app2.addInterface( secondIface );

		ObjectModel model = new ObjectModel();
		model.register( s );
		model.register( p );
		model.register( firstIface );
		model.register( app1 );
		model.register( secondIface );
		model.register( app2 );

		return model;

	}


	/**
	 *      ROUTER
	 *     /      \
	 *  IFACE0   IFACE1
	 *    |        |
	 *  SWITCH0  SWITCH1
	 *
	 * @param projectId
	 * @return
	 */
	public static ObjectModel getSimplestRouterModel( String projectId ) {

		Switch s0 = new Switch( "SWITCH0", projectId );
		Switch s1 = new Switch( "SWITCH1", projectId );

		Interface routerFirstIface = new Interface( "IFACE0", projectId, s0, new IpAddress( "192.168.18.1", 24 ) );
		Interface routerSecondIface = new Interface( "IFACE1", projectId, s1, new IpAddress( "192.168.19.1", 24 ) );

		Appliance router = new Appliance( "ROUTER", projectId, ApplianceType.ROUTER, "dummy" );
		router.addInterface( routerFirstIface );
		router.addInterface( routerSecondIface );

		ObjectModel model = new ObjectModel();

		model.registerAll( s0, s1, routerFirstIface, routerSecondIface, router );

		return model;

	}


	/**
	 *  FIRST         ROUTER         SECOND
	 *    |          /      \          |
	 *  IFACE0    IFACE0   IFACE1    IFACE1
	 *     \       /          \       /
	 *      SWITCH0            SWITCH1
	 */
	public static ObjectModel getSimpleRouterModel( String projectId ) {

		ObjectModel model = getSimplestRouterModel( projectId );

		Switch s0 = model.getSwitches().get( 0 );
		Switch s1 = model.getSwitches().get( 1 );

		Interface firstIface = new Interface( "IFACE0", projectId, s0, new IpAddress( "192.168.18.2", 24 ) );
		Interface secondIface = new Interface( "IFACE0", projectId, s1, new IpAddress( "192.168.19.2", 24 ) );

		Appliance first = new Appliance( "FIRST", projectId, ApplianceType.MACHINE, "dummy" );
		first.addInterface( firstIface );

		Appliance second = new Appliance( "SECOND", projectId, ApplianceType.MACHINE, "dummy" );
		second.addInterface( secondIface );

		model.registerAll( firstIface, secondIface, first, second );

		return model;

	}


	public static Appliance anyRouter( ObjectModel model ) {

		for ( Appliance app : model.getAppliances() ) {
			if ( ApplianceType.ROUTER.equals( app.getType() ) ) {
				return app;
			}
		}

		return null;

	}

}
