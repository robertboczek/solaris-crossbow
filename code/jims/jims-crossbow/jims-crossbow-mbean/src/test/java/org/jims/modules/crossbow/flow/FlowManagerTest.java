package org.jims.modules.crossbow.flow;

import org.jims.modules.crossbow.flow.Flow;
import org.jims.modules.crossbow.flow.FlowInfo;
import org.jims.modules.crossbow.flow.FlowManager;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.util.FlowToFlowInfoTranslator;
import org.jims.modules.crossbow.lib.FlowHelper;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class FlowManagerTest {

	@Before
	public void setUp() {

		flow = new Flow();

		helper = mock( FlowHelper.class );
		publisher = mock( Publisher.class );

		flowManager = new FlowManager();
		flowManager.setFlowadm( helper );
		flowManager.setPublisher( publisher );

	}


	@Test
	public void testGetFlows() {

		// Initialize objects.

		Set< String > flows = new HashSet< String >( Arrays.asList( "zyzio", "dyzio" ) );
		List< FlowInfo > flowInfos = new LinkedList< FlowInfo >();

		for ( String f : flows ) {
			flowInfos.add( new FlowInfo( f, null, null, null, true ) );
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


	@Ignore
	public void testCreateAndPublishFlow() throws XbowException {

		flowManager.create( flow );

		verify( publisher ).publish( flow );

	}


	@Test
	public void testOnlyCreateFlow() throws XbowException {

		flowManager.setPublisher( null );
		flowManager.create( flow );

	}


	@Test
	public void testRemoveAndUnpublishFlow() throws XbowException, NotPublishedException {

		flow.setName( "flow" );

		flowManager.remove( flow.getName(), true );

		verify( helper ).remove( flow.getName(), true );
		verify( publisher ).unpublish( flow.getName() );

	}


	@Test
	public void testOnlyRemoveFlow() throws XbowException {

		flow.setName( "ala" );
		flowManager.setPublisher( null );

		flowManager.remove( flow.getName(), true );

		verify( helper ).remove( flow.getName(), true );

	}


	@Test
	public void testFlowManagerDoesntFailWithoutPublisherSet() {

		flowManager.setPublisher( null );

	}


	private Flow flow;

	private FlowManager flowManager;
	private FlowHelper helper;
	private Publisher publisher;

}