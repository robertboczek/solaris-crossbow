package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.etherstub.Etherstub;
import agh.msc.xbowbase.etherstub.EtherstubManager;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.Etherstubadm;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author robert boczek
 */
public class EtherstubManagerTest {

    private Etherstubadm etherstubadm;
    private EtherstubManager etherstubManager;

    @Before
    public void setUp() {

            etherstubadm = mock(Etherstubadm.class);
            etherstubManager = new EtherstubManager();
            etherstubManager.setEtherstubadm(etherstubadm);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreatingNewEtherstub() throws EtherstubException{

        when(etherstubadm.getEtherstubNames()).thenReturn(new String[]{"etherstub"});      

        etherstubManager.create(new Etherstub("etherstub", true));

        assertEquals(1, etherstubManager.getEtherstubsNames().size());

        assertEquals("etherstub", etherstubManager.getEtherstubsNames().get(0));
    }

    @Test
    public void testRemovingEtherstub() throws EtherstubException{

        when(etherstubadm.getEtherstubNames()).thenReturn(null);

        etherstubManager.create(new Etherstub("etherstub", true));

        etherstubManager.delete("etherstub1", true);

        assertEquals(0, etherstubManager.getEtherstubsNames().size());

    }

}
