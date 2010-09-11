package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.InvalidLinkNameException;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.TooLongLinkNameException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.VNicHelper;
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
    
}
