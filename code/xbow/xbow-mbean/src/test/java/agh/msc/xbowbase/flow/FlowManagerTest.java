package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.util.FlowToFlowInfoTranslator;
import agh.msc.xbowbase.lib.FlowHelper;
import agh.msc.xbowbase.publisher.Publisher;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
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

		helper = mock( FlowHelper.class );
		publisher = mock( Publisher.class );

		flowManager = new FlowManager();
		flowManager.setFlowadm( helper );
		flowManager.setPublisher( publisher );

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


	@Test
	public void testUnpublishStaleFlows() throws NotPublishedException {

		List< FlowInfo > flowInfos = new LinkedList< FlowInfo >( Arrays.asList(
			new FlowInfo( "jas", "e1000g0", null, null, true ),
			new FlowInfo( "malgosia", "e1000g0", null, null, true )
		) );

		List< FlowInfo > newFlowInfos = new LinkedList< FlowInfo >( flowInfos.subList( 0, 1 ) );

		List< Flow > flows = new LinkedList< Flow >();

		for ( FlowInfo flowInfo : flowInfos ) {
			flows.add( FlowToFlowInfoTranslator.toFlow( flowInfo ) );
		}

		// Stub.

		when( helper.getFlowsInfo() )
			.thenReturn( flowInfos )
			.thenReturn( newFlowInfos );

		when( publisher.getPublished() )
			.thenReturn( new LinkedList< Object >( flows ) );

		// 1. FlowManager discovers 2 flows.

		flowManager.discover();

		// 2. Now, user removes one flow manually.
		// 3. FlowManager performs one more discovery and unpublishes deleted flow.

		flowManager.discover();

		verify( publisher, times( 2 ) ).publish( flows.get( 0 ) );
		verify( publisher, times( 1 ) ).publish( flows.get( 1 ) );

		verify( publisher ).unpublish( flowInfos.get( 1 ).getName() );

	}


	private FlowManager flowManager;
	private FlowHelper helper;
	private Publisher publisher;

}