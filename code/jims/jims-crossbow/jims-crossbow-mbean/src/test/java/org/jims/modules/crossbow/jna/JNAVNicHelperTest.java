package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.jna.XbowStatus;
import org.jims.modules.crossbow.jna.JNAVNicHelper;
import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.InvalidLinkNameException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.TooLongLinkNameException;
import org.jims.modules.crossbow.jna.mapping.LinkHandle;
import org.jims.modules.crossbow.lib.VNicHelper;
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
public class JNAVNicHelperTest {

    private VNicHelper vnicHelper;
    private LinkHandle handle;

    @Before
    public void setUp() {

            handle = mock( LinkHandle.class );
            vnicHelper = new JNAVNicHelper(handle);
    }

    @After
    public void tearDown() {

    }

    @Test(expected=InvalidLinkNameException.class)
    public void testCreatingVNicWithWrongName() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        vnicHelper.createVNic("erewfdsfdsf", true, "parent1");
    }

    @Test(expected=TooLongLinkNameException.class)
    public void testCreatingVNicWithTooLongName() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal());

        vnicHelper.createVNic("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", true, "parent1");
    }

    @Test(expected=LinkException.class)
    public void testCreatingVNicWithTheSameNameAsExisitngOne() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        vnicHelper.createVNic("vnic1", true, "parent1");
    }

    @Test
    public void testCreatingCorrectVNic() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        vnicHelper.createVNic("vnic1", true, "parent1");
    }

    @Test(expected=InvalidLinkNameException.class)
    public void testCreatingVNicWithWrongParentLinkName() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_PARENT_NAME.ordinal());

        vnicHelper.createVNic("vnic1", true, "dfdsfdksjf");
    }

    @Test(expected=TooLongLinkNameException.class)
    public void testCreatingVNicWithTooLongParentLinkName() throws LinkException{

        when(handle.create_vnic(anyString(), anyInt(), anyString())).thenReturn(XbowStatus.XBOW_STATUS_TOO_LONG_PARENT_NAME.ordinal());

        vnicHelper.createVNic("vnic1", true, "parent1parent1parent1parent1parent1parent1parent1parent1");
    }

    @Test
    public void testCorrectRemovalOfVNic() throws LinkException{

        when(handle.delete_vnic(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        vnicHelper.deleteVNic("vnic1", true);
    }

    @Test(expected=InvalidLinkNameException.class)
    public void testRemovingVNicWithWrongName() throws LinkException{

        when(handle.delete_vnic(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        vnicHelper.deleteVNic("vnic1", true);
    }

    @Test(expected=TooLongLinkNameException.class)
    public void testRemovingVNicWithTooLongName() throws LinkException{

        when(handle.delete_vnic(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal());

        vnicHelper.deleteVNic("vnic1", true);
    }

    @Test(expected=LinkException.class)
    public void testRemovingVNicWhenOperationFailed() throws LinkException{

        when(handle.delete_vnic(anyString(), anyInt())).thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        vnicHelper.deleteVNic("vnic1", true);
    }

    @Test(expected=InvalidLinkNameException.class)
    public void testTryingToSetPropertyToUnexistingEtherstubVNic() throws LinkException{

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal());

        vnicHelper.setLinkProperty("efdsfdsjkfdsjlk", LinkProperties.PRIORITY, "high");
    }

    @Test(expected=LinkException.class)
    public void testTryingToSetInvalidPropertyValue() throws LinkException{

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal());

        vnicHelper.setLinkProperty("vnic1", LinkProperties.PRIORITY, "veryhigh");
    }


    @Test
    public void testSucessfulTryingToSetPropertyToVNic() throws LinkException{

        when(handle.set_link_property(anyString(), anyString(), anyString()))
                .thenReturn(XbowStatus.XBOW_STATUS_OK.ordinal());

        vnicHelper.setLinkProperty("etherstub1", LinkProperties.PRIORITY, "high");
    }

    @Test
    public void testGettingNullArrayOfVNicsNames() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_names(anyInt()))
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(null);

        String []names = vnicHelper.getLinkNames(true);

        assertNotNull(names);
        assertEquals(0, names.length);
    }

    @Test
    public void testGettingArrayOfVnicNames() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_names(anyInt()))
                .thenReturn(p);

        when(p.getStringArray(0)).thenReturn(new String[]{"vnic1", "vnic16"});

        String []names = vnicHelper.getLinkNames(true);

        assertEquals(2, names.length);
        assertEquals("vnic1", names[0]);
    }

    @Test
    public void testGettingVNicParameter() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_parameter(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1500");

        String value = vnicHelper.getLinkParameter("vnic1", LinkParameters.MTU);

        assertEquals("1500", value);
    }

    @Test
    public void testGettingVNicProperty() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_property(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("9000");

        String value = vnicHelper.getLinkProperty("vnic1", LinkProperties.MAXBW);

        assertEquals("9000", value);
    }

    @Test
    public void testGettingLinkStatistic() throws LinkException{

        Pointer p = mock(Pointer.class);

        when(handle.get_link_statistic(anyString(), anyString()))
                .thenReturn(p);

        when(p.getString(0)).thenReturn("1543");

        String value = vnicHelper.getLinkStatistic("vnic1", LinkStatistics.IPACKETS);

        assertEquals("1543", value);
    }
    
}
