package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.InvalidLinkNameException;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.LinkHelper;
import agh.msc.xbowbase.link.validators.LinkValidator;
import com.sun.jna.Pointer;
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

        handle = mock(LinkHandle.class);
        linkValidator = mock(LinkValidator.class);
        linkHelper = new JNALinkHelper(handle);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadingIpAddressFromUnexistingLink() throws LinkException {

        when(handle.get_ip_address(anyString())).thenReturn(null);

        linkHelper.getIpAddress("unexistingLink");

    }

    @Test
    public void testSuccessfulReadingIpAddress() throws LinkException {

        String linkName = "e1000g0";
        String address = "192.168.0.1";
        when(handle.get_ip_address(anyString())).thenReturn(address);

        assertEquals(address, linkHelper.getIpAddress(linkName));
    }

    @Test
    public void testSuccessfulSettingIpAddress() throws LinkException, ValidationException {

        String linkName = "e1000g0";
        String address = "192.168.0.10";
        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));

        linkHelper.setIpAddress(linkName, address);
        assertEquals(address, linkHelper.getIpAddress(linkName));
    }

    @Test(expected = LinkException.class)
    public void testSettingIpAddressWhenOperationFails() throws LinkException, ValidationException {

        String linkName = "e1000g0";
        String address = "192.168.0.10";

        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        assertEquals(address, linkHelper.getIpAddress(linkName));
        linkHelper.setIpAddress(linkName, address);
    }

    @Test(expected = ValidationException.class)
    public void testSettingIpAddressWithWrongFormat() throws LinkException, ValidationException {

        String linkName = "e1000g0";
        String address = "02.266.0.10";

        linkHelper = new JNALinkHelper(handle, linkValidator);
        when(linkValidator.isIpAddressValid(anyString())).thenReturn(Boolean.FALSE);

        linkHelper.setIpAddress(linkName, address);
    }

    @Test
    public void testSettingIpAddressWithCorrectFormat() throws LinkException, ValidationException {

        String linkName = "e1000g0";
        String address = "102.243.0.10";

        linkHelper = new JNALinkHelper(handle, linkValidator);
        when(linkValidator.isIpAddressValid(anyString())).thenReturn(Boolean.TRUE);

        when(handle.get_ip_address(anyString())).thenReturn(address);

        when(handle.set_ip_address(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        linkHelper.setIpAddress(linkName, address);
    }

    @Test
    public void testFreeMemoryAfterGetIpAddress() throws Exception {

        String ipAddress = "192.168.0.101";
        String link = "e1000g0";

        when(handle.get_ip_address(anyString())).thenReturn(ipAddress);

        linkHelper.getIpAddress(link);

        verify(handle).get_ip_address(link);
        verify(handle).free(ipAddress);

    }

    @Test(expected = ValidationException.class)
    public void testSettingInvalidNetmask() throws XbowException {

        when(handle.set_netmask(anyString(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_VALUE.ordinal());

        linkHelper.setNetmask("e1000g0", "24");

    }

    @Test(expected = XbowException.class)
    public void testReactingToInternalError() throws XbowException {

        int rc = XbowStatus.values()[XbowStatus.values().length - 1].ordinal();

        when(handle.set_netmask(anyString(), anyString())).thenReturn(rc);

        linkHelper.setNetmask("some-interface", "255.255.255.111");

    }

    @Test
    public void testFreeMemoryAfterGetNetmask() {

        String netmask = "255.255.0.0";
        String link = "e1000g0";

        when(handle.get_netmask(anyString())).thenReturn(netmask);

        linkHelper.getNetmask(link);

        verify(handle).get_netmask(link);
        verify(handle).free(netmask);

    }

    @Test(expected=InvalidLinkNameException.class)
    public void testTryingToSetPropertyToUnexistingLink() throws LinkException{

        String linkName = "efdsfdsjkfdsjlk";
        LinkProperties linkProperty = LinkProperties.PRIORITY;
        String value = "high";

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        linkHelper.setLinkProperty(linkName, linkProperty, value);

        verify(handle).set_link_property(linkName, linkProperty.toString(), value);

    }

    @Test(expected=LinkException.class)
    public void testTryingToSetInvalidPropertyValueToLink() throws LinkException{

        String linkName = "vnic1";
        LinkProperties linkProperty = LinkProperties.PRIORITY;
        String value = "veryhigh";

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        linkHelper.setLinkProperty(linkName, linkProperty, value);

        verify(handle).set_link_property(linkName, linkProperty.toString(), value);
    }


    @Test
    public void testSucessfulAttemptToSetPropertyToVNic() throws LinkException{

        String linkName = "vnic1";
        LinkProperties linkProperty = LinkProperties.PRIORITY;
        String value = "high";

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        linkHelper.setLinkProperty(linkName, linkProperty, value);

        verify(handle).set_link_property(linkName, linkProperty.toString(), value);
    }

    @Test
    public void testGettingNullArrayOfVNicsNames() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_names(anyInt()))
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(null);

        String []names = linkHelper.getLinkNames(true);

        assertNotNull(names);
        assertEquals(0, names.length);
    }

    @Test
    public void testGettingArrayOfVnicNames() throws LinkException{

        String linkName = "vnic1";
        String linkName2 = "vnic16";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_names(anyInt()))
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(new String[]{linkName, linkName2});

        String []names = linkHelper.getLinkNames(true);

        assertEquals(2, names.length);
        assertEquals(linkName, names[0]);
    }

    @Test
    public void testFreeingMemoryAfterGettingArrayOfVnicNames() throws LinkException{

        String linkName = "vnic1";
        String linkName2 = "vnic16";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_names(anyInt()))
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(new String[]{linkName, linkName2});

        linkHelper.getLinkNames(true);

        verify(handle).get_link_names(0);
        verify(p).getStringArray(0);
        verify(handle).free_char_array(p);
    }

    @Test
    public void testGettingVNicParameter() throws LinkException{

        String linkName = "vnic1";
        LinkParameters linkParameter = LinkParameters.MTU;
        String value = "1500";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1500");

        String parameterValue = linkHelper.getLinkParameter(linkName, linkParameter);

        assertEquals(value, parameterValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingVNicParameter() throws LinkException{

        String linkName = "vnic1";
        LinkParameters linkParameter = LinkParameters.MTU;
        String value = "1500";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1500");

        linkHelper.getLinkParameter(linkName, linkParameter);

        verify(handle).get_link_parameter(linkName, linkParameter.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);
    }

    @Test
    public void testGettingVNicProperty() throws LinkException{

        String linkName = "vnic1";
        LinkProperties linkProperty = LinkProperties.MAXBW;
        String value = "9000";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        String propertyValue = linkHelper.getLinkProperty(linkName, linkProperty);

        assertEquals(value, propertyValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingVNicProperty() throws LinkException{

        String linkName = "vnic1";
        LinkProperties linkProperty = LinkProperties.MAXBW;
        String value = "9000";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        linkHelper.getLinkProperty(linkName, linkProperty);

        verify(handle).get_link_property(linkName, linkProperty.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);
    }

    @Test
    public void testGettingLinkStatistic() throws LinkException{

        String linkName = "vnic1";
        LinkStatistics linkStatistic = LinkStatistics.IPACKETS;
        String value = "1543";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        String statisticValue = linkHelper.getLinkStatistic(linkName, linkStatistic);

        assertEquals(value, statisticValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingLinkStatistic() throws LinkException{

        String linkName = "vnic1";
        LinkStatistics linkStatistic = LinkStatistics.IPACKETS;
        String value = "1543";
        Pointer p = mock(Pointer.class);

        when(handle.get_link_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        linkHelper.getLinkStatistic(linkName, linkStatistic);

        verify(handle).get_link_statistic(linkName, linkStatistic.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);
    }
}
