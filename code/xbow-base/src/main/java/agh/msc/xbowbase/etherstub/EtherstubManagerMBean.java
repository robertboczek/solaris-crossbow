
package agh.msc.xbowbase.etherstub;

import java.util.List;

/**
 *
 * @author robert
 */
public interface EtherstubManagerMBean {

    void create(EtherstubMBean etherstubMBean);

    void delete(String name, boolean temporary);

    List<String> getEtherstubsNames();

    void discover();
}
