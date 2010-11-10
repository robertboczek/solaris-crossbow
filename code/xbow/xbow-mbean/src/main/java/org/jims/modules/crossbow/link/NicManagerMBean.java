package org.jims.modules.crossbow.link;

import java.util.List;


/**
 * NicManagerMBean interface.
 *
 * Provides operations to discover NICs.
 *
 * @author cieplik
 */
public interface NicManagerMBean {

	/**
	 * @brief  Retrieves list of NICs in the system.
	 *
	 * @return  list of NICs
	 */
	public List< String > getNicsList();

	/**
	 * @brief  Discovers NICs present in the system.
	 */
	public void discover();

}
