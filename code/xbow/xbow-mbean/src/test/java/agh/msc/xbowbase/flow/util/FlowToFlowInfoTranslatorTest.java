package agh.msc.xbowbase.flow.util;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.flow.enums.FlowAttribute;
import agh.msc.xbowbase.flow.enums.FlowProperty;
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
public class FlowToFlowInfoTranslatorTest {

	public FlowToFlowInfoTranslatorTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {

		attrs = new HashMap< FlowAttribute, String >();
		attrs.put( FlowAttribute.TRANSPORT, "tcp" );

		props = new HashMap< FlowProperty, String >();
		props.put( FlowProperty.PRIORITY, "high" );

	}

	@After
	public void tearDown() {
	}


	@Test
	public void testFlowToFlowInfoTranslation() {

		// Create and initialize a flow.

		Flow flow = new Flow();

		flow.setName( "telnet" );
		flow.setLink( "e1000g0" );
		flow.setTemporary( true );
		flow.setAttrs( attrs );
		flow.setProps( props );

		// Translate.

		FlowInfo flowInfo = FlowToFlowInfoTranslator.toFlowInfo( flow );

		// Check.

		assertEquals( flow.getName(), flowInfo.getName() );
		assertEquals( flow.getLink(), flowInfo.getLink() );
		assertEquals( flow.isTemporary(), flowInfo.isTemporary() );
		assertEquals( flow.getAttrs(), flowInfo.getAttributes() );
		assertEquals( flow.getProps(), flowInfo.getProperties() );

	}


	@Test
	public void testFlowInfoToFlowTranslation() {

		// Create and initialize FlowInfo instance.

		FlowInfo flowInfo = new FlowInfo( "video", "e1000g0", attrs, props, false );

		// Translate.

		Flow flow = FlowToFlowInfoTranslator.toFlow( flowInfo );

		// Check.

		assertEquals( flowInfo.getName(), flow.getName() );
		assertEquals( flowInfo.getLink(), flow.getLink() );
		assertEquals( flowInfo.isTemporary(), flow.isTemporary() );
		assertEquals( flowInfo.getAttributes(), flow.getAttrs() );
		assertEquals( flowInfo.getProperties(), flow.getProps() );

	}


	Map< FlowAttribute, String > attrs;
	Map< FlowProperty, String > props;

}