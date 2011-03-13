package org.jims.modules.crossbow.manager;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import org.apache.log4j.Logger;

/**
 * Base abstract class for whole crossbow managers
 *
 * @author robert boczek
 */
public abstract class BaseManager {

    protected MBeanServer server;

    private static final Logger logger = Logger.getLogger(BaseManager.class);

    /**
     * Default constructor searches for jims MBean server
     */
    public BaseManager() {

        server = MBeanServerFactory.newMBeanServer();

        // get the JIMS MBean server
        /*ArrayList servers = MBeanServerFactory.findMBeanServer(null);
        Iterator it = servers.iterator();

        while (it.hasNext()) {
            MBeanServer testedServer = (MBeanServer) it.next();
            try {
                if (testedServer.isRegistered(new ObjectName("Information:class=OSCommon"))) {
                    this.server = testedServer;
                    break;
                }
            } catch (Exception ex) {
                logger.error("Error occured: "+ex.getMessage());
                ex.printStackTrace();
            }
        }
        if (this.server == null) {
            return;
        }*/
        
    }

}
