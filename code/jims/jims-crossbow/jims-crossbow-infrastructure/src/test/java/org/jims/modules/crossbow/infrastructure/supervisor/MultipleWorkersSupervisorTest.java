package org.jims.modules.crossbow.infrastructure.supervisor;

import org.junit.Ignore;
import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.HashMap;
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import java.util.LinkedList;
import org.jims.modules.crossbow.infrastructure.worker.*;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.infrastructure.supervisor.vlan.VlanTagProvider;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.VlanApplianceAnnotation;
import org.jims.modules.crossbow.objectmodel.VlanInterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class MultipleWorkersSupervisorTest {

	@Before
	public void setUp() {

		actions = new Actions();
		model = ModelHelper.getSimpleModel( "MY-PROJECT", ".." );

		firstWorker = mock( WorkerMBean.class );
		secondWorker = mock( WorkerMBean.class );
		assigner = mock( AssignerMBean.class );
		provider = mock( WorkerProvider.class );
		tagProvider = mock( VlanTagProvider.class );

		supervisor = new Supervisor( provider, assigner );

		supervisor.addWorker( W0, firstWorker );
		supervisor.addWorker( W1, secondWorker );
		supervisor.setAssigner( assigner );
		supervisor.setTagProvider( tagProvider );

	}


	@Test
	public void testEveryWorkerGettingItsPart() throws Exception {

		when( tagProvider.provide() ).thenReturn( TAG );

		model = ModelHelper.getSimpleRouterModel( "MY-PROJECT" );

		actions = new Actions();

		for ( Object o : new LinkedList< Object >(){{ addAll( model.getAppliances() );
		                                              addAll( model.getInterfaces() );
		                                              addAll( model.getSwitches() ); }} ) {
			actions.put( o, Actions.Action.ADD );
		}

		Assignments assignments = new Assignments();

		assignments.put( model.getAppliances( ApplianceType.MACHINE ).get( 0 ), W0 );
		assignments.put( model.getAppliances( ApplianceType.MACHINE ).get( 1 ), W1 );

		assignments.put( model.getAppliances( ApplianceType.ROUTER ).get( 0 ), W1 );  // It doesn't matter.

		assignments.put( model.getSwitches().get( 0 ), W0 );
		assignments.put( model.getSwitches().get( 1 ), W1 );

		// Assignments for interfaces are always determined by the underlying switch.

		for ( Switch s : model.getSwitches() ) {
			for ( Endpoint ep : s.getEndpoints() ) {
				assignments.put( ep, assignments.get( s ) );
			}
		}

		supervisor.instantiate( model, actions, assignments );

		assert ( 2 == model.getAppliances( ApplianceType.ROUTER ).size() );

		String firstRouterAssign = assignments.get( model.getAppliances( ApplianceType.ROUTER ).get( 0 ) );
		String secondRouterAssign = assignments.get( model.getAppliances( ApplianceType.ROUTER ).get( 1 ) );

		assert ( ( ! firstRouterAssign.equals( secondRouterAssign ) ) && ( null != secondRouterAssign ) );

		ArgumentCaptor< Actions > firstActionsCaptor = ArgumentCaptor.forClass( Actions.class );
		verify( firstWorker ).instantiate( eq( model ), firstActionsCaptor.capture(), eq( assignments ) );

		ArgumentCaptor< Actions > secondActionsCaptor = ArgumentCaptor.forClass( Actions.class );
		verify( secondWorker ).instantiate( eq( model ), secondActionsCaptor.capture(), eq( assignments ) );

		for ( Object o : assignments.filterByTarget( W0 ) ) {
			assert ( null != firstActionsCaptor.getValue().get( o ) );
			assert ( null == secondActionsCaptor.getValue().get( o ) );
		}

		for ( Object o : assignments.filterByTarget( W1 ) ) {
			assert ( null == firstActionsCaptor.getValue().get( o ) );
			assert ( null != secondActionsCaptor.getValue().get( o ) );
		}

	}


	@Test
	public void testAnnotationsApplied() {

		Map< Appliance, Collection< Interface > > parts = new HashMap< Appliance, Collection< Interface > >();

		Appliance router = new Appliance( "aaa", "bbb", ApplianceType.ROUTER );

		Interface i0 = new Interface( "my-int", "my-pro" );
		Interface i1 = new Interface( "my-int2", "my-pro" );

		parts.put( router, Arrays.asList( i0, i1 ) );

		Assignments assignments = new Assignments();

		assignments.putAnnotation( router, new VlanApplianceAnnotation( TAG ) );

		assert ( null == assignments.getAnnotation( i0 ) );
		assert ( null == assignments.getAnnotation( i1 ) );

		supervisor.createVlanAssignments( parts, null, assignments );

		assert ( TAG == ( ( VlanInterfaceAssignment ) assignments.getAnnotation( i0 ) ).getTag() );
		assert ( TAG == ( ( VlanInterfaceAssignment ) assignments.getAnnotation( i1 ) ).getTag() );

	}


	private static final String W0 = "w0", W1 = "w1";
	private static final int TAG = 13;

	private WorkerMBean firstWorker, secondWorker;
	private Actions actions;
	private ObjectModel model;
	private Supervisor supervisor;
	private WorkerProvider provider;
	private AssignerMBean assigner;
	private VlanTagProvider tagProvider;

}
