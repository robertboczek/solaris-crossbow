package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import java.util.Map;

/**
 * Interface EtherstubMBean describes all possible operations
 * @author robert boczekk
 */
public interface EtherstubMBean {

    /**
     * Returns etherstub name
     * @return Etherstub name
     */
    public String getName();

    /**
     * Speifies whether etherstub is temporary or persistent. Persistent means that
     * etherstub exists between reboots
     * @return Flag whether etherstub is temporary
     */
    public boolean isTemporary();

    /**
     * Returns map of all existing properties assigned to this etherstub
     * @return Map of properties or empty map when no properties were assigned to this etherstub
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<EtherstubProperties, String> getProperties() throws EtherstubException;

    /**
     * Returns map of all existing parameters assigned to this etherstub
     * @return Map of parameters or empty map when no parameters were assigned to this etherstub
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<EtherstubParameters, String> getParameters() throws EtherstubException;

    /**
     * Return map of all existing statistics assigned to this etherstub
     * @return Map of statistics or empty map when no statistics couldn't be read
     * @throws EtherstubException Exeption thrown in case of errors
     */
    public Map<EtherstubStatistics, String> getStatistics() throws EtherstubException;
}
