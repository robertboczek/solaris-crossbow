package agh.msc.xbowbase.link;

import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.VNicHelper;
import agh.msc.xbowbase.publisher.Publisher;
import agh.msc.xbowbase.publisher.VNicMBeanPublisher;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests for VNicManager class
 *
 * @author robert boczek
 */
public class VNicManagerTest {

    private VNicHelper vnicHelper;
    private VNicManager vnicManager;
    private Publisher publisher;

    @Before
    public void setUp() {

        vnicHelper = mock(VNicHelper.class);
        publisher = mock(Publisher.class);
        vnicManager = new VNicManager();
        vnicManager.setVNicHelper(vnicHelper);
        vnicManager.setPublisher(publisher);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCreatingNewVNic() throws LinkException {

        when(vnicHelper.getLinkNames(anyBoolean())).thenReturn(new String[]{"vnic1"});

        vnicManager.create(new VNic("vnic1", true, "e1000g0"));

        assertEquals(1, vnicManager.getVNicsNames().size());

        assertEquals("vnic1", vnicManager.getVNicsNames().get(0));
    }

    @Test
    public void testPublishingVNicAfterCreation() throws LinkException {

        when(vnicHelper.getLinkNames(anyBoolean())).thenReturn(new String[]{});

        Publisher spyPublisher = spy(new VNicMBeanPublisher(null) {

            @Override
            public void publish(Object object) {
            }

            @Override
            public void unpublish(Object object) throws NotPublishedException {
            }

            @Override
            public List<Object> getPublished() {

                return new LinkedList<Object>();
            }
        });

        vnicManager.setPublisher(spyPublisher);

        VNicMBean vnic1 = new VNic("vnic1", true, "e1000g0");
        vnicManager.create(vnic1);

        InOrder inorder = inOrder(spyPublisher);
        inorder.verify(spyPublisher).publish(vnic1);

    }

    @Test
    public void testUnpublishingVNicAfterDeletion() throws Exception {

        when(vnicHelper.getLinkNames(true)).thenReturn(new String[]{"vnic1"});

        Publisher spyPublisher = spy(new VNicMBeanPublisher(null) {

            @Override
            public void publish(Object object) {
            }

            @Override
            public void unpublish(Object object) throws NotPublishedException {
            }

            @Override
            public List<Object> getPublished() {

                return new LinkedList<Object>();
            }
        });

        vnicManager.setPublisher(spyPublisher);

        vnicManager.delete("vnic1", false);

        InOrder inorder = inOrder(spyPublisher);
        inorder.verify(spyPublisher).unpublish(new VNic("vnic1", true, "e1000g0"));

    }

    @Test
    public void getVNicNames() throws LinkException {

        when(vnicHelper.getLinkNames(true)).thenReturn(new String[]{"vnic1", "vnic22"});

        assertEquals(2, vnicManager.getVNicsNames().size());

    }

    @Test
    public void testDiscoveryFunction() throws Exception {

        final VNic vnic1 = new VNic("vnic1", false, "e1000g0");
        final VNic vnic3 = new VNic("vnic3", false, "e1000g0");
        final VNic vnic5 = new VNic("vnic5", false, "e1000g0");

        when(vnicHelper.getLinkNames(anyBoolean())).thenReturn(new String[]{"vnic1", "vnic3"});

        Publisher spyPublisher = spy(new VNicMBeanPublisher(null) {

            @Override
            public void publish(Object object) {
            }

            @Override
            public void unpublish(Object object) throws NotPublishedException {
            }

            @Override
            public List<Object> getPublished() {

                return Arrays.asList(new Object[]{vnic1,
                            vnic5});
            }
        });

        vnicManager.setPublisher(spyPublisher);

        vnicManager.discover();

        InOrder inorder = inOrder(spyPublisher);
        inorder.verify(spyPublisher).publish(vnic3);
        inorder.verify(spyPublisher).unpublish(vnic5);

    }
}
