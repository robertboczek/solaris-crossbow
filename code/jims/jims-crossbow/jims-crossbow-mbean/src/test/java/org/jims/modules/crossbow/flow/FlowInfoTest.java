package org.jims.modules.crossbow.flow;

import org.jims.modules.crossbow.flow.FlowInfo;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class FlowInfoTest {

	public FlowInfoTest() {
	}


	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}


	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}


	@Test
	public void testFlowInfoGetters() {

		// Initialize.

		String name = "ala";
		String link = "e1000g2";

		Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();
		attrs.put( FlowAttribute.TRANSPORT, "udp" );

		Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();
		props.put( FlowProperty.PRIORITY, "low" );

		boolean temporary = true;

		FlowInfo flowInfo = new FlowInfo( name, link, attrs, props, temporary );

		// Check.

		assertEquals( name, flowInfo.getName() );
		assertEquals( link, flowInfo.getLink() );
		assertEquals( attrs, flowInfo.getAttributes() );
		assertEquals( props, flowInfo.getProperties() );
		assertEquals( temporary, flowInfo.isTemporary() );

	}

}
