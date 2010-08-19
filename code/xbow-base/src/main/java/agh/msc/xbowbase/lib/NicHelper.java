package agh.msc.xbowbase.lib;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.link.NicInfo;
import java.util.List;

/**
 * Link helper interface.
 *
 * Used to manage and query links.
 *
 * @author cieplik
 */
public interface NicHelper {

    /**
     * Retrieves NicInfo objects for all links in the system.
     *
     * @return  list of NicInfo objects
     */
    public List<NicInfo> getNicsInfo();

    /**
     * Returns true iff the NIC is up.
     *
     * @param  name  NIC name
     *
     * @return  true  iff the link is up
     */
    public boolean isUp(String name);

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

    /**
     * Return list of existing etherstubs
     *
     * @param isVNic If true returns list of vnic's, if false returns list of nic's
     * @return Array of names of existing etherstubs (empty array when there's no etherstub's existing)
     * @throws LinkException Exception thrown when error while reading names occur
     */
    public String[] getLinkNames(boolean isVNic) throws LinkException;

    /**
     * Returns value of requested parameter
     *
     * @param name Name of link
     * @param parameter Type of requested property
     * @return Value of requested parameter
     * @throws LinkException Exception thrown when error while reading value occur
     */
    public String getLinkParameter(String name, LinkParameters parameter) throws LinkException;

    /**
     * Returns value of requested parameter
     *
     * @param name Name of link
     * @param property Type of requested property
     * @return Value of requested statistic
     * @throws LinkException Exception thrown when error while reading value occur
     */
    public String getLinkStatistic(String name, LinkStatistics property) throws LinkException;

    /**
     * Sets new value to property 'property' to the link whose name is 'name'
     *
     * @param name Name of link
     * @param property Type of property to be set
     * @param value Value to be set
     * @throws LinkException Exception thrown when error while setting value occur
     */
    public void setLinkProperty(String name, LinkProperties property, String value) throws LinkException;

    /**
     * Returns value of requested property
     *
     * @param name Name of link
     * @param property Type of requested property
     * @return Value of requested property
     * @throws LinkException Exception thrown when error while reading value occur
     */
    public String getLinkProperty(String name, LinkProperties property) throws LinkException;
}
