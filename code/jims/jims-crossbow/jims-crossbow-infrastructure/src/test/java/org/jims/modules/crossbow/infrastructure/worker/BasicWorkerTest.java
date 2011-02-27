package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.etherstub.EtherstubMBean;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.infrastructure.worker.exception.ActionException;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
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

		worker = new Worker( vNicManager, etherstubManager, null, null );

	}


	@Test( expected = ModelInstantiationException.class )
	public void testExceptionPropagationOnError() throws Exception {

		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.REM );
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getMachines().get( 0 ), Actions.ACTION.ADD );

		ActionException actionException = new ActionException( "" );

		doThrow( new EtherstubException( "" ) ).when( etherstubManager ).create( ( EtherstubMBean ) anyObject() );

		worker.instantiate( model, actions, new Assignments() );

	}


	private WorkerMBean worker;
	private Actions actions;
	private ObjectModel model;

	private EtherstubManagerMBean etherstubManager;
	private VNicManagerMBean vNicManager;

}