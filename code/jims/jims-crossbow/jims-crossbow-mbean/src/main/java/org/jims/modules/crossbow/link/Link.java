package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.lib.LinkHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;

/**
 * The class implements Link MBean functionality.
 *
 * @see LinkMBean
 *
 * @author cieplik
 */
public abstract class Link implements LinkMBean {

    protected String name;
    protected LinkHelper linkHelper;
    protected Map<LinkStatistics, String> statisticsMap = new HashMap<LinkStatistics, String>();
    protected Map<LinkProperties, String> propertiesMap = new HashMap<LinkProperties, String>();
    protected Map<LinkParameters, String> parametersMap = new HashMap<LinkParameters, String>();
    private static final Logger logger = Logger.getLogger(Link.class);

    protected LinkStatisticsGatherer linkStatisticsGatherer;

    /**
     * Constructor of link object
     *
     * @param name Link name value
     */
    public Link(String name) {
        this.name = name;
        this.linkStatisticsGatherer = new LinkStatisticsGatherer(name);
    }

    /**
     * @see LinkMBean#getName()
     */
    @Override
    public String getName() {
        return this.name;
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
    public void setIpMask(String ipMask) throws LinkException, ValidationException {
        linkHelper.setNetmask(name, ipMask);
    }

    /**
     * @see VNicMBean#getIpAddress()
     */
    @Override
    public String getIpAddress() throws LinkException {
        return linkHelper.getIpAddress(this.name);
    }

    /**
     * @see VNicMBean#setIpAddress(java.lang.String)
     */
    @Override
    public void setIpAddress(String ipAddress) throws LinkException {
        try {

            linkHelper.setIpAddress(this.name, ipAddress);

        } catch (LinkException e) {
            logger.error("Couldn't set new ip address: " + ipAddress + " to vnic: " + this.name, e);
        } catch (ValidationException ex) {
            logger.error("Couldn't set new ip address: " + ipAddress + " to vnic: " + this.name + ". Ip format is incorrect", ex);
        }
    }

		/**
		 * @see LinkMBean#resetProperty(org.jims.modules.crossbow.enums.LinkProperties)
		 */
		@Override
		public void resetProperty( LinkProperties property ) throws LinkException {
			// TODO implement
			throw new UnsupportedOperationException("Not supported yet.");
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

    /**
     * @see LinkMBean#getProperties()
     */
    @Override
    public Map<LinkProperties, String> getProperties() throws LinkException {

        logger.info("Getting properties for " + this.name);

        Map<LinkProperties, String> properties = new HashMap<LinkProperties, String>();

        for (LinkProperties property : LinkProperties.values()) {
            properties.put(property, linkHelper.getLinkProperty(name, property));
        }

        return properties;

    }

    @Override
    public boolean isPlumbed() throws LinkException {
        return linkHelper.isPlumbed(name);
    }

    @Override
    public void setPlumbed(boolean plumbed) throws LinkException {

        if (!isPlumbed()) {

            linkHelper.plumb(name);
            logger.info(name + " plumbed.");

        } else {

            logger.info("Not plumbing " + name + " as it's already plumbed.");

        }

    }

    @Override
    public boolean isUp() throws LinkException {
        return this.linkHelper.isUp(this.name);
    }

    @Override
    public void setUp(boolean up) throws LinkException {
        this.linkHelper.putUp(this.name, up);
    }

    public void setLinkHelper(LinkHelper linkHelper) {
        this.linkHelper = linkHelper;

        linkStatisticsGatherer.setLinkHelper(linkHelper);
    }


    /*
     * JConsole only
     */
    @Override
    public Map<String, String> get_Properties() throws LinkException {

        Map<String, String> res = new HashMap<String, String>();

        for (Map.Entry<LinkProperties, String> entry : getProperties().entrySet()) {
            res.put(entry.getKey().toString(), entry.getValue());
        }

        return res;

    }

    @Override
    public void _setProperty(String property, String value) throws LinkException {
        setProperty(LinkProperties.fromString(property), value);
    }

    @Override
    public Map<String, String> get_Parameters() throws LinkException {

        Map<String, String> res = new HashMap<String, String>();

        for (Map.Entry<LinkParameters, String> entry : getParameters().entrySet()) {
            res.put(entry.getKey().toString(), entry.getValue());
        }

        return res;

    }

    @Override
    public Map<String, String> get_Statistics2() throws LinkException {

        Map<String, String> res = new HashMap<String, String>();

        for (LinkStatistics l : LinkStatistics.values()) {
           res.put(l.toString(), linkHelper.getLinkStatistic(name, l));
        }

        return res;

    }

    @Override
    public List<Map<LinkStatistics, Long>> getStatistics(LinkStatisticTimePeriod period) {
        return linkStatisticsGatherer.getStatistics(period);
    }

    public void startGatheringStatistics(){
       linkStatisticsGatherer.start();
    }

    public void stopGatheringStatistics(){
       linkStatisticsGatherer.stop();
    }
}
