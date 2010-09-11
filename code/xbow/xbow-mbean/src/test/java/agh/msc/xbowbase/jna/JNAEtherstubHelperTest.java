package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.InvalidEtherstubNameException;
import agh.msc.xbowbase.exception.TooLongEtherstubNameException;
import agh.msc.xbowbase.jna.mapping.EtherstubHandle;
import agh.msc.xbowbase.lib.EtherstubHelper;
import com.sun.jna.Pointer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * JUnit test for @see JNAEtherstubHelper
 * 
 * @author robert boczek
 */
public class JNAEtherstubHelperTest {

    private EtherstubHelper etherstubHelper;
    private EtherstubHandle handle;

    @Before
    public void setUp() {

            handle = mock( EtherstubHandle.class );
            etherstubHelper = new JNAEtherstubHelper(handle);
    }

    @After
    public void tearDown() {

    }

    @Test(expected=InvalidEtherstubNameException.class)
    public void testCreatedEtherstubWithWrongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        etherstubHelper.createEtherstub("fdsf", true);
    }

    @Test(expected=TooLongEtherstubNameException.class)
    public void testCreatedEtherstubWithTooLongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal());

        etherstubHelper.createEtherstub("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", true);
    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithTheSameNameAsExisitngOne() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        etherstubHelper.createEtherstub("etherstub1", true);
    }

    @Test
    public void testCreatingCorrectEtherstub() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        etherstubHelper.createEtherstub("etherstub", true);
    }

    @Test(expected=InvalidEtherstubNameException.class)
    public void testDeleteUnexisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        etherstubHelper.deleteEtherstub("etherstub123", true);
    }

    @Test
    public void testCorrectRemovalOfExisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        etherstubHelper.deleteEtherstub("etherstub3", true);
    }

    @Test(expected=InvalidEtherstubNameException.class)
    public void testTryingToSetPropertyToUnexistingEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        etherstubHelper.setEtherstubProperty("efdsfdsjkfdsjlk", LinkProperties.PRIORITY, "high");
    }

    @Test(expected=EtherstubException.class)
    public void testTryingToSetInvalidPropertyValue() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        etherstubHelper.setEtherstubProperty("etherstub1", LinkProperties.PRIORITY, "veryhigh");
    }

    
    @Test
    public void testSucessfulTryingToSetPropertyToEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        etherstubHelper.setEtherstubProperty("etherstub1", LinkProperties.PRIORITY, "high");
    }

    @Test
    public void testGettingNullArrayOfEtherstubsNames() throws EtherstubException{

        Pointer p = mock(Pointer.class);        

        when(handle.get_etherstub_names())
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(null);

        String []names = etherstubHelper.getEtherstubNames();

        assertNotNull(names);
        assertEquals(0, names.length);
    }

    @Test
    public void testGettingArrayOfEtherstubsNames() throws EtherstubException{

        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_names())
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(new String[]{"etherstub1", "ether12"});

        String []names = etherstubHelper.getEtherstubNames();

        assertEquals(2, names.length);
        assertEquals("etherstub1", names[0]);
    }

    @Test
    public void testFreeingTheMemoryAfterReadingEtherstubNames() throws EtherstubException{

        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_names())
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(new String[]{"etherstub1", "ether12"});


        etherstubHelper.getEtherstubNames();

        verify(handle).get_etherstub_names();
        verify(p).getStringArray(0);
        verify(handle).free_char_array(p);
        
    }

    @Test
    public void testGettingEtherstubParameter() throws EtherstubException{

        String value = "1500";
        String etherName = "ether1";
        LinkParameters linkParameter = LinkParameters.MTU;
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        String parameterValue = etherstubHelper.getEtherstubParameter(etherName, linkParameter);

        assertEquals(value, parameterValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingEtherstubParameterValue() throws EtherstubException{

        String etherName = "ether1";
        LinkParameters linkParameter = LinkParameters.MTU;
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1500");

        etherstubHelper.getEtherstubParameter(etherName, linkParameter);

        verify(handle).get_etherstub_parameter(etherName, linkParameter.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);

    }

    @Test
    public void testGettingEtherstubProperty() throws EtherstubException{

        String etherName = "ether1";
        LinkProperties linkProperty = LinkProperties.MAXBW;
        String value = "9000";
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        String propertyValue = etherstubHelper.getEtherstubProperty(etherName, linkProperty);

        assertEquals(value, propertyValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingEtherstubPropertyValue() throws EtherstubException{

        String etherName = "ether1";
        LinkProperties linkProperty = LinkProperties.MAXBW;
        String value = "9000";
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        etherstubHelper.getEtherstubProperty(etherName, linkProperty);

        verify(handle).get_etherstub_property(etherName, linkProperty.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);
    }

    @Test
    public void testGettingEtherstubStatistic() throws EtherstubException{

        String value = "1543";
        String etherName = "ether1";
        LinkStatistics linkStatistic = LinkStatistics.IPACKETS;
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        String statisticValue = etherstubHelper.getEtherstubStatistic(etherName, linkStatistic);

        assertEquals(value, statisticValue);
    }

    @Test
    public void testFreeingMemoryAfterGettingEtherstubStatisticValue() throws EtherstubException{

        String value = "1543";
        String etherName = "ether1";
        LinkStatistics linkStatistic = LinkStatistics.IPACKETS;
        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn(value);

        etherstubHelper.getEtherstubStatistic(etherName, linkStatistic);

        verify(handle).get_etherstub_statistic(etherName, linkStatistic.toString());
        verify(p).getString(0);
        verify(handle).free_char_string(p);
    }


}
