package org.jims.modules.crossbow.etherstub;

import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.exception.InvalidEtherstubNameException;
import org.jims.modules.crossbow.exception.TooLongEtherstubNameException;
import org.jims.modules.crossbow.lib.EtherstubHelper;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import javax.management.Notification;
import javax.management.NotificationListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.jna.JNAEtherstubHelper;
import org.jims.modules.crossbow.manager.BaseManager;
import org.jims.modules.crossbow.publisher.EtherstubMBeanPublisher;

/**
 * @brief Implements EtherstubManagerMBean interface
 * Implementation of EtherstubManagerMBean
 * 
 * @see EtherstubManagerMBean
 * @author robert boczek
 */
public class EtherstubManager extends BaseManager implements EtherstubManagerMBean, NotificationListener {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Etherstub.class);
    private EtherstubHelper etherstubHelper;
    private Publisher publisher;

    /**
     * @see EtherstubManagerMBean#create(agh.msc.xbowbase.etherstub.EtherstubMBean) 
     */
    @Override
    public void create(EtherstubMBean etherstubMBean) throws EtherstubException {

        try {
            this.etherstubHelper
                    .createEtherstub(etherstubMBean.getName(), etherstubMBean.isTemporary());
            discover();

        } catch (TooLongEtherstubNameException e2) {
            logger.error("Etherstub's name: " + etherstubMBean + " was too long", e2);
            throw e2;

        } catch (InvalidEtherstubNameException ex) {
            logger.error("Etherstub's name: " + etherstubMBean + " was incorrect", ex);
            throw ex;

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
            this.etherstubHelper.deleteEtherstub(name, temporary);
            removeNoMoreExistingEtherstubMBeans(Arrays.asList(new EtherstubMBean[]{etherstubMBean}));
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

        return Arrays.asList(this.etherstubHelper.getEtherstubNames());
    }

    /**
     * @see EtherstubManagerMBean#discover()
     */
    @Override
    public void discover() throws EtherstubException {
        logger.debug("Searching for new etherstubs's and ones that don't exist any more");

        
        if(publisher != null){
            Set<EtherstubMBean> currentMBeans = convertToSet(this.etherstubHelper.getEtherstubNames());
            Set<Object> etherstubsSet = new HashSet<Object>(publisher.getPublished());

            //check for new Etherstubs
            for (EtherstubMBean etherstubMBean : currentMBeans) {
                if ( ! etherstubsSet.contains(etherstubMBean) ) {
                    //create and register new EtherstubMBean
									( ( Etherstub ) etherstubMBean ).setEtherstubHelper( etherstubHelper );  // TODO: < this is temporary. investigate
                    registerNewEtherstubMBean(etherstubMBean);
                }
            }

            List<EtherstubMBean> etherstubMBeansToRemove = new LinkedList<EtherstubMBean>();
            //remove etherstubs that don't exist anymore
            for (Object object : etherstubsSet) {
                if (currentMBeans.contains((EtherstubMBean)object) == false) {
                    //save this etherstub as one to be removed
                    etherstubMBeansToRemove.add((EtherstubMBean)object);
                }
            }
            removeNoMoreExistingEtherstubMBeans(etherstubMBeansToRemove);
        }

    }

    /**
     * @brief Injects EtherstubHelper instance
     * Sets the implementation of EtherstubHelper
     *
     * @param etherstubHelper Conrete implementation of Ehterstubadm
     */
    public void setEtherstHelper(EtherstubHelper etherstubHelper) {
        this.etherstubHelper = etherstubHelper;
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
     * @brief Converts array of Etherstub's names to set of EthertsubMBean objects
     * Converts array of names to set of EtherstubMBean objets (we assume that
     * created etherstubs are persitent not temporary )
     *
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
     * @brief Registers new EtherstubMBean in the MBeanServer
     * Method adds new etherstub's to the currentMBeans set (possibly created by the admin) and registers them in
     * the jmx registry
     *
     * @param etherstubMBean New etherstub to be registered
     */
    private void registerNewEtherstubMBean(EtherstubMBean etherstubMBean) {
        
        if (publisher != null) {
            //register in the mbean server
            publisher.publish(etherstubMBean);
        }
    }

    /**
     * @brief Unregisters EtherstubMBean from the MBeanServer
     * Unregisters EtherstubMBean's from the jmx and removes from the currentMBeans set
     *
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
        }
    }

    /**
     * @brief Injects Publisher class instance
     * Injects the publisher object, used to publish new etherstubs and removes unexisting ones
     *
     * @param publisher Instance of Publisher to be used by EtherstubManagerMBean
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }


		/*
		 * jconsole only
		 */

		@Override
		public void _create( String name, boolean temporary ) throws EtherstubException {
			create( new Etherstub( name, temporary ) );
		}
}
