package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.publisher.EtherstubMBeanPublisher;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.etherstub.Etherstub;
import org.jims.modules.crossbow.etherstub.EtherstubMBean;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import javax.management.MBeanServer;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for @see EtherstubMBeanPublisher class
 *
 * @author robert boczek
 */
public class EtherstubMBeanPublisherTest {

    private Publisher publisher;
    private MBeanServer mbeanServer;

    @Before
    public void setUp() {
        mbeanServer = mock(MBeanServer.class);
        publisher = new EtherstubMBeanPublisher(mbeanServer);
    }

    @After
    public void tearDown() {
    }

    @Test(expected=NotPublishedException.class)
    public void testUnregisteringNotRegisteredEtherstub() throws NotPublishedException{

        publisher.unpublish(new Etherstub("etherstub", true));
    }

    @Test
    public void testUnregisteringPreviouslyRegisteredEtherstub() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        EtherstubMBean etherstub = new Etherstub("etherstub1", true);

        publisher.publish(etherstub);
        publisher.publish(new Etherstub("etherstub2", true));

        assertEquals(2, publisher.getPublished().size());

        publisher.unpublish(etherstub);

        assertEquals(1, publisher.getPublished().size());
    }

    @Test
    public void testCorrectRegisteringEtherstubs() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        publisher.publish(new Etherstub("etherstub1", true));
        publisher.publish(new Etherstub("etherstub2", true));

        assertEquals(2, publisher.getPublished().size());
    }

    @Test
    public void testMultipleRegisteringSameEtherstubs() throws NotPublishedException{

        assertEquals(0, publisher.getPublished().size());

        publisher.publish(new Etherstub("etherstub1", true));
        publisher.publish(new Etherstub("etherstub2", true));
        publisher.publish(new Etherstub("etherstub2", true));
        publisher.publish(new Etherstub("etherstub2", true));
        publisher.publish(new Etherstub("etherstub2", true));

        assertEquals(2, publisher.getPublished().size());
    }

}
