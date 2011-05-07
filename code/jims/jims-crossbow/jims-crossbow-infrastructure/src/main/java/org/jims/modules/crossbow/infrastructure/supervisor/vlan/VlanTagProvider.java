package org.jims.modules.crossbow.infrastructure.supervisor.vlan;


/**
 *
 * @author cieplik
 */
public interface VlanTagProvider {

	int provide();

	void refresh();

}
