package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.etherstub.EtherstubMBean;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Machine;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.junit.Assert.*;

import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class WorkerTest {

	@Before
	public void setUp() {

		vNicManager = mock( VNicManagerMBean.class );
		etherstubManager = mock( EtherstubManagerMBean.class );
		flowManager = mock( FlowManagerMBean.class );
		ZoneCopierMBean zoneCopier = mock( ZoneCopierMBean.class );

		worker = new Worker( vNicManager, etherstubManager, flowManager, zoneCopier );

	}


	@Test
	public void testSimpleModelInstantiation() throws Exception {

		/*
		 * M -- S
		 */

		String machineId = "MYSQL", portId = machineId + SEP + "LINK0", switchId = "SWITCH";
		String etherstubId = projectId + SEP + switchId;

		Machine m = new Machine( machineId, projectId );
		Port p = new Port( portId, projectId );
		Switch s = new Switch( switchId, projectId );

		m.addPort( p );
		p.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.addPorts( p );
		model.addSwitches( s );
		model.addMachines( m );

		Assignments assignments = new Assignments();

		assignments.setAssignment( p, etherstubId );

		worker.instantiate( model, new Actions(), assignments );

		// Verify

		ArgumentCaptor< EtherstubMBean > etherstub = ArgumentCaptor.forClass( EtherstubMBean.class );
		ArgumentCaptor< VNicMBean > vnic = ArgumentCaptor.forClass( VNicMBean.class );

		verify( etherstubManager ).create( etherstub.capture() );
		verify( vNicManager ).create( vnic.capture() );

		assertEquals( etherstubId, etherstub.getValue().getName() );
		assertEquals( etherstubId, vnic.getValue().getParent() );
		assertEquals( projectId + SEP + portId, vnic.getValue().getName() );

	}


	private VNicManagerMBean vNicManager;
	private EtherstubManagerMBean etherstubManager;
	private FlowManagerMBean flowManager;

	private String projectId = "MYPROJECT";
	private Worker worker;

	private final String SEP = Worker.SEP;

}