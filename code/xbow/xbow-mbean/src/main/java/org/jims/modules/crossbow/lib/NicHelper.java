package org.jims.modules.crossbow.lib;

import org.jims.modules.crossbow.link.NicInfo;
import java.util.List;

/**
 * Nic helper interface.
 *
 * Used to manage and query nics.
 *
 * @author cieplik
 */
public interface NicHelper extends LinkHelper{

    /**
     * Retrieves NicInfo objects for all links in the system.
     *
     * @return  list of NicInfo objects
     */
    public List<NicInfo> getNicsInfo();

    /**
     * Returns true if the NIC is up.
     *
     * @param  name  NIC name
     *
     * @return  true  iff the link is up
     */
    public boolean isUp(String name);
    
}
