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
 * @brief EtherstubMBean implementation
 * Implementation of EtherstubMBean interface
 *
 * @author robert boczek
 */
public class Etherstub implements EtherstubMBean {

    /** Logger */
    private static final Logger logger = Logger.getLogger(Etherstub.class);

    private String name;
    private boolean temporary;
    private Map<LinkParameters, String> parametersMap;
    private Map<LinkProperties, String> propertiesMap; //some properties contain a few values like names of cpus
    private Map<LinkStatistics, String> statisticsMap;
    private EtherstubHelper etherstubHelper = null;

    /**
     * @brief Constructor of Etherstub object
     * Constructor of Etherstub Class, it's the only way to set up name and temporary values
     * 
     * @param name Etherstub name
     * @param temporary Flag specifies whether Etherstub is temporary or persistent between reboots
     */
    public Etherstub(String name, boolean temporary) {
        this.name = name;
        this.temporary = temporary;
        this.parametersMap = new HashMap<LinkParameters, String>();
        this.propertiesMap = new HashMap<LinkProperties, String>();
        this.statisticsMap = new HashMap<LinkStatistics, String>();
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
            this.propertiesMap.put(property, etherstubHelper.getEtherstubProperty(name, property));
        }
        return this.propertiesMap;
    }

    /**
     * @see EtherstubMBean#getParameters()
     */
    @Override
    public Map<LinkParameters, String> getParameters() throws EtherstubException {

        for (LinkParameters parameter : LinkParameters.values()) {
            this.parametersMap.put(parameter, etherstubHelper.getEtherstubParameter(name, parameter));
        }
        return this.parametersMap;
    }

    /**
     * @see EtherstubMBean#getStatistics()
     */
    @Override
    public Map<LinkStatistics, String> getStatistics() throws EtherstubException {

        for (LinkStatistics statistic : LinkStatistics.values()) {
            this.statisticsMap.put(statistic, etherstubHelper.getEtherstubStatistic(name, statistic));
        }
        return this.statisticsMap;
    }

    /**
     * @see EtherstubMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String) 
     */
    @Override
    public void setProperty(LinkProperties etherstubProperty, String value) throws EtherstubException {

        logger.info("Setting new property value to property " + etherstubProperty
                + " to etherstub: " + this.name + " with value: " + value);
        
        etherstubHelper.setEtherstubProperty(this.name, etherstubProperty, value);
        //@todo check return value
        this.propertiesMap.put(etherstubProperty, value);
    }

    /**
     * @brief Injects EthertubHelper
     * Sets the implementation of EtherstubHelper
     *
     * @param etherstubHelper Concrete implementation of Ehterstubadm
     */
    public void setEtherstubHelper(EtherstubHelper etherstubHelper) {
        this.etherstubHelper = etherstubHelper;
    }

    /**
     * @brief Compares EtherstubMBean objects by their name attribute
     * Etherstub's are equal when their attributes name's are equal
     *
     * @param object Object to be compared with this one
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof EtherstubMBean) {
            return this.name.equals(((EtherstubMBean) object).getName());
        }
        return false;
    }

    /**
     * @brief Etherstub's hashCode
     * Etherstub's hashCode is eqaul to their attributes name's hashCode value
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * @brief Etherstub toString returns Etherstub's name attribute
     * @return Returns name attribute
     */
    @Override
    public String toString() {
        return this.name;
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


	@Override
    public Map< String, String > getParametersJC() throws EtherstubException {

			Map< String, String > res = new HashMap< String, String >();
			Map< LinkParameters, String > map = getParameters();

			for ( Map.Entry< LinkParameters, String > entry : map.entrySet() ) {
				res.put( entry.getKey().toString(), entry.getValue() );
			}

			return res;

		}

	@Override
	public void setPropertyJC( String property, String value ) throws EtherstubException {
		setProperty( LinkProperties.fromString( property ), value );
	}

}
