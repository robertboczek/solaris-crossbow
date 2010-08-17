package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.Etherstubadm;
import agh.msc.xbowbase.publisher.Publisher;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.logging.Level;
import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Implementation of EtherstubManagerMBean
 * @see EtherstubManagerMBean
 * @author robert boczek
 */
public class EtherstubManager implements EtherstubManagerMBean, NotificationListener {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Etherstub.class);
    private Etherstubadm etherstubadm;
    private final Set<EtherstubMBean> etherstubsSet;
    private Publisher publisher;

    /**
     * Construtor of EtherstubManager, creates empty map of existing etherstubs
     */
    public EtherstubManager() {
        this.etherstubsSet = new HashSet<EtherstubMBean>();
    }

    /**
     * @see EtherstubManagerMBean#create(agh.msc.xbowbase.etherstub.EtherstubMBean) 
     */
    @Override
    public void create(EtherstubMBean etherstubMBean) throws EtherstubException {
        try {
            this.etherstubadm.createEtherstub(etherstubMBean.getName(), etherstubMBean.isTemporary());
            synchronized (etherstubsSet) {
                registerNewEtherstubMBean(etherstubMBean);
            }
            discover();
        } catch (EtherstubException e) {
            logger.error("Etherstub " + etherstubMBean + " couldn't be created", e);
            throw e;
        }
    }

    /**
     *  @see EtherstubManager#delete(java.lang.String, boolean)
     */
    @Override
    public void delete(String name, boolean temporary) throws EtherstubException {
        try {
            EtherstubMBean etherstubMBean = new Etherstub(name, temporary);
            this.etherstubadm.deleteEtherstub(name, temporary);
            synchronized (etherstubsSet) {
                removeNoMoreExistingEtherstubMBeans(Arrays.asList(new EtherstubMBean[]{etherstubMBean}));
            }
            discover();
        } catch (EtherstubException e) {
            logger.error("Etherstub " + name + " couldn't be deleted", e);
            throw e;
        }
    }

    /**
     *  @see EtherstubManagerMBean#getEtherstubsNames() 
     */
    @Override
    public List<String> getEtherstubsNames() throws EtherstubException {
        String[] etherstubNames = this.etherstubadm.getEtherstubNames();
        if (etherstubNames == null) {
            return new LinkedList<String>();
        } else {
            return Arrays.asList(this.etherstubadm.getEtherstubNames());
        }
    }

    /**
     * @see EtherstubManagerMBean#discover()
     */
    @Override
    public void discover() throws EtherstubException {

        synchronized (etherstubsSet) {

            Set<EtherstubMBean> currentMBeans = convertToSet(this.etherstubadm.getEtherstubNames());

            //check for new Etherstubs
            for (EtherstubMBean etherstubMBean : currentMBeans) {
                if (this.etherstubsSet.contains(etherstubMBean) == false) {
                    //create and register new EtherstubMBean
                    registerNewEtherstubMBean(etherstubMBean);
                }
            }

            List<EtherstubMBean> etherstubMBeansToRemove = new LinkedList<EtherstubMBean>();
            //remove etherstubs that don't exist anymore
            for (EtherstubMBean etherstubMBean : this.etherstubsSet) {
                if (currentMBeans.contains(etherstubMBean) == false) {
                    //save this etherstub as one to be removed
                    etherstubMBeansToRemove.add(etherstubMBean);
                }
            }
            removeNoMoreExistingEtherstubMBeans(etherstubMBeansToRemove);
        }

    }

    /**
     * Sets the implementation of Etherstubadm
     * @param etherstubadm Conrete implementation of Ehterstubadm
     */
    public void setEtherstubadm(Etherstubadm etherstubadm) {
        this.etherstubadm = etherstubadm;
    }

    /**
     * Executes discover() in response to notification.
     *
     * @see  NotificationListener#handleNotification( javax.management.Notification, java.lang.Object )
     */
    @Override
    public void handleNotification(Notification ntfctn, Object o) {
        logger.debug("EtherstubManager received notification... running discovery method");
        try {
            discover();
        } catch (EtherstubException ex) {
            logger.error("Exception while discovery occured", ex);
        }
    }

    /**
     * Converts array of names to set of EtherstubMBean objets (we assume that
     * created etherstubs are persitent not temporary )
     * @param etherstubNames Array of existing etherstub names
     * @return Set of EtherstubMBean objects
     */
    private Set<EtherstubMBean> convertToSet(String[] etherstubNames) {
        Set<EtherstubMBean> set = new HashSet<EtherstubMBean>();
        if (etherstubNames != null) {
            for (String etherstubMBeanName : etherstubNames) {
                set.add(new Etherstub(etherstubMBeanName, false));
            }
        }
        return set;
    }

    /**
     * Method adds new etherstub's to the currentMBeans set (possibly created by the admin) and registers them in
     * the jmx registry
     * @param etherstubMBean New etherstub to be registered
     */
    private void registerNewEtherstubMBean(EtherstubMBean etherstubMBean) {
        if (publisher != null) {
            //register in the mbean server
            publisher.publish(etherstubMBean);
        }
        //add to the set
        this.etherstubsSet.add(etherstubMBean);
    }

    /**
     * Unregisters EtherstubMBean's from the jmx and removes from the currentMBeans set
     * @param etherstubMBeansToRemove List of unexisting EtherstubMBean's (possibly removed by the admin)
     */
    private void removeNoMoreExistingEtherstubMBeans(List<EtherstubMBean> etherstubMBeansToRemove) {

        for (EtherstubMBean etherstubMBean : etherstubMBeansToRemove) {
            try {
                if (publisher != null) {
                    this.publisher.unpublish(etherstubMBean);
                }
            } catch (NotPublishedException ex) {
                logger.error("Etherstub object : " + etherstubMBean + " has not been registered in the mbean server");
            }
            this.etherstubsSet.remove(etherstubMBean);
        }
    }

    /**
     * Injects the publisher object, used to publish new etherstubs and removes unexisting ones
     * @param publisher Instance of Publisher to be used by EtherstubManagerMBean
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
