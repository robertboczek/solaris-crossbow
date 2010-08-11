package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


    /**
 * The class implements NIC MBean functionality.
 *
 * @author cieplik
 */
public class Nic implements NicMBean {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Nic.class);

    private String name;
    private Map<LinkStatistics, String> statisticsMap;
    private Map<LinkProperties, String> propertiesMap;
    private Map<LinkParameters, String> parametersMap;
    private String ipAddress;
    private String ipMask;
    private boolean plumbed;
    private boolean up;

    public Nic(String name) {

        this.name = name;
        this.propertiesMap = new HashMap<LinkProperties, String>();
        this.parametersMap = new HashMap<LinkParameters, String>();
        this.statisticsMap = new HashMap<LinkStatistics, String>();
    }

    /**
     * @see  NicMBean#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @see  NicMBean#getIpAddress()
     */
    @Override
    public String getIpAddress() throws LinkException{
        return this.ipAddress;
    }

    /**
     * @see  NicMBean#setIpAddress( java.lang.String )
     */
    @Override
    public void setIpAddress(String ipAddress) throws LinkException{
        //@todo use jna library to set ip address
        this.ipAddress = ipAddress;
    }

    /**
     * @see  NicMBean#getIpMask()
     */
    @Override
    public String getIpMask() throws LinkException{
        //@todo read use jna library to read ip mask
        return this.ipMask;
    }

    /**
     * @see  NicMBean#setIpMask( java.lang.String )
     */
    @Override
    public void setIpMask(String ipMask) throws LinkException{
        //@todo use jna library to set ip mask
        this.ipMask = ipMask;
    }

    /**
     * @see  NicMBean#isPlumbed()
     */
    @Override
    public boolean isPlumbed() throws LinkException{
        //@todo use jna library to get plumbed property
        return this.plumbed;
    }

    /**
     * @see  NicMBean#setPlumbed( boolean )
     */
    @Override
    public void setPlumbed(boolean plumbed) throws LinkException{
        //@todo use jna library to set plumbed property
        this.plumbed = plumbed;
    }

    /**
     * @see  NicMBean#isUp()
     */
    @Override
    public boolean isUp() throws LinkException{
        //@todo use jna library to get up property
        return this.up;
    }

    /**
     * @see  NicMBean#setUp( boolean )
     */
    @Override
    public void setUp(boolean up) throws LinkException{
        //@todo use jna library to set up property
        this.up = up;
    }

    /**
     * @see LinkMBean#getProperties()
     */
    @Override
    public Map<LinkProperties, String> getProperties() throws LinkException{
        //@todo read parameters by the jna from system
        return this.propertiesMap;
    }

    /**
     * @see LinkMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String)
     */
    @Override
    public void setProperty(LinkProperties property, String value) throws LinkException{
        //@todo set property with the jna
        this.propertiesMap.put(property, value);
    }

    /**
     * @see LinkMBean#getParameters()
     */
    @Override
    public Map<LinkParameters, String> getParameters() throws LinkException{
        //@todo read parameters by the jna from system
        return this.parametersMap;
    }

    /**
     * @see LinkMBean#getStatistics()
     */
    @Override
    public Map<LinkStatistics, String> getStatistics() throws LinkException{
        //@todo read parameters by the jna from system
        return this.statisticsMap;
    }

    /**
     * Nic's are equal when their attributes name's are equal
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof NicMBean) {
            return this.name.equals(((NicMBean) object).getName());
        }
        return false;
    }

    /**
     * Nic's hashCode is eqaul to their attributes name's hashCode value
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
