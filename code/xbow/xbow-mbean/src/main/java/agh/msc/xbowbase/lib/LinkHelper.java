package agh.msc.xbowbase.lib;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;

/**
 * Link helper interface.
 *
 * Used to query and manage links.
 *
 * @author robert boczek
 */
public interface LinkHelper {

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

    /**
     * @brief  Performs plumbing needed for IP to use link.
     *
     * @param  link  link name
     *
     * // TODO-DAWID: exceptions
     */
    public void plumb(String link);

		/**
		 * TODO-DAWID: comments
		 *
		 *
		 * @param link
		 * @return
		 */
		public boolean isPlumbed( String link );

    /**
     * @brief  For given interface, sets its netmask.
     *
     * @param  name  the name of the link
     * @param  mask  netmask (string form - e.g. 255.255.255.0) to be set
     *
     * @throws  ValidationException  the mask has invalid format
     * @throws  LinkException        internal exception
     */
    public void setNetmask(String name, String mask) throws ValidationException,
            LinkException;

    /**
     * @brief  Retrieves netmask for link.
     *
     * @param  link  link name
     *
     * @return  string representation of netmask set for link
     */
    public String getNetmask(String link);

    /**
     * Returns ip address assigned to this link
     *
     * @param link Name of the link
     * @return Ip address or null if no address is assigned to this link
     * @throws LinkException Exception thrown when errors while getting ip address for this link occur
     */
    public String getIpAddress(String link) throws LinkException;

    /**
     * Sets new ip address to link
     *
     * @param link Name of the link
     * @param ipAddress New ipv4 address( for example: '192.168.0.123' )
     * @throws LinkException Exception thrown when error with setting ip address occur
     */
    public void setIpAddress(String link, String ipAddress) throws LinkException, ValidationException;
}
