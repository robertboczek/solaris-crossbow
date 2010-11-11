package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.jna.XbowStatus;
import org.jims.modules.crossbow.jna.JNALinkHelper;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.jna.mapping.LinkHandle;
import org.jims.modules.crossbow.lib.LinkHelper;
import org.jims.modules.crossbow.link.validators.LinkValidator;
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

}
