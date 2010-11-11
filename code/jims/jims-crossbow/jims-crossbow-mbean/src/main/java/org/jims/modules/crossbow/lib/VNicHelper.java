package org.jims.modules.crossbow.lib;

import org.jims.modules.crossbow.exception.LinkException;

/**
 * VNic helper interface.
 * 
 * Used to query and manage VNics.
 *
 * @author robert boczek
 */
public interface VNicHelper extends LinkHelper {

    /**
     * Removes vnic with specified name
     *
     * @param name Name of vnic
     * @param temporary Flag specifies whether vnic should be removed temporary or persistently
     * @throws LinkException Exception thrown when error while reading names occur
     */
    public void deleteVNic(String name, boolean temporary) throws LinkException;

    /**
     * Creates vnic with specified name
     *
     * @param name VNic name
     * @param temporary Flag specifies whether vnic should be created temporary or persistent
     * @param parent Parent link name
     * @throws LinkException Exception thrown when error while reading names occur
     */
    public void createVNic(String name, boolean temporary, String parent) throws LinkException;
}
