package agh.msc.xbowbase.lib;

import com.sun.jna.Library;
import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.EtherstubException;

/**
 * Etherstub helper interface
 * Used to manage etherstub's
 * 
 * @author robert boczek
 */
public interface EtherstubHelper extends Library {

    /**
     * Removes etherstub with specified name
     *
     * @param name Name of etherstub
     * @param temporary Flag specifies whether etherstub should be removed temporary or persistently
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException;

    /**
     * Creates etherstub with specified name
     *
     * @param name Name of etherstub
     * @param temporary Flag specifies whether etherstub should be created temporary or persistent
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public void createEtherstub(String name, boolean temporary) throws EtherstubException;

    /**
     * Return list of existing etherstubs
     *
     * @return Array of names of existing etherstubs (empty array when there's no etherstub's existing)
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public String[] getEtherstubNames() throws EtherstubException;

    /**
     * Returns value of requested parameter
     *
     * @param name Name of etherstub
     * @param parameter Type of requested property
     * @return Value of requested parameter
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubParameter(String name, LinkParameters parameter) throws EtherstubException;

    /**
     * Returns value of requested parameter
     *
     * @param name Name of etherstub
     * @param property Type of requested property
     * @return Value of requested statistic
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubStatistic(String name, LinkStatistics property) throws EtherstubException;

    /**
     * Sets new value to property 'property' to the etherstub whose name is 'name'
     *
     * @param name Name of etherstub
     * @param property Type of property to be set
     * @param value Value to be set
     * @throws EtherstubException
     */
    public void setEtherstubProperty(String name, LinkProperties property, String value) throws EtherstubException;

    /**
     * Returns value of requested property
     *
     * @param name Name of etherstub
     * @param property Type of requested property
     * @return Value of requested property
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubProperty(String name, LinkProperties property) throws EtherstubException;
}
