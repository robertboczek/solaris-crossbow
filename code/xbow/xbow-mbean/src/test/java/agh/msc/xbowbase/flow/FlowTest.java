package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.enums.FlowAttribute;
import agh.msc.xbowbase.flow.enums.FlowProperty;
import agh.msc.xbowbase.lib.FlowHelper;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class FlowTest {

	public FlowTest() {
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

		flow = new Flow();
		flow.setFlowadm( helper );

	}

	@After
	public void tearDown() {
	}


	@Test
	public void testSettersAndGetters() {

		String name = "koszalek";
		String link = "e1000g1";

		Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();
		attrs.put( FlowAttribute.TRANSPORT, "udp" );

		Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();
		props.put( FlowProperty.MAXBW, "10M" );

		boolean temporary = false;

		flow.setName( name );
		flow.setLink( link );
		flow.setAttrs( attrs );
		flow.setProps( props );
		flow.setTemporary( temporary );

		assertEquals( name, flow.getName() );
		assertEquals( link, flow.getLink() );
		assertEquals( attrs, flow.getAttrs() );
		assertEquals( props, flow.getProps() );
		assertEquals( temporary, flow.isTemporary() );

	}


	@Test
	public void testSettingPropertiesWithHelper() throws XbowException {

		Map< FlowProperty, String > properties = new HashMap< FlowProperty, String >();

		flow.setName( "flow13" );
		flow.setProperties( properties, true );

		verify( helper ).setProperties( flow.getName(), properties, true );
		verify( helper ).getProperties( flow.getName() );

	}


	@Test
	public void testResettingPropertiesWithHelper() throws XbowException {

		List< FlowProperty > properties = new LinkedList< FlowProperty >();

		flow.setName( "name" );
		flow.resetProperties( properties, true );

		verify( helper ).resetProperties( flow.getName(), properties, true );
		verify( helper ).getProperties( flow.getName() );

	}


	@Test
	public void testGettingPropertiesWithHelper() throws XbowException {

		Map< FlowProperty, String > properties = new HashMap< FlowProperty, String >();

		when( helper.getProperties( anyString() ) )
			.thenReturn( properties );

		assertEquals( properties, flow.getProperties() );

	}


	@Test( expected = NoSuchFlowException.class )
	public void testGetNonInstantiatedFlowsAttributes() throws NoSuchFlowException {

		when( helper.getAttributes( flow.getName() ) )
			.thenThrow( new NoSuchFlowException( flow.getName() ) );

		flow.getAttributes();

	}


	@Test( expected = NoSuchFlowException.class )
	public void testGetNonInstantiatedFlowsProperties() throws NoSuchFlowException {

		when( helper.getProperties( flow.getName() ) )
			.thenThrow( new NoSuchFlowException( flow.getName() ) );

		flow.getProperties();

	}


	@Test( expected = NoSuchFlowException.class )
	public void testSetNonInstantiatedFlowsProperties() throws XbowException {

		Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();

		doThrow( new NoSuchFlowException( flow.getName() ) )
			.when( helper ).setProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.setProperties( props, true );

	}


	@Test( expected = ValidationException.class )
	public void testSetInvalidFlowProperties() throws XbowException {

		Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();
		props.put( FlowProperty.PRIORITY, "medum" );

		doThrow( new ValidationException( "" ) )
			.when( helper ).setProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.setProperties( props, true );

	}


	@Test( expected = NoSuchFlowException.class )
	public void testResetNonInstantiatedFlowsProperties() throws XbowException {

		List< FlowProperty > props = new LinkedList< FlowProperty >();

		doThrow( new NoSuchFlowException( flow.getName() ) )
			.when( helper ).resetProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.resetProperties( props, true );

	}


	@Test( expected = ValidationException.class )
	public void testResetInvalidFlowProperties() throws XbowException {

		List< FlowProperty > props = new LinkedList< FlowProperty >();
		props.add( FlowProperty.MAXBW );

		doThrow( new ValidationException( "prioority" ) )
			.when( helper ).resetProperties( eq( flow.getName() ), anyList(), anyBoolean() );

		flow.resetProperties( props, true );

	}


	@Test
	public void testEquals() {

		flow.setName( "name" );

		Flow aFlow = new Flow();
		aFlow.setName( flow.getName() + "0" );

		Flow oneMoreFlow = new Flow();
		oneMoreFlow.setName( flow.getName() );

		String someObject = "someObject";

		assertTrue( flow.equals( flow ) );
		assertTrue( flow.equals( oneMoreFlow ) );
		assertFalse( flow.equals( aFlow ) );
		assertFalse( flow.equals( someObject ) );

	}


	private Flow flow;

	private FlowHelper helper;

}