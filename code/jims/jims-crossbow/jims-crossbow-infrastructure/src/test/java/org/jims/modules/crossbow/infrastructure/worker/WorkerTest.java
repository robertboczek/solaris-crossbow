package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.crossbow.etherstub.EtherstubMBean;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.solaris.commands.CreateZoneFromSnapshotCommand;
import org.jims.modules.solaris.commands.ModifyZoneCommand;
import org.jims.modules.solaris.commands.SolarisCommandFactory;
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
		commandFactory = mock( SolarisCommandFactory.class );

		worker = new Worker( vNicManager, etherstubManager, flowManager, zoneCopier, commandFactory );

	}


	@Test
	public void testSimpleModelInstantiation() throws Exception {

		CreateZoneFromSnapshotCommand cmd = mock( CreateZoneFromSnapshotCommand.class );
		ModifyZoneCommand modifyCmd = mock( ModifyZoneCommand.class );

		when( commandFactory.getCreateZoneFromSnapshotCommand() ).thenReturn( cmd );
		when( commandFactory.getModifyZoneCommand() ).thenReturn( modifyCmd );

		/*
		 * M -- S
		 */

		String machineId = "MYSQL", portId = "LINK0", switchId = "SWITCH";
		String etherstubId = projectId + SEP + switchId;

		Appliance m = new Appliance( machineId, projectId, ApplianceType.MACHINE );

		Interface p = new Interface( portId, projectId );
		p.setIpAddress( new IpAddress( "1.2.3.4", 23 ) );

		Switch s = new Switch( switchId, projectId );

		m.addInterface( p );
		p.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.register( p );
		model.register( s );
		model.register( m );

		Actions actions = new Actions();
		actions.insert( m, Actions.ACTION.ADD );
		actions.insert( p, Actions.ACTION.ADD );
		actions.insert( s, Actions.ACTION.ADD );

		worker.instantiate( model, actions, new Assignments() );

		// Verify

		ArgumentCaptor< EtherstubMBean > etherstub = ArgumentCaptor.forClass( EtherstubMBean.class );
		ArgumentCaptor< VNicMBean > vnic = ArgumentCaptor.forClass( VNicMBean.class );

		verify( etherstubManager ).create( etherstub.capture() );
		verify( vNicManager ).create( vnic.capture() );
		verify( cmd ).createZone( ( ZoneInfo ) anyObject(), anyString() );

		// Interface attachment.
		verify( modifyCmd ).attachInterfaces( eq( NameHelper.machineName( m ) ), anyList() );

		// Boot.
		verify( modifyCmd ).bootZone( NameHelper.machineName( m ) );

		// IP setup.
		verify( modifyCmd ).configureInterfaces( eq( NameHelper.machineName( m ) ), anyList(), anyList() );

		assertEquals( etherstubId, etherstub.getValue().getName() );
		assertEquals( etherstubId, vnic.getValue().getParent() );
		assertEquals( projectId + SEP + machineId + SEP + portId, vnic.getValue().getName() );

	}


	private VNicManagerMBean vNicManager;
	private EtherstubManagerMBean etherstubManager;
	private FlowManagerMBean flowManager;
	private SolarisCommandFactory commandFactory;

	private String projectId = "MYPROJECT";
	private Worker worker;

	private final String SEP = NameHelper.SEP;

}
