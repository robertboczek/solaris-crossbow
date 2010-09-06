package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.LinkException;
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

    private LinkHelper linkHelper;
    private LinkHandle handle;

    @Before
    public void setUp() {

            handle = mock( LinkHandle.class );
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
    public void testSuccessfulSettingIpAddress() throws LinkException{

        String linkName = "e1000g0";
        String address = "192.168.0.10";
        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));
        
        linkHelper.setIpAddress(linkName, address);
        assertEquals(address, linkHelper.getIpAddress(linkName));
    }

    @Test(expected=LinkException.class)
    public void testSettingIpAddressWhenOperationFails() throws LinkException{

        String linkName = "e1000g0";
        String address = "192.168.0.10";

        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));
        linkHelper.setIpAddress(linkName, address);
    }
}
