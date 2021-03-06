package org.jims.modules.crossbow.flow;

import org.jims.modules.crossbow.exception.XbowException;
import java.util.List;
import org.jims.modules.crossbow.manager.GenericManager;


/**
 * FlowManagerMBean interface.
 *
 * Provides operations to discover, create and remove flows.
 *
 * @author cieplik
 */
public interface FlowManagerMBean extends GenericManager< FlowMBean > {

	/**
	 * @brief  Returns names of all flows.
	 *
	 * @return  flows' names
	 */
	public List< String > getFlows();

	/**
	 * @brief  Returns MBean object for flow identified with name.
	 *
	 * @param  name  of the flow
	 *
	 * @return  MBean object
	 */
	public FlowMBean getByName( String name );

	/**
	 * @brief  Discovers flows present in the system.
	 *
	 * Discovered flow processing is implementation-specific.
	 */
	public void discover();

	/**
	 * @brief  Creates flow in the system.
	 *
	 * @param  flow  initialized object representing a flow
	 *
	 * @throws  XbowException              creation failed
	 */
	public void create( FlowMBean flow ) throws XbowException;

	/**
	 * @brief  Removes flow from the system.
	 *
	 * @param  flowName   name of the flow to remove
	 * @param  temporary  determines whether the operation is temporary.
	 *                    If so, the flow will be present after system reboot.
	 *
	 * @throws  XbowException  flow removal failed
	 */
	public void remove( String flowName, boolean temporary ) throws XbowException;


	/*
	 * jconsole only
	 */

	public void _create( String flowName, String link, String attributes ) throws XbowException;

}
