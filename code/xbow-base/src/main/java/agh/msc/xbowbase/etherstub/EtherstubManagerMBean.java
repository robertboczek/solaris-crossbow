package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.exception.EtherstubException;
import java.util.List;

/**
 *
 * @author robert boczek
 */
public interface EtherstubManagerMBean {

    void create(EtherstubMBean etherstubMBean) throws EtherstubException;

    void delete(String name, boolean temporary) throws EtherstubException;

    List<String> getEtherstubsNames() throws EtherstubException;

    /**
     * Removes unexisting beans and registers new ones
     * @throws EtherstubException
     */
    void discover() throws EtherstubException;
}
