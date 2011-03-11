package org.jims.modules.crossbow.lib;

import org.jims.modules.crossbow.exception.IncompatibleFlowException;
import org.jims.modules.crossbow.exception.NoSuchFlowException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.FlowInfo;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import java.util.List;
import java.util.Map;
import org.jims.modules.crossbow.flow.enums.FlowStatistics;


/**
 * Flow helper interface.
 *
 * Used to manage and query flows.
 *
 * @author cieplik
 */
public interface FlowHelper {

	/**
	 * Retrieves FlowInfo objects for all flows in the system (created over all links).
	 *
	 * @return  list of FlowInfo objects
	 */
	public List< FlowInfo > getFlowsInfo();

	/**
	 * Retrieves FlowInfo objects for all flows created over specified links.
	 *
	 * @param  links  links' names
	 *
	 * @return  list of FlowInfo objects
	 */
	public List< FlowInfo > getFlowsInfo( List< String > links );


	/**
	 * Creates new flow in the system.
	 *
	 * @param  flowInfo  flow descriptor
	 *
	 * @throws  IncompatibleFlowException  creation failed because of incompatibilities
	 *                                     with another existing flow
	 * @throws  XbowException              creation failed
	 */
	public void create( FlowInfo flowInfo ) throws IncompatibleFlowException,
	                                               XbowException;


	/**
	 * Deletes a flow.
	 *
	 * @param  flow       name of the flow to be removed
	 * @param  temporary  determines if the operation is temporary
	 *
	 * @throws  NoSuchFlowException  specified flow doesn't exist
	 * @throws  XbowException        removal failed
	 */
	public void remove( String flow, boolean temporary ) throws XbowException,
	                                                            NoSuchFlowException;


	/**
	 * Retrieves flow's attributes.
	 *
	 * @param  flowName  flow name
	 *
	 * @throws  NoSuchFlowException  the flow of given name doesn't exist
	 *
	 * @return  attributes map
	 */
	Map< FlowAttribute, String > getAttributes( String flowName ) throws NoSuchFlowException;


	/**
	 * Sets flow properties.
	 *
	 * @param  flowName    flow name
	 * @param  properties  map of properties to be set
	 * @param  temporary   determines if the operation is temporary
	 *
	 * @throws  NoSuchFlowException  the flow with specified name doesn't exist in the system
	 * @throws  ValidationException  error while validating attributes
	 */
	void setProperties( String flowName, Map< FlowProperty, String > properties, boolean temporary )
		throws NoSuchFlowException,
		       ValidationException,
		       XbowException;


	/**
	 * Retrieves flow's properties.
	 *
	 * @param  flowName  flow name
	 *
	 * @throws  NoSuchFlowException  the flow with specified name doesn't exist in the system
	 *
	 * @return  properties map
	 */
	Map< FlowProperty, String > getProperties( String flowName ) throws NoSuchFlowException;


	/**
	 * Resets flow properties.
	 *
	 * @param  flowName    flow name
	 * @param  properties  map of properties to be reset
	 * @param  temporary   determines if the operation is temporary
	 *
	 * @throws  NoSuchFlowException  the flow with specified name doesn't exist in the system
	 * @throws  ValidationException  error while validating properties
	 */
	void resetProperties( String flowName, List< FlowProperty > properties, boolean temporary )
		throws NoSuchFlowException,
		       ValidationException;


	Map< FlowStatistics, Long > getUsage( String flowName, String startTime );

}
