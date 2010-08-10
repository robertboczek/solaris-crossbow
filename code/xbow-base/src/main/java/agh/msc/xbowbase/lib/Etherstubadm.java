package agh.msc.xbowbase.lib;

import com.sun.jna.Library;
import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.XbowException;

/**
 *
 * @author robert boczek
 */
public interface Etherstubadm extends Library {

    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException;

    public void createEtherstub(String name, boolean temporary) throws EtherstubException;

    public String[] getEtherstubNames() throws EtherstubException;

    public String getEtherstubParameter(String name, EtherstubParameters parameter) throws EtherstubException;

    public String getEtherstubStatistic(String name, EtherstubStatistics property) throws EtherstubException;

    public void setEtherstubProperty(String name, EtherstubProperties property, String value) throws EtherstubException;

    public String getEtherstubProperty(String name, EtherstubProperties property) throws EtherstubException;
}
