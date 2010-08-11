package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.lib.Etherstubadm;
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
    private Map<EtherstubParameters, String> parameters;
    private Map<EtherstubProperties, String> properties; //some properties contain a few values like names of cpus
    private Map<EtherstubStatistics, String> statistis;
    private Etherstubadm etherstubadm = null;

    /**
     * Constructor of Etherstub Class, it's the only way to set up name and temporary values
     * @param name Etherstub name
     * @param temporary Flag specifies whether Etherstub is temporary or persistent between reboots
     */
    public Etherstub(String name, boolean temporary) {
        this.name = name;
        this.temporary = temporary;
        this.parameters = new HashMap<EtherstubParameters, String>();
        this.properties = new HashMap<EtherstubProperties, String>();
        this.statistis = new HashMap<EtherstubStatistics, String>();
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
    public Map<EtherstubProperties, String> getProperties() throws EtherstubException {
        for (EtherstubProperties property : EtherstubProperties.values()) {
            this.properties.put(property, etherstubadm.getEtherstubProperty(name, property));
        }
        return this.properties;
    }

    /**
     * @see EtherstubMBean#getParameters()
     */
    @Override
    public Map<EtherstubParameters, String> getParameters() throws EtherstubException {
        for (EtherstubParameters parameter : EtherstubParameters.values()) {
            this.parameters.put(parameter, etherstubadm.getEtherstubParameter(name, parameter));
        }
        return this.parameters;
    }

    /**
     * @see EtherstubMBean#getStatistics()
     */
    @Override
    public Map<EtherstubStatistics, String> getStatistics() throws EtherstubException {
        for (EtherstubStatistics statistic : EtherstubStatistics.values()) {
            this.statistis.put(statistic, etherstubadm.getEtherstubStatistic(name, statistic));
        }
        return this.statistis;
    }

    /**
     * Sets requested property value specified by the user
     * @param etherstubProperty Type of property to be set
     * @param value Requested value
     * @throws XbowException Exeption thrown in case of error
     */
    public void setProperty(EtherstubProperties etherstubProperty, String value) throws EtherstubException {

        logger.info("Setting new property value to property " + etherstubProperty
                + " to etherstub: " + this.name + " with value: " + value);
        etherstubadm.setEtherstubProperty(this.name, etherstubProperty, value);
        this.properties.put(etherstubProperty, value);
    }

    /**
     * Sets the implementation of Etherstubadm
     * @param etherstubadm Conrete implementation of Ehterstubadm
     */
    public void setEtherstubadm(Etherstubadm etherstubadm) {
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
}
