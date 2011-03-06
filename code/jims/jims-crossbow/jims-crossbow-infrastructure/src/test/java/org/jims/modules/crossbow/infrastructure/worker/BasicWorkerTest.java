package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.etherstub.EtherstubMBean;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.infrastructure.worker.exception.ActionException;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class BasicWorkerTest {

	@Before
	public void setUp() {

		actions = new Actions();
		model = ModelHelper.getSimpleModel( "MY-PROJECT", ".." );

		etherstubManager = mock( EtherstubManagerMBean.class );
		vNicManager = mock( VNicManagerMBean.class );
		flowManager = mock( FlowManagerMBean.class );

		worker = new Worker( vNicManager, etherstubManager, null, null );

	}


	@Test
	public void testExceptionPropagationOnError() throws Exception {

		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.REM );
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getMachines().get( 0 ), Actions.ACTION.ADD );

		ActionException actionException = new ActionException( "" );
		EtherstubException etherstubException = new EtherstubException( "" );

		doThrow( etherstubException ).when( etherstubManager ).create( ( EtherstubMBean ) anyObject() );

		ModelInstantiationException ex = null;

		try {
			worker.instantiate( model, actions, new Assignments() );
		} catch ( ModelInstantiationException e ) {
			ex= e;
		}

		assert ( etherstubException == ( ( ActionException ) ex.getCause() ).getCause() );

	}


	@Test
	public void testPolicyAppliedToVnicIfAnyFilter() throws Exception {

		model = ModelHelper.getSimpleQoSAnyFilter( "SOME-PROJECT", ".." );

		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getPolicies().get( 0 ), Actions.ACTION.ADD );

		PriorityPolicy policy = ( PriorityPolicy ) model.getPolicies().get( 0 );

		VNicMBean vnic = mock( VNicMBean.class );

		when( vNicManager.getByName( anyString() ) ).thenReturn( vnic );

		worker.instantiate( model, actions, new Assignments() );

		verify( vnic ).setProperty( eq( LinkProperties.PRIORITY ), eq( policy.getPriorityAsString() ) );
		verifyNoMoreInteractions( flowManager );

	}


	private WorkerMBean worker;
	private Actions actions;
	private ObjectModel model;

	private EtherstubManagerMBean etherstubManager;
	private VNicManagerMBean vNicManager;
	private FlowManagerMBean flowManager;

}