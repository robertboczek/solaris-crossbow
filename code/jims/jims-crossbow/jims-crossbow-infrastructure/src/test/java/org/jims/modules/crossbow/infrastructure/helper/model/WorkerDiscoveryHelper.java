package org.jims.modules.crossbow.infrastructure.helper.model;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.flow.FlowMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagementMBean;

import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class WorkerDiscoveryHelper {

	public static void stubQosModelMocks( EtherstubManagerMBean etherstubManager,
	                                      VNicManagerMBean vnicManager,
	                                      FlowManagerMBean flowManager,
																				GlobalZoneManagementMBean zoneManager )
		throws Exception {


		// One appliance.

		String appName = "PROJ..MMINE";
		when( zoneManager.getZones() ).thenReturn( Arrays.asList( appName ) );

		// One switch.

		String switchName = "PROJ..SMINE0";
		when( etherstubManager.getEtherstubsNames() ).thenReturn( Arrays.asList( switchName ) );

		// One interface.

		VNicMBean vnic = mock( VNicMBean.class );
		when( vnic.getParent() ).thenReturn( switchName );
		when( vnic.getIpAddress() ).thenReturn( "1.1.1.1" );
		when( vnic.getIpMask() ).thenReturn( "24" );

		String ifaceName = "PROJ..MMINE..IFACE0";
		when( vnicManager.getVNicsNames() ).thenReturn( Arrays.asList( ifaceName ) );
		when( vnicManager.getByName( ifaceName ) ).thenReturn( vnic );

		// One policy.

		Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();
		attrs.put( FlowAttribute.TRANSPORT, "icmpv6" );

		Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();
		props.put( FlowProperty.MAXBW, "13" );

		FlowMBean flow = mock( FlowMBean.class );
		when( flow.getProperties() ).thenReturn( props );
		when( flow.getAttributes() ).thenReturn( attrs );

		String policyName = "PROJ..MMINE..IFACE0..MYPOLICY";
		when( flowManager.getFlows() ).thenReturn( Arrays.asList( policyName ) );
		when( flowManager.getByName( policyName ) ).thenReturn( flow );

	}

}
