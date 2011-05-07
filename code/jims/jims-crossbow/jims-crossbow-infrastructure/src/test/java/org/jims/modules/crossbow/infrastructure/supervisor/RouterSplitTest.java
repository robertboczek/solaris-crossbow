package org.jims.modules.crossbow.infrastructure.supervisor;

import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class RouterSplitTest {

	@Before
	public void setUp() {
		supervisor = new Supervisor( null, null );
	}


	@Test
	public void testNoSplitWhenSingleSubnet() {

		ObjectModel model = new ObjectModel();

		Appliance router = new Appliance( "some-id", "some-id", ApplianceType.ROUTER );
		router.addInterface( new Interface(
			"some-id", "some-id",
			new Switch( "some-id", "some-id" ),
			new IpAddress( "1.2.3.4", 24 )
		) );

		model.register( router );

		Actions actions = new Actions();
		actions.put( router, Actions.Action.ADD );

		Assignments assignments = new Assignments();
		assignments.put( router, "w0" );
		assignments.put( ( Switch ) router.getInterface( 0 ).getEndpoint(), "w0" );

		supervisor.splitRouters( model, actions, assignments );

		assert ( 1 == model.getAppliances().size() );
		assert ( router == model.getAppliances().get( 0 ) );

		assert ( Actions.Action.ADD.equals( actions.get( router ) ) );

		assert ( "w0".equals( assignments.get( router ) ) );
		assert ( "w0".equals( assignments.get( ( Switch ) router.getInterface( 0 ).getEndpoint() ) ) );

	}


	@Test
	public void testSplitSimpleRouterModel() {

		ObjectModel model = ModelHelper.getSimpleRouterModel( "project-id" );

		Appliance router = ModelHelper.anyRouter( model );

		Actions actions = new Actions();
		actions.put( router, Actions.Action.ADD );

		Assignments assignments = new Assignments();
		assignments.put( router, "w0" );
		assignments.put( ( Switch ) router.getInterface( 0 ).getEndpoint(), "w0" );
		assignments.put( ( Switch ) router.getInterface( 1 ).getEndpoint(), "w1" );

		supervisor.splitRouters( model, actions, assignments );

		assertEquals( 2, model.getRouters().size() );

		assert ( Actions.Action.ADD.equals( actions.get( model.getRouters().get( 0 ) ) ) );
		assert ( Actions.Action.ADD.equals( actions.get( model.getRouters().get( 1 ) ) ) );

		assert ( null == assignments.get( router ) );

		assert ( ! assignments.get( model.getRouters().get( 0 ) ).equals(
			assignments.get( model.getRouters().get( 1 ) )
		) );

	}


	private Supervisor supervisor;

}