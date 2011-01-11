package org.jims.modules.crossbow.zones;

/**
 * MBean responsible for copying zone
 * from NFS server locally mounted to
 * local file system
 *
 * @author robert boczek
 */
public interface ZoneCopierMBean {

    /**
     * @brief Copies zone file from 'fromLocation' to 'toLocation'
     * 
     * @param fromLocation Location of file to be copied
     * @param toLocation Location of file where it should be copied
     * 
     * @return Returns true if file was successfully copied or false otherwise
     */
    public boolean copyZone(String fromLocation, String toLocation);
}
