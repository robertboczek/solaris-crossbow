package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import java.util.Map;

/**
 *
 * @author robert boczekk
 */
public interface EtherstubMBean {

    public String getName();

    public boolean isTemporary();

    public Map<EtherstubProperties, String> getProperties() throws EtherstubException;

    public Map<EtherstubParameters, String> getParameters() throws EtherstubException;

    public Map<EtherstubStatistics, String> getStatistics() throws EtherstubException;
}
