package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.etherstub.Etherstub;
import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.JNAEtherstubadm;
import agh.msc.xbowbase.lib.Etherstubadm;
import java.util.Map;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 *
 * @author robert boczek
 */
public class EtherstubTest {

    private Etherstubadm etherstubadm;
    private Etherstub etherstub;

    @Before
    public void setUp() {

        etherstubadm = mock(JNAEtherstubadm.class);
        etherstub = new Etherstub("etherstub1", false);
        etherstub.setEtherstubadm(etherstubadm);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testReadingParameters() throws EtherstubException{

        when(etherstubadm.getEtherstubParameter(etherstub.getName(), EtherstubParameters.BRIDGE)).thenReturn(null);
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), EtherstubParameters.MTU)).thenReturn("1200");
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), EtherstubParameters.OVER)).thenReturn(null);
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), EtherstubParameters.BRIDGE)).thenReturn("unknown");

        Map<EtherstubParameters, String> map = etherstub.getParameters();

        assertEquals(map.size(), 4);

        assertEquals("1200", map.get(EtherstubParameters.MTU));

        assertEquals(null, map.get(EtherstubParameters.STATE));
        
    }

    @Test
    public void testReadingProperties() throws EtherstubException{

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.LEARN_LIMIT)).thenReturn("1010");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.CPUS)).thenReturn(null);

        Map<EtherstubProperties, String> map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("980", map.get(EtherstubProperties.MAXBW));

        assertEquals(null, map.get(EtherstubProperties.CPUS));

    }

    @Test
    public void testReadingStatistics() throws EtherstubException{

        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.IERRORS)).thenReturn("2");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.IPACKETS)).thenReturn("12");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.OBYTES)).thenReturn("120");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.OERRORS)).thenReturn("2");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.OPACKETS)).thenReturn("12");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), EtherstubStatistics.RBYTES)).thenReturn("21");

        Map<EtherstubStatistics, String> map = etherstub.getStatistics();

        assertEquals(map.size(), 6);

        assertEquals("12", map.get(EtherstubStatistics.IPACKETS));

        assertEquals("2", map.get(EtherstubStatistics.OERRORS));

    }

    @Test
    public void testSettingNewLearnLimitValueProperty() throws EtherstubException{

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.LEARN_LIMIT)).thenReturn("1010");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.CPUS)).thenReturn(null);

        Map<EtherstubProperties, String> map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("1010", map.get(EtherstubProperties.LEARN_LIMIT));

        etherstub.setProperty(EtherstubProperties.LEARN_LIMIT, "120");

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.LEARN_LIMIT)).thenReturn("120");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), EtherstubProperties.CPUS)).thenReturn(null);

        map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("120", map.get(EtherstubProperties.LEARN_LIMIT));
   
    }

}
