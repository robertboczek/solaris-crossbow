package org.jims.modules.crossbow.infrastructure.worker;

import org.junit.Ignore;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.util.struct.Pair;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import java.util.Map;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.infrastructure.helper.model.WorkerDiscoveryHelper;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.TransportFilter;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.vlan.VlanManagerMBean;
import org.jims.modules.solaris.commands.SolarisCommandFactory;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagementMBean;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class WorkerDiscoveryTest {

	@Before
	public void setUp() {

		actions = new Actions();

		etherstubManager = mock( EtherstubManagerMBean.class );
		vNicManager = mock( VNicManagerMBean.class );
		flowManager = mock( FlowManagerMBean.class );
		vlanManager = mock( VlanManagerMBean.class );
		zoneManager = mock( GlobalZoneManagementMBean.class );
		commandFactory = mock( SolarisCommandFactory.class );

		worker = new Worker( vNicManager, etherstubManager, flowManager, vlanManager,
		                     zoneManager, commandFactory );

	}


	@Test
	public void dummy() {}


	// TODO-DAWID reenable
	@Ignore
	public void testPoliciesDiscovery() throws Exception {

		WorkerDiscoveryHelper.stubQosModelMocks( etherstubManager, vNicManager, flowManager, zoneManager );

		Map< String, Pair< ObjectModel, Assignments > > projects = worker.discover();

		String pn = projects.keySet().iterator().next();
		ObjectModel om = projects.get( pn ).first;
		Policy policy = om.getPolicies().get( 0 );

		assertEquals( 1, projects.size() );
		assert ( "PROJ".equals( pn ) );

		assertTrue( policy instanceof BandwidthPolicy );
		assertTrue( policy.getFilter() instanceof TransportFilter );
		assertEquals( TransportFilter.Transport.ICMPV6, ( ( TransportFilter ) policy.getFilter() ).getTransport() );

	}


	private WorkerMBean worker;
	private Actions actions;
	private ObjectModel model;

	private EtherstubManagerMBean etherstubManager;
	private VNicManagerMBean vNicManager;
	private FlowManagerMBean flowManager;
	private VlanManagerMBean vlanManager;
	private GlobalZoneManagementMBean zoneManager;
	private SolarisCommandFactory commandFactory;

}