package agh.msc.xbowbase.lib;

import com.sun.jna.Library;
import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;

/**
 * Etherstub helper interface
 * Used to manage etherstub's
 * @author robert boczek
 */
public interface Etherstubadm extends Library {

    /**
     *  Removes etherstub with specified name
     * @param name Name of etherstub
     * @param temporary Flag specifies whether etherstub should be removed temporary or persistently
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException;

    /**
     *  Creates etherstub with specified name
     * @param name Name of etherstub
     * @param temporary Flag specifies whether etherstub should be created temporary or persistent
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public void createEtherstub(String name, boolean temporary) throws EtherstubException;

    /**
     * Return list of existing etherstubs
     * @return Array of names of existing etherstubs
     * @throws EtherstubException Exception thrown when error while reading names occur
     */
    public String[] getEtherstubNames() throws EtherstubException;

    /**
     * Returns value of requested parameter
     * @param name Name of etherstub
     * @param parameter Type of requested property
     * @return Value of requested parameter
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubParameter(String name, EtherstubParameters parameter) throws EtherstubException;

    /**
     * Returns value of requested parameter
     * @param name Name of etherstub
     * @param property Type of requested property
     * @return Value of requested statistic
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubStatistic(String name, EtherstubStatistics property) throws EtherstubException;

    /**
     * Sets new value to property 'property' to the etherstub whose name is 'name'
     * @param name Name of etherstub
     * @param property Type of property to be set
     * @param value Value to be set
     * @throws EtherstubException
     */
    public void setEtherstubProperty(String name, EtherstubProperties property, String value) throws EtherstubException;

    /**
     * Returns value of requested property
     * @param name Name of etherstub
     * @param property Type of requested property
     * @return Value of requested
     * @throws EtherstubException Exception thrown when error while reading value occur
     */
    public String getEtherstubProperty(String name, EtherstubProperties property) throws EtherstubException;
}
