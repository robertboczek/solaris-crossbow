package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import java.util.Map;

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
}
