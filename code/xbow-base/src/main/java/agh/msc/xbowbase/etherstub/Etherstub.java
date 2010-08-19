package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.EtherstubHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Implementation of EtherstubMBean interface
 * @author robert boczek
 */
public class Etherstub implements EtherstubMBean {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Etherstub.class);

    private String name;
    private boolean temporary;
    private Map<LinkParameters, String> parameters;
    private Map<LinkProperties, String> properties; //some properties contain a few values like names of cpus
    private Map<LinkStatistics, String> statistis;
    private EtherstubHelper etherstubadm = null;

    /**
     * Constructor of Etherstub Class, it's the only way to set up name and temporary values
     * @param name Etherstub name
     * @param temporary Flag specifies whether Etherstub is temporary or persistent between reboots
     */
    public Etherstub(String name, boolean temporary) {
        this.name = name;
        this.temporary = temporary;
        this.parameters = new HashMap<LinkParameters, String>();
        this.properties = new HashMap<LinkProperties, String>();
        this.statistis = new HashMap<LinkStatistics, String>();
    }

    /**
     * @see EtherstubMBean#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @see EtherstubMBean#isTemporary()
     */
    @Override
    public boolean isTemporary() {
        return this.temporary;
    }

    /**
     * @see EtherstubMBean#getProperties()
     */
    @Override
    public Map<LinkProperties, String> getProperties() throws EtherstubException {
        for (LinkProperties property : LinkProperties.values()) {
            this.properties.put(property, etherstubadm.getEtherstubProperty(name, property));
        }
        return this.properties;
    }

    /**
     * @see EtherstubMBean#getParameters()
     */
    @Override
    public Map<LinkParameters, String> getParameters() throws EtherstubException {
        for (LinkParameters parameter : LinkParameters.values()) {
            this.parameters.put(parameter, etherstubadm.getEtherstubParameter(name, parameter));
        }
        return this.parameters;
    }

    /**
     * @see EtherstubMBean#getStatistics()
     */
    @Override
    public Map<LinkStatistics, String> getStatistics() throws EtherstubException {
        for (LinkStatistics statistic : LinkStatistics.values()) {
            this.statistis.put(statistic, etherstubadm.getEtherstubStatistic(name, statistic));
        }
        return this.statistis;
    }

    /**
     * @see EtherstubMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String) 
     */
    @Override
    public void setProperty(LinkProperties etherstubProperty, String value) throws EtherstubException {

        logger.info("Setting new property value to property " + etherstubProperty
                + " to etherstub: " + this.name + " with value: " + value);
        etherstubadm.setEtherstubProperty(this.name, etherstubProperty, value);
        this.properties.put(etherstubProperty, value);
    }

    /**
     * Sets the implementation of EtherstubHelper
     * @param etherstubadm Conrete implementation of Ehterstubadm
     */
    public void setEtherstubadm(EtherstubHelper etherstubadm) {
        this.etherstubadm = etherstubadm;
    }

    /**
     * Etherstub's are equal when their attributes name's are equal
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof EtherstubMBean) {
            return this.name.equals(((EtherstubMBean) object).getName());
        }
        return false;
    }

    /**
     * Etherstub's hashCode is eqaul to their attributes name's hashCode value
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }


		/*
		 * JConsole only
		 */

	@Override
    public Map< String, String > getPropertiesJC() throws EtherstubException {

			Map< String, String > result = new HashMap< String, String >();

			for ( Map.Entry< LinkProperties, String > entry : getProperties().entrySet() ) {
				result.put( entry.getKey().name(),  entry.getValue() );
			}

			return result;

		}

    // public Map< String, String > getParametersJC() throws EtherstubException;

		// public void setPropertyJC( String property, String value ) throws EtherstubException;
}
