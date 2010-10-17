package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.InvalidEtherstubNameException;
import agh.msc.xbowbase.exception.TooLongEtherstubNameException;
import agh.msc.xbowbase.lib.EtherstubHelper;
import agh.msc.xbowbase.publisher.EtherstubMBeanPublisher;
import agh.msc.xbowbase.publisher.Publisher;
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
 * Tests  for EtherstubManager class
 *
 * @author robert boczek
 */
public class EtherstubManagerTest {

    private EtherstubHelper etherstubHelper;
    private EtherstubManager etherstubManager;
    private Publisher publisher;

    @Before
    public void setUp() {

            etherstubHelper = mock(EtherstubHelper.class);
            publisher = mock(Publisher.class);
            etherstubManager = new EtherstubManager();
            etherstubManager.setEtherstHelper(etherstubHelper);
            etherstubManager.setPublisher(publisher);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testCreatingNewEtherstub() throws EtherstubException{

        when(etherstubHelper.getEtherstubNames()).thenReturn(new String[]{"etherstub"});

        etherstubManager.create(new Etherstub("etherstub", true));

        assertEquals(1, etherstubManager.getEtherstubsNames().size());

        assertEquals("etherstub", etherstubManager.getEtherstubsNames().get(0));
    }

    @Test(expected=TooLongEtherstubNameException.class)
    public void testCreatingNewEtherstubWithTooLongName() throws EtherstubException{

        doThrow(new TooLongEtherstubNameException("")).when(etherstubHelper)
                .createEtherstub(anyString(), anyBoolean());

        
        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.create(new Etherstub("etherstubetherstubetherstubetherstubetherstubetherstubetherstubetherstub", true));

    }

    @Test(expected=InvalidEtherstubNameException.class)
    public void testCreatingNewEtherstubWithWithInvalidName() throws EtherstubException{

        doThrow(new InvalidEtherstubNameException("")).when(etherstubHelper)
                .createEtherstub(anyString(), anyBoolean());

        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.create(new Etherstub("invalidName", true));

    }

    @Test(expected=EtherstubException.class)
    public void testCreatingNewEtherstubWhenOperationFailes() throws EtherstubException{

        doThrow(new EtherstubException("")).when(etherstubHelper)
                .createEtherstub(anyString(), anyBoolean());

        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.create(new Etherstub("invalidName", true));

    }

    @Test(expected=TooLongEtherstubNameException.class)
    public void testRemovingEtherstubWhenEtherstubNameIsTooLong() throws EtherstubException{

        doThrow(new TooLongEtherstubNameException("")).when(etherstubHelper).
                deleteEtherstub(anyString(), anyBoolean());

        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.delete("etherstub128etherstub128etherstub128etherstub128etherstub128etherstub128etherstub128etherstub128", true);

    }

    @Test(expected=InvalidEtherstubNameException.class)
    public void testRemovingEtherstubWhenEtherstubNameIsInvalid() throws EtherstubException{

        doThrow(new InvalidEtherstubNameException("")).when(etherstubHelper).
                deleteEtherstub(anyString(), anyBoolean());

        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.delete("invalidEtherstubName", true);

    }

    @Test(expected=EtherstubException.class)
    public void testRemovingEtherstubWhenOperationFailes() throws EtherstubException{

        doThrow(new EtherstubException("")).when(etherstubHelper).deleteEtherstub(anyString(), anyBoolean());

        etherstubManager.setEtherstHelper(etherstubHelper);
        etherstubManager.delete("etherstub128", true);

    }

    @Test
    public void testUnpublishingEtherstubAfterDeletion() throws Exception{

        when(etherstubHelper.getEtherstubNames()).thenReturn(new String[]{"ether1"});

        Publisher spyPublisher = spy(new EtherstubMBeanPublisher(null){

            @Override
            public void publish( Object object ){

            }

            @Override
            public void unpublish( Object object ) throws NotPublishedException{

            }

            @Override
            public List< Object > getPublished(){

                return new LinkedList<Object>();
            }

        });

        etherstubManager.setPublisher(spyPublisher);

        etherstubManager.delete("ether1", false);

        InOrder inorder = inOrder(spyPublisher);
        inorder.verify(spyPublisher).unpublish(new Etherstub("ether1", false));

    }



    @Test
    public void getEtherstubNames() throws EtherstubException{

        when(etherstubHelper.getEtherstubNames()).thenReturn(new String[]{"etherstub1", "ethertstub22"});

        assertEquals(2, etherstubManager.getEtherstubsNames().size());
        
    }

    @Test
    public void testDiscoveryFunction() throws Exception{

        when(etherstubHelper.getEtherstubNames()).thenReturn(new String[]{"ether1", "ether3"});

        Publisher spyPublisher = spy(new EtherstubMBeanPublisher(null){

            @Override
            public void publish( Object object ){

            }

            @Override
            public void unpublish( Object object ) throws NotPublishedException{

            }

            @Override
            public List< Object > getPublished(){

                return Arrays.asList(new Object[]{new Etherstub("ether5", false),
                    new Etherstub("ether3", false)});
            }

        });
        
        etherstubManager.setPublisher(spyPublisher);

        etherstubManager.discover();

        InOrder inorder = inOrder(spyPublisher);
        inorder.verify(spyPublisher).publish(new Etherstub("ether1", false));
        inorder.verify(spyPublisher).unpublish(new Etherstub("ether5", false));

    }

}
