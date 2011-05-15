package org.jims.modules.crossbow.publisher;


import org.jims.modules.crossbow.link.VNic;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import javax.management.MBeanServer;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests for @see VNicMBeanPublisher class
 *
 * @author robert boczek
 */
public class VNicMBeanPublisherTest {

    private Publisher publisher;
    private MBeanServer mbeanServer;

    @Before
    public void setUp() {
        mbeanServer = mock(MBeanServer.class);
        publisher = new VNicMBeanPublisher(mbeanServer);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void sth(){

    }

    @Test(expected=NotPublishedException.class)
    public void testUnregisteringNotRegisteredVNic() throws NotPublishedException{

        publisher.unpublish(new VNic("vnic1", true, "e1000g0"));
    }

    @Test
    public void testUnregisteringPreviouslyRegisteredVNic() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        VNicMBean vnic = new VNic("vnic1", true, "e1000g0");

        publisher.publish(vnic);
        publisher.publish(new VNic("vnic2", true, "e1000g0"));

        assertEquals(2, publisher.getPublished().size());

        publisher.unpublish( "vnic1" );

        assertEquals(1, publisher.getPublished().size());
    }

    @Test
    public void testCorrectRegisteringVNics() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        publisher.publish(new VNic("vnic1", true, "e1000g0"));
        publisher.publish(new VNic("vnic13", true, "e1000g0"));

        assertEquals(2, publisher.getPublished().size());
    }

    @Test
    public void testMultipleRegisteringSameVNics() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        publisher.publish(new VNic("vnic1", true, "e1000g0"));
        publisher.publish(new VNic("vnic2", true, "e1000g0"));
        publisher.publish(new VNic("vnic2", true, "e1000g0"));
        publisher.publish(new VNic("vnic2", true, "e1000g0"));
        publisher.publish(new VNic("vnic2", true, "e1000g0"));

        assertEquals(2, publisher.getPublished().size());
    }
}
