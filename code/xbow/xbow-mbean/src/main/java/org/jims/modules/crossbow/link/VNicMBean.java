package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.exception.LinkException;

/**
 * Interface VNicMBean describes all possible operations
 * @author robert boczekk
 */

public interface VNicMBean extends LinkMBean{

    /**
     * Speifies whether vnic is temporary or persistent. Persistent means that
     * vnic exists between reboots
     * @return Flag whether vnic is temporary
     */
    public boolean isTemporary();

    /**
     * Specifies whether this vnic runs under Etherstub or real NIC
     * @return Information about vnic's parent type
     * @throws LinkException Exception thrown in case of errors
     */
    public boolean isEtherstubParent() throws LinkException;

    /**
     * Returns name of parent for this vnic
     * @return Name of parent for this vnic
     * @throws LinkException Exception thrown in case of errors
     */
    public String getParent()  throws LinkException;

}
