package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.lib.Etherstubadm;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author robert boczek
 */
public class Etherstub implements EtherstubMBean {

    private String name;
    private boolean temporary;
    private Map<EtherstubParameters, String> parameters;
    private Map<EtherstubProperties, String> properties; //some properties contain a few values like names of cpus
    private Map<EtherstubStatistics, String> statistis;
    private Etherstubadm etherstubadm = null;

    /**
     * 
     * @param name
     * @param temporary
     */
    public Etherstub(String name, boolean temporary) {
        this.name = name;
        this.temporary = temporary;
        this.parameters = new HashMap<EtherstubParameters, String>();
        this.properties = new HashMap<EtherstubProperties, String>();
        this.statistis = new HashMap<EtherstubStatistics, String>();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isTemporary() {
        return this.temporary;
    }

    @Override
    public Map<EtherstubProperties, String> getProperties() throws EtherstubException {
        for (EtherstubProperties property : EtherstubProperties.values()) {
            this.properties.put(property, etherstubadm.getEtherstubProperty(name, property));
        }
        return this.properties;
    }

    @Override
    public Map<EtherstubParameters, String> getParameters() throws EtherstubException {
        for (EtherstubParameters parameter : EtherstubParameters.values()) {
            this.parameters.put(parameter, etherstubadm.getEtherstubParameter(name, parameter));
        }
        return this.parameters;
    }

    @Override
    public Map<EtherstubStatistics, String> getStatistics() throws EtherstubException {
        for (EtherstubStatistics statistic : EtherstubStatistics.values()) {
            this.statistis.put(statistic, etherstubadm.getEtherstubStatistic(name, statistic));
        }
        return this.statistis;
    }

    public void setProperty(EtherstubProperties etherstubProperty, String value) throws XbowException {

        etherstubadm.setEtherstubProperty(this.name, etherstubProperty, value);
        this.properties.put(etherstubProperty, value);
    }

    public void setEtherstubadm(Etherstubadm etherstubadm) {
        this.etherstubadm = etherstubadm;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof EtherstubMBean) {
            return this.name.equals(((EtherstubMBean) object).getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
