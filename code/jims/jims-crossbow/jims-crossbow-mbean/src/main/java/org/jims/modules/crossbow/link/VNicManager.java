package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.exception.InvalidLinkNameException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.lib.VNicHelper;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.manager.BaseManager;


/**
 * Implementation of VNicManagerMBean, @see VNicManagerMBean
 *
 * @author robert boczek
 */
public class VNicManager extends BaseManager implements VNicManagerMBean, NotificationListener {

    /** Logger */
    private static final Logger logger = Logger.getLogger(VNicManager.class);
    private Publisher publisher;
    private VNicHelper vnicHelper;

    /**
     * Executes discover() in response to notification.
     *
     * @see  NotificationListener#handleNotification( javax.management.Notification, java.lang.Object )
     */
    @Override
    public void handleNotification(Notification notification, Object handback) {

        logger.debug("VNicManager received notification... running discovery method");

        try {
            discover();
        } catch (LinkException ex) {
            logger.error("Error while running discovery method", ex);
        }
    }

    /**
     * @see  VNicManagerMBean#create(agh.msc.xbowbase.link.VNicMBean)
     */
    @Override
    public void create(VNicMBean vNicMBean) throws LinkException {

        logger.debug("VNicManager creating new vnic with name: " + vNicMBean.getName() + ", temporary: " + vNicMBean.isTemporary() + ", under: " + vNicMBean.getParent());

        try {
            this.vnicHelper.createVNic(vNicMBean.getName(), vNicMBean.isTemporary(), vNicMBean.getParent());

						// TODO: this is temporary. investigate (vnicHelper was not injected)
						( ( VNic ) vNicMBean ).setVNicHelper( vnicHelper );
            registerNewVNicMBean(vNicMBean);
            discover();

        } catch (InvalidLinkNameException ex) {
            logger.error("VNic " + vNicMBean + " couldn't be created", ex);
            throw ex;
        } catch (LinkException e) {
            logger.error("VNic " + vNicMBean + " couldn't be created", e);
            throw e;
        }

    }

    /**
     * @see  VNicManagerMBean#delete(java.lang.String, boolean)
     */
    @Override
    public void delete(String name, boolean temporary) throws LinkException {

        logger.debug("VNicManager removing vnic with name: " + name + ", temporary: " + temporary);

        try {
            VNicMBean vnicMBean = new VNic(name, temporary, null);
            this.vnicHelper.deleteVNic(name, temporary);
            removeNoMoreExistingVNicMBeans(Arrays.asList(new VNicMBean[]{vnicMBean}));
            discover();

        } catch (InvalidLinkNameException ex) {
            logger.error("VNic " + name + " couldn't be created", ex);
            throw ex;
        } catch (LinkException e) {
            logger.error("VNic " + name + " couldn't be deleted", e);
            throw e;
        }
    }

    /**
     * @see  VNicManagerMBean#getVNicsNames()
     */
    @Override
    public List<String> getVNicsNames() throws LinkException {

        return Arrays.asList(this.vnicHelper.getLinkNames(true));
    }


		/**
		 * @see VNicManagerMBean#getByName(java.lang.String)
		 */
		@Override
		public VNicMBean getByName( String name ) throws LinkException {

			if ( null != publisher ) {

				for ( Object o : publisher.getPublished() ) {

					VNicMBean vNicMBean = ( VNicMBean ) o;

					if ( name.equals( vNicMBean.getName() ) ) {
						return vNicMBean;
					}

				}

				throw new LinkException( "No VNIC named " + name + "found." );

			}

			throw new LinkException( "No publisher set." );

		}


    /**
     * @see  VNicManagerMBean#discover()
     */
    @Override
    public void discover() throws LinkException {
        logger.debug("Searching for new vnic's and ones that don't exist any more");

        Set<VNicMBean> currentMBeans = convertToSet(vnicHelper.getLinkNames(true));

        if (publisher != null) {
            Set<Object> vnicSet = new HashSet<Object>(publisher.getPublished());

            //check for new vnic's
            for (VNicMBean vNicMBean : currentMBeans) {
                if (vnicSet.contains(vNicMBean) == false) {
                    //create and register new VnicMBean
                    ( ( VNic ) vNicMBean ).setVNicHelper( vnicHelper );
                    registerNewVNicMBean(vNicMBean);
                }
            }

            List<VNicMBean> vNicMBeansToRemove = new LinkedList<VNicMBean>();
            //remove etherstubs that don't exist anymore
            for (Object object : vnicSet) {
                if (object instanceof VNicMBean && currentMBeans.contains((VNicMBean) object) == false) {
                    //save this etherstub as one to be removed
                    vNicMBeansToRemove.add(((VNicMBean) object));
                }
            }
            removeNoMoreExistingVNicMBeans(vNicMBeansToRemove);
        }
    }

    /**
     * Sets publisher instance
     *
     * @param publisher Instance of publisher to be used for publishing MBeans
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Registers  new VNicMBean to MBeanServer
     *
     * @param vNicMBean New VNicMBean to be registered to MBeanServer
     */
    private void registerNewVNicMBean(VNicMBean vNicMBean) {

        if (publisher != null) {

            logger.debug("Registering new VNicMBean to MBeanServer: " + vNicMBean);
            publisher.publish(vNicMBean);
        }
    }

    /**
     * Removes VNicMBeans that don't exist in the system from the MBeanServer
     *
     * @param vNicMBeansList List of VNicMBean to unregister
     */
    private void removeNoMoreExistingVNicMBeans(List<VNicMBean> vNicMBeansList) {

        for (VNicMBean vNicMBean : vNicMBeansList) {
            try {
                if (publisher != null) {
                    logger.debug("Unregistering VNicMBean from the MBeanServer: " + vNicMBean);
                    this.publisher.unpublish(vNicMBean);
                }
            } catch (NotPublishedException ex) {
                logger.error("VNicMBean object : " + vNicMBean + " has not been registered in the mbean server");
            }
        }
    }

    /**
     * Converts array of names to set of VNicMBean objets (we assume that
     * created VNic's are persitent not temporary and parent is null )
     *
     * @param vNicNames Array of existing VNicMBean names
     * @return Set of EtherstubMBean objects
     */
    private Set<VNicMBean> convertToSet(String[] vNicNames) {
        Set<VNicMBean> set = new HashSet<VNicMBean>();
        if (vNicNames != null) {
            for (String vnicMBeanName : vNicNames) {
                set.add(new VNic(vnicMBeanName, false, null));
            }
        }
        return set;
    }

    public void setVNicHelper(VNicHelper linkHelper) {

        this.vnicHelper = linkHelper;
    }


		/*
		 * jconsole only
		 */
		@Override
    public void _create( String name, boolean temporary, String parent ) throws LinkException {
			create( new VNic( name, temporary, parent ) );
		}
}
