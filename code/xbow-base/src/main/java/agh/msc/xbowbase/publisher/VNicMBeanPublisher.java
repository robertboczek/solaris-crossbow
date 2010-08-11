package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.link.VNicMBean;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * Publishes new VNicbMBean object in the MBeanServer and removes ones that do not exist anymore
 * @author robert boczek
 */
public class VNicMBeanPublisher implements Publisher {

    private static final Logger logger = Logger.getLogger(VNicMBeanPublisher.class);
    private final MBeanServer mBeanServer;
    private final List<Object> published;

    /**
     * Constructor of VNicMBeanPublisher. Sets mbeanServer property and creates empty published list
     * @param mBeanServer Instance of MBean server to be used to register and unregister MBeans
     */
    public VNicMBeanPublisher(MBeanServer mBeanServer) {

        this.mBeanServer = mBeanServer;
        this.published = new LinkedList<Object>();
    }

    /**
     * @see Publisher#publish(java.lang.Object) 
     */
    @Override
    public void publish(Object object) {

        if (object instanceof VNicMBean) {

            VNicMBean vNicMBean = (VNicMBean) object;

            if (vNicMBean == null || vNicMBean.getName() == null) {

                logger.error("Couldn't register register new vnic. Object is null or its name is null");
                return;
            }

            if (published.contains(vNicMBean)) {
                logger.warn("VNic object has been already registered");
            } else {
                try {
                    mBeanServer.registerMBean(vNicMBean, getObjectName(vNicMBean));
                    synchronized (this.published) {
                        published.add(vNicMBean);
                    }

                    logger.info("VNic's object " + getObjectName(vNicMBean) + "has been successfully registered in the MBeanServer");

                } catch (MalformedObjectNameException ex) {
                    logger.error("Exeption occured while creating object name");
                } catch (Exception ex) {
                    logger.error("Couldn't register vnic " + vNicMBean.getName(), ex);
                }

            }

        } else {
            logger.error("Provided object to be registered in the MBeanServer was not an VNicMBean object");
        }
    }

    /**
     * @see Publisher#unpublish(java.lang.Object) 
     */
    @Override
    public void unpublish(Object object) throws NotPublishedException {

        if (object instanceof VNicMBean) {

            VNicMBean vNicMBean = (VNicMBean) object;

            if (vNicMBean == null || vNicMBean.getName() == null) {
                logger.error("Couldn't unregister vnic. Object is null or its  name attribute is null");
                return;
            }

            if (published.contains(vNicMBean)) {
                try {

                    mBeanServer.unregisterMBean(getObjectName(vNicMBean));
                    synchronized (this.published) {
                        this.published.remove(vNicMBean);
                    }

                    logger.info("VNic's object " + getObjectName(vNicMBean) + " has been successfully unregistered in the MBeanServer");

                } catch (MalformedObjectNameException ex) {
                    logger.error("Exeption occured while creating object name", ex);
                } catch (Exception ex) {
                    logger.error("Couldn't unregister vnic " + vNicMBean.getName(), ex);
                }
            } else {

                throw new NotPublishedException("VNic " + vNicMBean + " has not been published");

            }
        } else {
            logger.error("Provided object to be unregistered in the MBeanServer was not an VNicMBean object");
        }

    }

    /**
     * @see Publisher#getPublished() 
     */
    @Override
    public List<Object> getPublished() {
        return this.published;
    }

    /**
     * Creates vnic's object name
     * @param VNicMBean VNic MBean object whose object name is requested
     * @return VNic's object name
     * @throws MalformedObjectNameException
     */
    private ObjectName getObjectName(VNicMBean vNicMBean) throws MalformedObjectNameException {

        return new ObjectName(String.format(
                "agh.msc.xbowbase:type=VNic,name=%s",
                vNicMBean.getName()));
    }
}
