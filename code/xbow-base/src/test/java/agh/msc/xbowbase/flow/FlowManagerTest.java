package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.lib.Flowadm;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class FlowManagerTest {

	public FlowManagerTest() {
	}


	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}


	@Before
	public void setUp() {

		helper = mock( Flowadm.class );

		flowManager = new FlowManager();
		flowManager.setFlowadm( helper );

	}

	@After
	public void tearDown() {
	}


	@Test
	public void testGetFlows() {

		// Initialize objects.

		Set< String > flows = new HashSet< String >( Arrays.asList( "zyzio", "dyzio" ) );
		List< FlowInfo > flowInfos = new LinkedList< FlowInfo >();

		for ( String flow : flows ) {
			flowInfos.add( new FlowInfo( flow, null, null, null, true ) );
		}

		// Stub.

		when( helper.getFlowsInfo() )
			.thenReturn( flowInfos );

		List< String > flowNames = flowManager.getFlows();

		assertEquals( flows, new HashSet< String >( flowNames ) );

	}


	private FlowManager flowManager;

	private Flowadm helper;

}