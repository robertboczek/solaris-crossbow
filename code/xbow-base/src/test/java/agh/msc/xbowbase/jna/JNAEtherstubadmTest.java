package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.Etherstubadm;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * JUnit test for @see JNAEtherstubadm
 * @author robert boczek
 */
public class JNAEtherstubadmTest {

    private Etherstubadm etherstubadm;
    private JNAEtherstubadm.IEtherstubadmin handle;

    @Before
    public void setUp() {

            handle = mock( JNAEtherstubadm.IEtherstubadmin.class );
            etherstubadm = new JNAEtherstubadm(handle);
    }

    @After
    public void tearDown() {

    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithWrongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.INVALID_ETHERSTUB_NAME.ordinal());

        etherstubadm.createEtherstub("fdsf", true);
    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithTheSameNameAsExisitngOne() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.CREATE_FAILURE.ordinal());

        etherstubadm.createEtherstub("etherstub1", true);
    }

    @Test(expected=EtherstubException.class)
    public void testCreatedEtherstubWithTooLongName() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.TOO_LONG_ETHERSTUB_NAME.ordinal());

        etherstubadm.createEtherstub("etherstubetherstubetherstubetherstubetherstub", true);
    }

    @Test
    public void testCreatingCorrectEtherstub() throws EtherstubException{

        when(handle.create_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubadm.createEtherstub("etherstub", true);
    }

    @Test(expected=EtherstubException.class)
    public void testDeleteUnexisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.DELETE_FAILURE.ordinal());

        etherstubadm.deleteEtherstub("etherstub123", true);
    }

    @Test
    public void testCorrectRemovalOfExisitngEtherstub() throws EtherstubException{

        when(handle.delete_etherstub(anyString(), anyInt())).thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubadm.deleteEtherstub("etherstub3", true);
    }

    @Test(expected=EtherstubException.class)
    public void testTryingToSetPropertyToUnexistingEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyInt(), anyString()))
                .thenReturn(EtherstubReturn.INVALID_ETHERSTUB_NAME.ordinal());

        etherstubadm.setEtherstubProperty("efdsfdsjkfdsjlk", EtherstubProperties.PRIORITY, "high");
    }

    @Test(expected=EtherstubException.class)
    public void testTryingToSetInvalidPropertyValue() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyInt(), anyString()))
                .thenReturn(EtherstubReturn.ETHERSTUB_PROPERTY_FAILURE.ordinal());

        etherstubadm.setEtherstubProperty("etherstub1", EtherstubProperties.PRIORITY, "veryhigh");
    }

    
    @Test
    public void testSucessfulTryingToSetPropertyToEtherstub() throws EtherstubException{

        when(handle.set_etherstub_property(anyString(), anyInt(), anyString()))
                .thenReturn(EtherstubReturn.RESULT_OK.ordinal());

        etherstubadm.setEtherstubProperty("etherstub1", EtherstubProperties.PRIORITY, "high");
    }
    
}
