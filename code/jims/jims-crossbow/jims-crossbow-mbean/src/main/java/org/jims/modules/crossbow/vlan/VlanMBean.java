package org.jims.modules.crossbow.vlan;


/**
 *
 * @author cieplik
 */
public interface VlanMBean {

	/**
	 * Retrieves the interface name associated with the VLAN.
	 *
	 * @return  interface name
	 */
	String getName();

	/**
	 * Returns the link the VLAN is created over.
	 * 
	 * @return  link name
	 */
	String getLink();

	/**
	 * Returns the VLAN tag/id.
	 *
	 * @return  VLAN tag
	 */
	int getTag();

}
