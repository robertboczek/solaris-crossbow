package agh.msc.xbowbase.lib;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;

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


		public void plumb( String name );

		public void setNetmask( String name, String mask );

		public String getNetmask( String name );

}
