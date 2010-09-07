package agh.msc.xbowbase.link;

import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * The class implements NIC MBean functionality.
 *
 * @author cieplik
 */
public class Nic extends Link implements NicMBean {


    /**
     * @see NicMBean#getStatistics()
     */
    @Override
    public Map<LinkStatistics, String> getStatistics() throws LinkException {

        logger.info("Getting statistics map from nic: " + this.name);

        for (LinkStatistics statistic : LinkStatistics.values()) {
            this.statisticsMap.put(statistic, nicHelper.getLinkStatistic(name, statistic));
        }
        return this.statisticsMap;
    }


    /**
     * @see  NicMBean#isUp()
     */
    @Override
    public boolean isUp() {
        return nicHelper.isUp(name);
    }

    /**
     * @see  NicMBean#setUp( boolean )
     */
    @Override
    public void setUp(boolean up) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNicHelper(NicHelper nicHelper) {
        this.nicHelper = nicHelper;
				super.linkHelper = nicHelper;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {

            return true;

        } else if (o instanceof Nic) {

            return name.equals(((Nic) o).getName());

        } else {

            return false;

        }

    }

    @Override
    public int hashCode() {

        int hash = 7;
        hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);

        return hash;

    }

    @Override
    public String toString() {
        return name;
    }

    private Map<LinkStatistics, String> statisticsMap = new HashMap<LinkStatistics, String>();
    private Map<LinkProperties, String> propertiesMap = new HashMap<LinkProperties, String>();
    private Map<LinkParameters, String> parametersMap = new HashMap<LinkParameters, String>();
    private boolean plumbed;
    private boolean up;
    NicHelper nicHelper;
    private static final Logger logger = Logger.getLogger(Nic.class);
}
