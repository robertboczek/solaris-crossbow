package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.etherstub.Etherstub;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import javax.management.MBeanServer;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author robert boczek
 */
public class EtherstubPublisher {

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

}
