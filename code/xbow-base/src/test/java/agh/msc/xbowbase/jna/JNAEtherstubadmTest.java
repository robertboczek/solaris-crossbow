package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.jna.mapping.EtherstubHandle;
import agh.msc.xbowbase.lib.EtherstubHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * JUnit test for @see JNAEtherstubHelper
 * 
 * @author robert boczek
 */
public class JNAEtherstubadmTest {

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

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithWrongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.INVALID_ETHERSTUB_NAME.ordinal());

        etherstubHelper.createEtherstub("fdsf", true);
    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithTheSameNameAsExisitngOne() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.CREATE_FAILURE.ordinal());

        etherstubHelper.createEtherstub("etherstub1", true);
    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithTooLongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.TOO_LONG_ETHERSTUB_NAME.ordinal());

        etherstubHelper.createEtherstub("etherstubetherstubetherstubetherstubetherstub", true);
    }

    @Test
    public void testCreatingCorrectEtherstub() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubHelper.createEtherstub("etherstub", true);
    }

    @Test(expected=EtherstubException.class)
    public void testDeleteUnexisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.DELETE_FAILURE.ordinal());

        etherstubHelper.deleteEtherstub("etherstub123", true);
    }

    @Test
    public void testCorrectRemovalOfExisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubHelper.deleteEtherstub("etherstub3", true);
    }

    @Test(expected=EtherstubException.class)
    public void testTryingToSetPropertyToUnexistingEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(EtherstubReturn.INVALID_ETHERSTUB_NAME.ordinal());

        etherstubHelper.setEtherstubProperty("efdsfdsjkfdsjlk", LinkProperties.PRIORITY, "high");
    }

    @Test(expected=EtherstubException.class)
    public void testTryingToSetInvalidPropertyValue() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(EtherstubReturn.ETHERSTUB_PROPERTY_FAILURE.ordinal());

        etherstubHelper.setEtherstubProperty("etherstub1", LinkProperties.PRIORITY, "veryhigh");
    }

    
    @Test
    public void testSucessfulTryingToSetPropertyToEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyString(), anyString()))
                .thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubHelper.setEtherstubProperty("etherstub1", LinkProperties.PRIORITY, "high");
    }
    
}
