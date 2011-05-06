package org.jims.modules.crossbow.flow;

import org.jims.modules.crossbow.exception.NoSuchFlowException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import java.util.List;
import java.util.Map;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.flow.enums.FlowStatistics;


/**
 * FlowMBean interface.
 *
 * Provides operations and attributes to manage and monitor flows.
 *
 * @author cieplik
 */
public interface FlowMBean {

	/**
	 * @brief  Name getter method.
	 *
	 * @return  flow name
	 */
	public String getName();

	/**
	 * @brief  Link getter method.
	 *
	 * @return  link name
	 */
	public String getLink();

	/**
	 * @brief  Attributes getter method.
	 *
	 * @throws  NoSuchFlowException  the flow hasn't been created in the system
	 *
	 * @return  flow attributes map
	 */
	public Map< FlowAttribute, String > getAttributes() throws NoSuchFlowException;

	/**
	 * @brief  Properties getter method.
	 *
	 * @throws  NoSuchFlowException  the flow hasn't been created in the system
	 *
	 * @return  flow properties map
	 */
	public Map< FlowProperty, String > getProperties() throws NoSuchFlowException;

	/**
	 * @brief  Properties setter method.
	 *
	 * @param  properties  properties map
	 * @param  temporary   indicates whether the operation is temporary.
	 *                     If so, the properties will be reset after syste reboot
	 *
	 * @throws  NoSuchFlowException  the flow hasn't been created in the system
	 * @throws  ValidationException  properties validation failed
	 */
	public void setProperties( Map< FlowProperty, String > properties, boolean temporary )
		throws NoSuchFlowException,
		       ValidationException;

	/**
	 * @brief  Resets flow's properties.
	 *
	 * @param  properties  properties map
	 * @param  temporary   indicates whether the operation is temporary
	 *
	 * @throws  NoSuchFlowException  the flow hasn't been created in the system
	 * @throws  ValidationException  properties validation failed
	 */
	public void resetProperties( List< FlowProperty > properties, boolean temporary )
		throws NoSuchFlowException,
		       ValidationException;

	/**
	 * @brief  Temporary getter method.
	 *
	 * @return  true  iff the flow is not persistent
	 */
	public boolean isTemporary();


	/**
	 * @brief Returns the flow's usage statistics.
	 *
	 * @return  flow statistics map
	 */
	public Map< FlowStatistics, Long > getStatistics();

        /**
         * @brief Gets statistics for specified period
         *
         * @param period Time period type
         * @return Returns List containging map with statistics for specified time period
         */
        public List<Map<FlowStatistics, Long>> getStatistics(LinkStatisticTimePeriod period);


	/*
	 * jconsole only
	 */

	public Map< String, String > get_Attributes() throws NoSuchFlowException;

	public void set_Property( String name, String value, boolean temporary ) throws NoSuchFlowException,
	                                                                                ValidationException;

	public Map< String, String > get_Properties() throws NoSuchFlowException;

	public void _resetProperty( String name, boolean temporary ) throws NoSuchFlowException,
	                                                                    ValidationException;

	public Map< String, String > get_Statistics();

}
