package org.jims.modules.crossbow.infrastructure.gatherer;

import java.util.Map;
import java.util.HashMap;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.infrastructure.worker.NameHelper;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.sg.service.wnservice.WNDelegateMBean;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class BasicStatisticsGathererTest {

	@Before
	public void setUp() {

		vNicManager = mock( VNicManagerMBean.class );
		vnic = mock( VNicMBean.class );
		wNDelegateMBean = mock( WNDelegateMBean.class );

		statisticsGatherer = new StatisticsGatherer();

	}

	@Test
	public void test() {

	}


	/*@Test
	public void testInterfaceStatisticsTranslation() throws Exception {

		Interface iface = new Interface( "some-resource", "some-project" );

		Appliance appliance = new Appliance( "some-appliance", "some-project", ApplianceType.MACHINE );
		appliance.addInterface( iface );

		String ifaceName = NameHelper.interfaceName( iface );

		Map< LinkStatistics, String > stats = new HashMap< LinkStatistics, String >();
		stats.put( LinkStatistics.RBYTES, "11" );
		stats.put( LinkStatistics.IERRORS, "12" );
		stats.put( LinkStatistics.IPACKETS, "13" );

		when( vNicManager.getByName( ifaceName ) ).thenReturn( vnic );
		when( vnic.getStatistics() ).thenReturn( stats );

		Map< LinkStatistics, Long > ifaceStats = statisticsGatherer.getInterfaceStatistics( iface );

		assert ( 11 == ifaceStats.get( LinkStatistics.RBYTES ) );
		assert ( 12 == ifaceStats.get( LinkStatistics.IERRORS ) );
		assert ( 13 == ifaceStats.get( LinkStatistics.IPACKETS ) );

	}*/


	private StatisticsGatherer statisticsGatherer;

	private WNDelegateMBean wNDelegateMBean;
	private VNicManagerMBean vNicManager;
	private VNicMBean vnic;

}
