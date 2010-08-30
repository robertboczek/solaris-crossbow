package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.jna.JNAEtherstubHelper;
import agh.msc.xbowbase.lib.EtherstubHelper;
import java.util.Map;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
/**
 * Tests for Etherstub class
 * @author robert boczek
 */
public class EtherstubTest {

    private EtherstubHelper etherstubadm;
    private Etherstub etherstub;

    @Before
    public void setUp() {

        etherstubadm = mock(JNAEtherstubHelper.class);
        etherstub = new Etherstub("etherstub1", false);
        etherstub.setEtherstubHelper(etherstubadm);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testReadingParameters() throws EtherstubException{

        when(etherstubadm.getEtherstubParameter(etherstub.getName(), LinkParameters.BRIDGE)).thenReturn(null);
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), LinkParameters.MTU)).thenReturn("1200");
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), LinkParameters.OVER)).thenReturn(null);
        when(etherstubadm.getEtherstubParameter(etherstub.getName(), LinkParameters.BRIDGE)).thenReturn("unknown");

        Map<LinkParameters, String> map = etherstub.getParameters();

        assertEquals(map.size(), 4);

        assertEquals("1200", map.get(LinkParameters.MTU));

        assertEquals(null, map.get(LinkParameters.STATE));
        
    }

    @Test
    public void testReadingProperties() throws EtherstubException{

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("1010");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.CPUS)).thenReturn(null);

        Map<LinkProperties, String> map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("980", map.get(LinkProperties.MAXBW));

        assertEquals(null, map.get(LinkProperties.CPUS));

    }

    @Test
    public void testReadingStatistics() throws EtherstubException{

        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.IERRORS)).thenReturn("2");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.IPACKETS)).thenReturn("12");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.OBYTES)).thenReturn("120");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.OERRORS)).thenReturn("2");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.OPACKETS)).thenReturn("12");
        when(etherstubadm.getEtherstubStatistic(etherstub.getName(), LinkStatistics.RBYTES)).thenReturn("21");

        Map<LinkStatistics, String> map = etherstub.getStatistics();

        assertEquals(map.size(), 6);

        assertEquals("12", map.get(LinkStatistics.IPACKETS));

        assertEquals("2", map.get(LinkStatistics.OERRORS));

    }

    @Test
    public void testSettingNewLearnLimitValueProperty() throws EtherstubException{

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("1010");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.CPUS)).thenReturn(null);

        Map<LinkProperties, String> map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("1010", map.get(LinkProperties.LEARN_LIMIT));

        etherstub.setProperty(LinkProperties.LEARN_LIMIT, "120");

        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("120");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(etherstubadm.getEtherstubProperty(etherstub.getName(), LinkProperties.CPUS)).thenReturn(null);

        map = etherstub.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("120", map.get(LinkProperties.LEARN_LIMIT));
   
    }

}
