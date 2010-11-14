package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.link.VNicMBean;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/**
 * @brief Subclass of MBeanPublisher, deals with VNicMBeanPublisher
 * Publishes new VNicbMBean object in the MBeanServer and removes ones that do not exist anymore
 * 
 * @author robert boczek
 */
public class VNicMBeanPublisher extends MBeanPublisher {

    private static final Logger logger = Logger.getLogger(VNicMBeanPublisher.class);

    /**
     * @brief Constructor of VNicMBeanPublisher class
     * Constructor of VNicMBeanPublisher. Sets mbeanServer property and creates empty published list
     * 
     * @param mBeanServer Instance of MBean server to be used to register and unregister MBeans
     */
    public VNicMBeanPublisher(MBeanServer mBeanServer) {

        super(mBeanServer);
    }

    /**
     * @brief Creates VNicMBean object's name
     * Creates vnic's object name
     *
     * @param VNicMBean VNic MBean object whose object name is requested
     * @return VNic's object name
     * @throws MalformedObjectNameException

     */
    @Override
    protected ObjectName createObjectName(Object object) throws MalformedObjectNameException {

			VNicMBean vnic = ( VNicMBean ) object;

			String parent = "unknown";
			try {
				parent = vnic.getParent();
			} catch ( LinkException e ) {
			}

			return new ObjectName(String.format(
				"Crossbow:type=VNic,parent=%s,name=%s", parent, vnic.getName()
			));

    }

    /**
     * @see MBeanPublisher#identifies(java.lang.Object, java.lang.Object)
     */
    @Override
    protected boolean identifies(Object id, Object o) {
        return id.equals(o);
    }
}
