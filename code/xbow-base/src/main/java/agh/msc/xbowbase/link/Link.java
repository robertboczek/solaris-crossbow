package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.LinkHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * The class implements Link MBean functionality.
 *
 * @see LinkMBean
 *
 * @author cieplik
 */
public abstract class Link implements LinkMBean {

    /**
     * @see LinkMBean#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @see LinkMBean#getIpMask()
     */
    @Override
    public String getIpMask() throws LinkException {
        return linkHelper.getNetmask(name);
    }

    /**
     * @see LinkMBean#setIpMask(java.lang.String)
     */
    @Override
    public void setIpMask(String ipMask) throws LinkException {
        linkHelper.setNetmask(name, ipMask);
    }

    /**
     * @see VNicMBean#getIpAddress()
     */
    @Override
    public String getIpAddress() throws LinkException {
        this.ipAddress = linkHelper.getIpAddress(this.name);
        return this.ipAddress;
    }

    /**
     * @see VNicMBean#setIpAddress(java.lang.String)
     */
    @Override
    public void setIpAddress(String ipAddress) throws LinkException {
        try {
            linkHelper.setIpAddress(this.name, ipAddress);
            this.ipAddress = ipAddress;
        } catch (LinkException e) {
            logger.error("Couldn't set new ip address: " + ipAddress + " to vnic: " + this.name);
        }
    }

        /**
     * @see VNicMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String)
     */
    @Override
    public void setProperty(LinkProperties property, String value) throws LinkException {

        logger.info("Setting new property value to property " + property + " to vnic: " + this.name + " with value: " + value);

        linkHelper.setLinkProperty(this.name, property, value);
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
            this.parametersMap.put(parameter, linkHelper.getLinkParameter(name, parameter));
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
            this.statisticsMap.put(statistic, linkHelper.getLinkStatistic(name, statistic));
        }
        return this.statisticsMap;
    }

    @Override
    public boolean isPlumbed() throws LinkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setPlumbed(boolean plumbed) throws LinkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isUp() throws LinkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setUp(boolean up) throws LinkException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLinkHelper(LinkHelper linkHelper) {
        this.linkHelper = linkHelper;
    }


    protected String name;
    protected String ipAddress;
    protected LinkHelper linkHelper;

    protected Map<LinkStatistics, String> statisticsMap = new HashMap<LinkStatistics, String>();
    protected Map<LinkProperties, String> propertiesMap = new HashMap<LinkProperties, String>();
    protected Map<LinkParameters, String> parametersMap = new HashMap<LinkParameters, String>();

    private static final Logger logger = Logger.getLogger(Link.class);
}
