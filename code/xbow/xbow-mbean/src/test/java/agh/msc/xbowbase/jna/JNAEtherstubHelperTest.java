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
    public void testGettingEtherstubParameter() throws EtherstubException{

        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1500");

        String value = etherstubHelper.getEtherstubParameter("ether1", LinkParameters.MTU);

        assertEquals("1500", value);
    }

    @Test
    public void testGettingEtherstubProperty() throws EtherstubException{

        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("9000");

        String value = etherstubHelper.getEtherstubProperty("ether1", LinkProperties.MAXBW);

        assertEquals("9000", value);
    }

    @Test
    public void testGettingEtherstubStatistic() throws EtherstubException{

        Pointer p = mock(Pointer.class);

        when(handle.get_etherstub_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1543");

        String value = etherstubHelper.getEtherstubStatistic("ether1", LinkStatistics.IPACKETS);

        assertEquals("1543", value);
    }


}
