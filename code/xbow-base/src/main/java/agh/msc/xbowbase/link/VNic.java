package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.VNicHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Implements all method from @see VNicMBean
 * 
 * @author robert boczek
 */
public class VNic implements VNicMBean {

    private static final Logger logger = Logger.getLogger(VNic.class);
    private final String name;
    private final String parent;
    private final Map<LinkProperties, String> propertiesMap;
    private final Map<LinkParameters, String> parametersMap;
    private final Map<LinkStatistics, String> statisticsMap;
    private final boolean temporary;
    private String ipAddress;
    private String ipMask;
    private boolean plumbed;
    private boolean up;
    private boolean linkParent;
    private VNicHelper vNicHelper;

    /**
     * Constructor of VNic class
     *
     * @param name Name of vnic
     * @param temporary Specifies whether this vnic will exist between reboots
     * @param parent Name of link under whom this vnic works
     */
    public VNic(String name, boolean temporary, String parent) {
        this.name = name;
        this.temporary = temporary;
        this.parent = parent;

        propertiesMap = new HashMap<LinkProperties, String>();
        parametersMap = new HashMap<LinkParameters, String>();
        statisticsMap = new HashMap<LinkStatistics, String>();
    }

    /**
     * @see VNicMBean#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @see VNicMBean#getProperties() 
     */
    @Override
    public Map<LinkProperties, String> getProperties() throws LinkException {

        logger.info("Getting properties map from vnic: " + this.name);

        for (LinkProperties property : LinkProperties.values()) {
            this.propertiesMap.put(property, vNicHelper.getLinkProperty(name, property));
        }
        return this.propertiesMap;

    }

    /**
     * @see VNicMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String)
     */
    @Override
    public void setProperty(LinkProperties property, String value) throws LinkException {

        logger.info("Setting new property value to property " + property + " to vnic: " + this.name + " with value: " + value);

        vNicHelper.setLinkProperty(this.name, property, value);
        //@todo check return value
        this.propertiesMap.put(property, value);
    }

    /**
     * @see VNicMBean#getParameters() 
     */
    @Override
    public Map<LinkParameters, String> getParameters() throws LinkException {

        logger.info("Getting parameters map from vnic: " + this.name);

        for (LinkParameters parameter : LinkParameters.values()) {
            this.parametersMap.put(parameter, vNicHelper.getLinkParameter(name, parameter));
        }
        return this.parametersMap;
    }

    /**
     * @see VNicMBean#getStatistics()
     */
    @Override
    public Map<LinkStatistics, String> getStatistics() throws LinkException {

        logger.info("Getting statistics map from vnic: " + this.name);

        for (LinkStatistics statistic : LinkStatistics.values()) {
            this.statisticsMap.put(statistic, vNicHelper.getLinkStatistic(name, statistic));
        }
        return this.statisticsMap;
    }

    /**
     * @see VNicMBean#getIpAddress()
     */
    @Override
    public String getIpAddress() throws LinkException {
        //@todo use jna to get vnic's ip address
        return this.ipAddress;
    }

    /**
     * @see VNicMBean#setIpAddress(java.lang.String)
     */
    @Override
    public void setIpAddress(String ipAddress) throws LinkException {
        //@todo use jna to set vnic's ip address
        this.ipAddress = ipAddress;
    }

    /**
     * @see VNicMBean#getIpMask()
     */
    @Override
    public String getIpMask() throws LinkException {
        //@todo use jna to get vnic's ip mask
        return this.ipMask;
    }

    /**
     * @see VNicMBean#setIpMask(java.lang.String)
     */
    @Override
    public void setIpMask(String ipMask) throws LinkException {
        //@todo use jna to set vnic's ip mask
        this.ipMask = ipMask;
    }

    /**
     * @see VNicMBean#isPlumbed()
     */
    @Override
    public boolean isPlumbed() throws LinkException {
        //@todo use jna to get vnic's plumbed state
        return plumbed;
    }

    /**
     * @see VNicMBean#setPlumbed(boolean)
     */
    @Override
    public void setPlumbed(boolean plumbed) throws LinkException {
        //@todo use jna to set vnic's plumbed state
        this.plumbed = plumbed;
    }

    /**
     * @see VNicMBean#isUp() 
     */
    @Override
    public boolean isUp() throws LinkException {
        //@todo use jna to get vnic's up state
        return this.up;
    }

    /**
     * @see VNicMBean#setUp(boolean)
     */
    @Override
    public void setUp(boolean up) throws LinkException {
        //@todo use jna to set vnic's up state
        this.up = up;
    }

    /**
     * @see VNicMBean#isTemporary()
     */
    @Override
    public boolean isTemporary() {
        return this.temporary;
    }

    /**
     * @see VNicMBean#isEtherstubParent() 
     */
    @Override
    public boolean isEtherstubParent() throws LinkException {
        //@todo use jna to get info whether etherstub is parent of this vnic
        return linkParent;
    }

    /**
     * @see VNicMBean#getParent() 
     */
    @Override
    public String getParent() throws LinkException {
        return this.parent;
    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);

        return hash;

    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        } else if (o instanceof VNic) {
            return name.equals(((VNic) o).getName());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setVNicHelper(VNicHelper vNicHelper) {

        this.vNicHelper = vNicHelper;
    }
}
