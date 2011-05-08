package org.jims.modules.crossbow.infrastructure.supervisor;

import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.VlanApplianceAnnotation;
import org.jims.modules.crossbow.objectmodel.VlanInterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class RouterJoinTest {

	@Before
	public void setUp() {
		supervisor = new Supervisor( null, null );
	}


	@Test
	public void testSimpleRouterJoin() {

		Appliance r0 = new Appliance( "R0", "PRO", ApplianceType.ROUTER, "router" ),
		          r1 = new Appliance( "R1", "PRO", ApplianceType.ROUTER, "router" );

		Interface i0 = new Interface( "I0", "PRO" ),
		          v0 = new Interface( "V0", "PRO" ),
		          i1 = new Interface( "I1", "PRO" ),
		          v1 = new Interface( "V1", "PRO" );

		r0.addInterface( i0 );
		r0.addInterface( v0 );
		r1.addInterface( i1 );
		r1.addInterface( v1 );

		Assignments assignments = new Assignments();
		assignments.putAnnotation( v0, new VlanInterfaceAssignment( TAG ) );
		assignments.putAnnotation( v1, new VlanInterfaceAssignment( TAG ) );

		ObjectModel model = new ObjectModel();

		model.registerAll( r0, r1, i0, i1 );

		assertEquals( 2, model.getAppliances( ApplianceType.ROUTER ).size() );

		supervisor.joinRouters( model, assignments );

		assertEquals( 1, model.getAppliances( ApplianceType.ROUTER ).size() );  // redundant routers removed
		assertEquals( 2, model.getInterfaces().size() );                        // no VLAN interfaces

		Appliance router = model.getAppliances( ApplianceType.ROUTER ).get( 0 );

		assertEquals( TAG, ( ( VlanApplianceAnnotation ) assignments.getAnnotation( router ) ).getTag() );
		assertEquals( 2, router.getInterfaces().size() );

		assertNull( assignments.getAnnotation( v0 ) );

	}


	private Supervisor supervisor;
	public static final int TAG = 13;

}
