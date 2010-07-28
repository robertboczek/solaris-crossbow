
package agh.msc.xbowbase.lib;

import com.sun.jna.Library;
import agh.msc.xbowbase.etherstub.EtherstubMBean;

/**
 *
 * @author root
 */
public interface EtherstubAdm extends Library{

    public void init();

    public int remove(EtherstubMBean etherstubMBean);
}
