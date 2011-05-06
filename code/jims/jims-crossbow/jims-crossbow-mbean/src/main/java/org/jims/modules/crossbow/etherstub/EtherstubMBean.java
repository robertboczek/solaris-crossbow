package org.jims.modules.crossbow.etherstub;

import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.EtherstubException;
import java.util.Map;
import java.util.List;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;

/**
 * @brief Defines possible operations for Etherstubs
 * Interface EtherstubMBean describes all possible operations
 *
 *
 * @author robert boczekk
 */
public interface EtherstubMBean {

    /**
     * @brief Gets etherstub name
     * Returns etherstub name
     *
     * @return Etherstub name
     */
    public String getName();

    /**
     * @brief Returns temporary attribute state
     * Speifies whether etherstub is temporary or persistent. Persistent means that
     * etherstub exists between reboots
     *
     * @return Flag whether etherstub is temporary
     */
    public boolean isTemporary();

    /**
     * @brief Map of etherstubs' properties
     * Returns map of all existing properties assigned to this etherstub
     *
     * @return Map of properties or empty map when no properties were assigned to this etherstub
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<LinkProperties, String> getProperties() throws EtherstubException;

    /**
     * @brief Map of etherstubs' parameters
     * Returns map of all existing parameters assigned to this etherstub
     *
     * @return Map of parameters or empty map when no parameters were assigned to this etherstub
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<LinkParameters, String> getParameters() throws EtherstubException;

    /**
     * @brief Map of etherstubs' statistics
     * Return map of all existing statistics assigned to this etherstub
     *
     * @return Map of statistics or empty map when no statistics couldn't be read
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<LinkStatistics, String> getStatistics() throws EtherstubException;

    /**
     * @brief Sets new property value
     * Sets requested property value specified by the user
     *
     * @param etherstubProperty Type of property to be set
     * @param value Requested value
     * @throws XbowException Exeption thrown in case of error
     */
    public void setProperty(LinkProperties etherstubProperty, String value) throws EtherstubException;

    /**
     * @brief Gets lsit containing map with statistics for specified time period
     *
     * @param period Time period type
     * @return Returns list containing map with statistics for specified time period
     */
    public List<Map<LinkStatistics, Long>> getStatistics(LinkStatisticTimePeriod period);


    /*
     * JConsole only
     */
    public Map<String, String> get_Properties() throws EtherstubException;

    public Map<String, String> get_Parameters() throws EtherstubException;

    public void set_Property(String property, String value) throws EtherstubException;

    public Map<String, String> get_Statistics2() throws EtherstubException;
}
