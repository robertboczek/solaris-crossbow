package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.LinkHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


/**
 * Tests for JNALinkHelper class
 *
 * @author robert boczek
 */
public class JNALinkHelperTest {

	@Before
	public void setUp() {

		handle = mock( LinkHandle.class );
		linkHelper = new JNALinkHelper( handle );

	}


	@After
	public void tearDown() {
	}


	@Test( expected = ValidationException.class )
	public void testSettingInvalidNetmask() throws XbowException {

		when( handle.set_netmask( anyString(), anyString() ) )
			.thenReturn( XbowStatus.XBOW_STATUS_INVALID_VALUE.ordinal() );

		linkHelper.setNetmask( "e1000g0", "24" );

	}


	@Test( expected = XbowException.class )
	public void testReactingToInternalError() throws XbowException {

		int rc = XbowStatus.values()[ XbowStatus.values().length - 1 ].ordinal();

		when( handle.set_netmask( anyString(), anyString() ) )
			.thenReturn( rc );

		linkHelper.setNetmask( "some-interface", "255.255.255.111" );

	}


	@Test
	public void testFreeMemoryAfterGetNetmask() {

		String netmask = "255.255.0.0";
		String link = "e1000g0";

		when( handle.get_netmask( anyString() ) )
			.thenReturn( netmask );

		linkHelper.getNetmask( link );

		verify( handle ).get_netmask( link );
		verify( handle ).free( netmask );

	}


	private LinkHandle handle;
	private LinkHelper linkHelper;

}
