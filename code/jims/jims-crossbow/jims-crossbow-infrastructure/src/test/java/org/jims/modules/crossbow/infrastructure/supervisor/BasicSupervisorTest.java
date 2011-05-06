package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import org.jims.modules.crossbow.infrastructure.worker.*;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.gds.notification.WorkerNodeAddedNotification;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class BasicSupervisorTest {

	@Before
	public void setUp() {

		actions = new Actions();
		model = ModelHelper.getSimpleModel( "MY-PROJECT", ".." );

		worker = mock( WorkerMBean.class );
		assigner = mock( AssignerMBean.class );
		provider = mock( WorkerProvider.class );

		supervisor = new Supervisor( provider, assigner );

		supervisor.addWorker( "some-worker", worker );
		supervisor.setAssigner( assigner );

	}


	@Test
	public void testOperationsDelegation() throws Exception {

		actions.put( model.getInterfaces().get( 0 ), Actions.Action.REM );
		actions.put( model.getSwitches().get( 0 ), Actions.Action.ADD );
		actions.put( model.getAppliances().get( 0 ), Actions.Action.ADD );

		Assignments assignments = new Assignments();

		when( assigner.assign( model ) ).thenReturn( assignments );

		supervisor.instantiate( model, actions );

		verify( worker ).instantiate( same( model ), same( actions ), same( assignments ) );

	}


	@Test
	public void testWorkersListRefreshAfterNotification() throws Exception {

		Map< String, WorkerMBean > workers = new HashMap< String, WorkerMBean >();

		when( provider.getWorkers() ).thenReturn( workers );

		supervisor.handleNotification( new WorkerNodeAddedNotification( null, null, 0 ), null );

		verify( provider ).getWorkers();

		assert Arrays.equals( workers.keySet().toArray(), supervisor.getWorkers().toArray() );

	}


	private WorkerMBean worker;
	private Actions actions;
	private ObjectModel model;
	private Supervisor supervisor;
	private WorkerProvider provider;
	private AssignerMBean assigner;

}
