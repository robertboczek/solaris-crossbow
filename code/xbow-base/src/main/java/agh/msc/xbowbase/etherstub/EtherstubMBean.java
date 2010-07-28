
package agh.msc.xbowbase.etherstub;

import java.util.Map;

/**
 *
 * @author robert
 */
public interface EtherstubMBean {

    public String getName();

    public boolean isTemporary();

    public String getRootDir();

    public Map<String, String> getProperties();
    
    public Map<String, String> getParameters();

}
