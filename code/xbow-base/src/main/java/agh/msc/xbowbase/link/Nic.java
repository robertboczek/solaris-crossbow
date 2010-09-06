package agh.msc.xbowbase.link;

import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.enums.LinkProperties;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * The class implements NIC MBean functionality.
 *
 * @author cieplik
 */
public class Nic extends Link implements NicMBean {

    /**
     * @see NicMBean#getProperties()
     */
    @Override
    public Map<LinkProperties, String> getProperties() throws LinkException {

        logger.info("Getting properties map from nic: " + this.name);

        for (LinkProperties property : LinkProperties.values()) {
            this.propertiesMap.put(property, nicHelper.getLinkProperty(name, property));
        }
        return this.propertiesMap;

    }

    /**
     * @see  NicMBean#isPlumbed()
     */
    @Override
    public boolean isPlumbed() {
			// TODO-DAWID
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @see  NicMBean#setPlumbed( boolean )
     */
    @Override
    public void setPlumbed(boolean plumbed) {
			nicHelper.plumb( name );
			// TODO-DAWID
        // throw new UnsupportedOperationException("Not supported yet.");
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

    NicHelper nicHelper;
    private static final Logger logger = Logger.getLogger(Nic.class);
}
