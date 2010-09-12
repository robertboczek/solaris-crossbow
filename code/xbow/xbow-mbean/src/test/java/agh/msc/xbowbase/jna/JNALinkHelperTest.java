package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.LinkHelper;
import agh.msc.xbowbase.link.validators.LinkValidator;
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

    private LinkHelper linkHelper;
    private LinkHandle handle;
    private LinkValidator linkValidator;

    @Before
    public void setUp() {

            handle = mock( LinkHandle.class );
            linkValidator = mock( LinkValidator.class );
            linkHelper = new JNALinkHelper(handle);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testReadingIpAddressFromUnexistingLink() throws LinkException{

        when(handle.get_ip_address(anyString())).thenReturn(null);

        linkHelper.getIpAddress("unexistingLink");

    }

    @Test
    public void testSuccessfulReadingIpAddress() throws LinkException{

        String linkName = "e1000g0";
        String address = "192.168.0.1";
        when(handle.get_ip_address(anyString())).thenReturn(address);

        assertEquals(address, linkHelper.getIpAddress(linkName));
    }

    @Test
    public void testSuccessfulSettingIpAddress() throws LinkException, ValidationException{

        String linkName = "e1000g0";
        String address = "192.168.0.10";
        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));
        
        linkHelper.setIpAddress(linkName, address);
        assertEquals(address, linkHelper.getIpAddress(linkName));
    }

    @Test(expected=LinkException.class)
    public void testSettingIpAddressWhenOperationFails() throws LinkException, ValidationException{

        String linkName = "e1000g0";
        String address = "192.168.0.10";

        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));
        linkHelper.setIpAddress(linkName, address);
    }

    @Test(expected=ValidationException.class)
    public void testSettingIpAddressWithWrongFormat() throws LinkException, ValidationException{

        String linkName = "e1000g0";
        String address = "02.266.0.10";

        linkHelper = new JNALinkHelper(handle, linkValidator);
        when(linkValidator.isIpAddressValid(anyString())).thenReturn(Boolean.FALSE);

        linkHelper.setIpAddress(linkName, address);
    }

    @Test
    public void testSettingIpAddressWithCorrectFormat() throws LinkException, ValidationException{

        String linkName = "e1000g0";
        String address = "102.243.0.10";

        linkHelper = new JNALinkHelper(handle, linkValidator);
        when(linkValidator.isIpAddressValid(anyString())).thenReturn(Boolean.TRUE);

        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        linkHelper.setIpAddress(linkName, address);
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
}
