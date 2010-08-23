package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.etherstub.EtherstubMBean;
import javax.management.MBeanServer;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * @brief Subclass of MBean publisher, deals with EtherstubMBean's
 * Publishes new EtherstubMBean object in the MBeanServer and removes ones that do not exist anymore
 *
 * @author robert boczek
 */
public class EtherstubMBeanPublisher extends MBeanPublisher {

    private static final Logger logger = Logger.getLogger(EtherstubMBeanPublisher.class);

    /**
     * @brief Construcotr of EtherstubMBean class
     * Constructor of EtherstubMBeanPublisher. Sets mbeanServer property and creates empty published list
     *
     * @param mBeanServer Instance of MBean server to be used to register and unregister MBeans
     */
    public EtherstubMBeanPublisher(MBeanServer mBeanServer) {

        super(mBeanServer);
    }

    /**
     * @brief Creates etherstub's specific object name
     * Creates etherstub's object name
     * 
     * @param etherstubMBean Ethertstub MBean object whose object name is requested
     * @return Etherstub's object name
     * @throws MalformedObjectNameException
    */
    @Override
    protected ObjectName createObjectName(Object object) throws MalformedObjectNameException {
        
        return new ObjectName(String.format(
                "agh.msc.xbowbase:type=Etherstub,name=%s",
                ((EtherstubMBean) object).getName()));
    }

    /**
     * @see MBeanPublisher#identifies(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean identifies(Object id, Object o) {
        return id.equals(o);
    }
}
