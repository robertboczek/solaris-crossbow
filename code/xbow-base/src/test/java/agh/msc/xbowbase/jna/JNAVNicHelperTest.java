package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.VNicHelper;
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
            vnicHelper = new JNAVNicHelper();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSomething(){}

    
}
