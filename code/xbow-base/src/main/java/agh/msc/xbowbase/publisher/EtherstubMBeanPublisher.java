package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.etherstub.EtherstubMBean;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.LinkedList;
import java.util.List;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;

import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * Publishes new EtherstubMBean object in the MBeanServer and removes ones that do not exist anymore
 * @author robert boczek
 */
public class EtherstubMBeanPublisher implements Publisher{

    private static final Logger logger = Logger.getLogger(EtherstubMBeanPublisher.class);

    private MBeanServer mbeanServer;

    private List<EtherstubMBean> published;

    public EtherstubMBeanPublisher( MBeanServer mBeanServer){

        this.mbeanServer = mBeanServer;

        this.published = new LinkedList<EtherstubMBean>();
    }
    

    /**
     * Publishes new etherstub object in the MBeanServer
     * @param object New EtherstubMBean object to be registered
     */
    @Override
    public void publish(Object object) {

        if(object instanceof EtherstubMBean){

            EtherstubMBean etherstubMBean = (EtherstubMBean)object;

            if(etherstubMBean == null || etherstubMBean.getName() == null){

                logger.error("Couldn't register register new etherstub. Object is null or its name is null");
                return;
            }

            if(published.contains(etherstubMBean)){
                logger.warn("Etherstub object has been already registered");
            }
            else{
                try {
                    mbeanServer.registerMBean(etherstubMBean, getObjectName(etherstubMBean));
                    published.add(etherstubMBean);

                    logger.info("Etherstub's object " + getObjectName(etherstubMBean) + "has been successfully registered in the MBeanServer");

                } catch (MalformedObjectNameException ex) {
                    logger.error("Exeption occured while creating object name");
                } catch (Exception ex) {
                    logger.error("Couldn't register etherstub " + etherstubMBean.getName(), ex);
                }

           }
                        
        }else{
            logger.error("Provided object to be registered in the MBeanServer was not an EtherstubMBean object");
        }
    }

    /**
     * Unregisters etherstub that do not exist anymore in the system
     * @param object EtherstubMBean object to be unregistered
     * @throws NotPublishedException Exception thrown when specified EtherstubMBean wasn't registered
     */
    @Override
    public void unpublish(Object object) throws NotPublishedException {

        if(object instanceof EtherstubMBean){

            EtherstubMBean etherstubMBean = (EtherstubMBean)object;

            if(etherstubMBean == null || etherstubMBean.getName() == null){
                logger.error("Couldn't unregister etherstub. Object is null or its  name attribute is null");
                return;
            }

            if(published.contains(etherstubMBean)){
                try {

                    mbeanServer.unregisterMBean(getObjectName(etherstubMBean));
                    this.published.remove(etherstubMBean);

                    logger.info("Etherstub's object " + getObjectName(etherstubMBean) + " has been successfully unregistered in the MBeanServer");

                } catch (MalformedObjectNameException ex) {
                    logger.error("Exeption occured while creating object name", ex);
                } catch (Exception ex) {
                    logger.error("Couldn't unregister etherstub " + etherstubMBean.getName(), ex);
                }
            }else{

                throw new NotPublishedException("Etherstub " + etherstubMBean + " has not been published");

            }
        }else{
            logger.error("Provided object to be unregistered in the MBeanServer was not an EtherstubMBean object");
        }
    }

		@Override
		public List< Object > getPublished() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

    /**
     * Creates etherstub's object name
     * @param etherstubMBean Ethertstub MBean object whose object name is requested
     * @return Etherstub's object name
     * @throws MalformedObjectNameException
     */
    private ObjectName getObjectName(EtherstubMBean etherstubMBean) throws MalformedObjectNameException{

        return new ObjectName( String.format(
			"agh.msc.xbowbase:type=Etherstub,name=%s",
			etherstubMBean.getName() )
                      );
    }
}
