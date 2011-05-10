package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.link.VNic;
import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.jna.JNAVNicHelper;
import org.jims.modules.crossbow.lib.VNicHelper;
import java.util.Map;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Test for VNic class
 *
 * @author robert boczek
 */
public class VNicTest {

    private VNicHelper vnicHelper;
    private VNic vnic;

    @Before
    public void setUp() {

        vnicHelper = mock(JNAVNicHelper.class);
        vnic = new VNic("vnic1", false, "e1000g0");
        vnic.setLinkHelper(vnicHelper);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadingParameters() throws LinkException {

        when(vnicHelper.getLinkParameter(vnic.getName(), LinkParameters.BRIDGE)).thenReturn(null);
        when(vnicHelper.getLinkParameter(vnic.getName(), LinkParameters.MTU)).thenReturn("1200");
        when(vnicHelper.getLinkParameter(vnic.getName(), LinkParameters.OVER)).thenReturn(null);
        when(vnicHelper.getLinkParameter(vnic.getName(), LinkParameters.BRIDGE)).thenReturn("unknown");

        Map<LinkParameters, String> map = vnic.getParameters();

        assertEquals( LinkParameters.values().length, map.size() );

        assertEquals("1200", map.get(LinkParameters.MTU));

        assertEquals(null, map.get(LinkParameters.STATE));

    }

    @Test
    public void testReadingProperties() throws LinkException {

        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("1010");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.CPUS)).thenReturn(null);

        Map<LinkProperties, String> map = vnic.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("980", map.get(LinkProperties.MAXBW));

        assertEquals(null, map.get(LinkProperties.CPUS));

    }

    @Test
    public void testReadingStatistics() throws LinkException {

        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.IERRORS)).thenReturn("2");
        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.IPACKETS)).thenReturn("12");
        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.OBYTES)).thenReturn("120");
        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.OERRORS)).thenReturn("2");
        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.OPACKETS)).thenReturn("12");
        when(vnicHelper.getLinkStatistic(vnic.getName(), LinkStatistics.RBYTES)).thenReturn("21");

        Map<LinkStatistics, String> map = vnic.getStatistics();

        assertEquals(map.size(), 6);

        assertEquals("12", map.get(LinkStatistics.IPACKETS));

        assertEquals("2", map.get(LinkStatistics.OERRORS));

    }

    @Test
    public void testSettingNewLearnLimitValueProperty() throws LinkException {

        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("1010");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.CPUS)).thenReturn(null);

        Map<LinkProperties, String> map = vnic.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("1010", map.get(LinkProperties.LEARN_LIMIT));

        vnic.setProperty(LinkProperties.LEARN_LIMIT, "120");

        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.LEARN_LIMIT)).thenReturn("120");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.PRIORITY)).thenReturn("low");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.MAXBW)).thenReturn("980");
        when(vnicHelper.getLinkProperty(vnic.getName(), LinkProperties.CPUS)).thenReturn(null);

        map = vnic.getProperties();

        assertEquals(map.size(), 4);

        assertEquals("120", map.get(LinkProperties.LEARN_LIMIT));

    }

    //@todo write tests testing setting/getting ip
}
