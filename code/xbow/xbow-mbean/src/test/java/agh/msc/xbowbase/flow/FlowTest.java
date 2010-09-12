package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
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

		Map< String, String > attrs = new HashMap< String, String >();
		attrs.put( "transport", "udp" );

		Map< String, String > props = new HashMap< String, String >();
		props.put( "maxbw", "10M" );

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

		Map< String, String > properties = new HashMap< String, String >();

		flow.setName( "flow13" );
		flow.setProperties( properties, true );

		verify( helper ).setProperties( flow.getName(), properties, true );
		verify( helper ).getProperties( flow.getName() );

	}


	@Test
	public void testResettingPropertiesWithHelper() throws XbowException {

		List< String > properties = new LinkedList< String >();

		flow.setName( "name" );
		flow.resetProperties( properties, true );

		verify( helper ).resetProperties( flow.getName(), properties, true );
		verify( helper ).getProperties( flow.getName() );

	}


	@Test
	public void testGettingPropertiesWithHelper() throws XbowException {

		Map< String, String > properties = new HashMap< String, String >();

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

		Map< String, String > props = new HashMap< String, String >();

		doThrow( new NoSuchFlowException( flow.getName() ) )
			.when( helper ).setProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.setProperties( props, true );

	}


	@Test( expected = ValidationException.class )
	public void testSetInvalidFlowProperties() throws XbowException {

		Map< String, String > props = new HashMap< String, String >();
		props.put( "prioority", "medum" );

		doThrow( new ValidationException( props.keySet().toArray( new String[]{} )[ 0 ] ) )
			.when( helper ).setProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.setProperties( props, true );

	}


	@Test( expected = NoSuchFlowException.class )
	public void testResetNonInstantiatedFlowsProperties() throws XbowException {

		List< String > props = new LinkedList< String >();

		doThrow( new NoSuchFlowException( flow.getName() ) )
			.when( helper ).resetProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

		flow.resetProperties( props, true );

	}


	@Test( expected = ValidationException.class )
	public void testResetInvalidFlowProperties() throws XbowException {

		List< String > props = new LinkedList< String >();
		props.add( "prioority" );

		doThrow( new ValidationException( props.get( 0 ) ) )
			.when( helper ).resetProperties( eq( flow.getName() ), eq( props ), anyBoolean() );

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