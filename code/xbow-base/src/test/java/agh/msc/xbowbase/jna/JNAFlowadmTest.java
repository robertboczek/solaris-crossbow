package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.mapping.IFlowadm;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Pointer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class JNAFlowadmTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}


	@Before
	public void setUp() {

		handle = mock( IFlowadm.class );
		helper = new JNAFlowadm( handle );

	}

	@After
	public void tearDown() {
	}


	@Test( expected = NoSuchFlowException.class )
	public void testRemoveNonexistentFlow() throws XbowException {

		when( handle.remove_flow( anyString(), anyBoolean() ) ).thenReturn(
			XbowStatus.XBOW_STATUS_NOTFOUND.ordinal()
		);

		helper.remove( "nonexistent", true );

	}


	@Test
	public void testGetInfoForAllFlows() {

		IFlowadm.FlowInfosStruct fis = new IFlowadm.FlowInfosStruct();

		fis.flowInfos = new Pointer( 0 );
		fis.flowInfosLen = 0;

		when( handle.get_flows_info( null ) ).thenReturn( fis );

		helper.getFlowsInfo();

		verify( handle ).get_flows_info( null );
		verify( handle ).free_flow_infos( fis );

	}


	IFlowadm handle;
	Flowadm helper;

}
