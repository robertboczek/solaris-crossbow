package org.jims.modules.crossbow.infrastructure.supervisor;

import org.jims.modules.crossbow.infrastructure.worker.*;
import org.jims.modules.crossbow.infrastructure.assigner.AssignerMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.ModelHelper;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
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
		notification = mock(CrossbowNotificationMBean.class);

		supervisor = new Supervisor();

		supervisor.addWorker( "some-worker", worker );
		supervisor.setAssigner( assigner );
		supervisor.setCrossbowNotificationMBean( notification );

	}


	@Test
	public void testOperationsDelegation() throws Exception {

		actions.insert( model.getPorts().get( 0 ), Actions.ACTION.REM );
		actions.insert( model.getSwitches().get( 0 ), Actions.ACTION.ADD );
		actions.insert( model.getAppliances().get( 0 ), Actions.ACTION.ADD );

		Assignments assignments = new Assignments();

		when( assigner.assign( model ) ).thenReturn( assignments );

		supervisor.instantiate( model, actions );

		verify( worker ).instantiate( same( model ), same( actions ), same( assignments ) );
		verify( notification ).reset();

	}


	private WorkerMBean worker;
	private CrossbowNotificationMBean notification;
	private Actions actions;
	private ObjectModel model;
	private Supervisor supervisor;
	private AssignerMBean assigner;

}
