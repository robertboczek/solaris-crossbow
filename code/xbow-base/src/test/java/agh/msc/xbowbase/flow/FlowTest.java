package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.lib.Flowadm;
import java.util.HashMap;
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
	}

	@After
	public void tearDown() {
	}


	@Test
	public void testPropertiesManagement() throws ValidationException {
	
		Map< String, String > attributes = new HashMap< String, String >();
		attributes.put( "key0", "val0" );

		Flowadm flowadm = mock( Flowadm.class );
		doThrow( new ValidationException() ).when( flowadm ).setAttributes( anyString(), eq( attributes ) );

		Flow flow = new Flow();
		flow.setFlowadm( flowadm );

		Object exception = null;

		/*
		try {

			flow.setProperties( attributes, true );

		} catch ( ValidationException e ) {

			exception = e;

		}
		 *
		 */

		// assertNotNull( exception );
		assertNull( flow.getProperties() );
	
	}

}