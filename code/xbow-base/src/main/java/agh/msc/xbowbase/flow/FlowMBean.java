package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.ValidationException;
import java.util.List;
import java.util.Map;


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
	 * @return  flow attributes map
	 */
	public Map< String, String > getAttributes();

	/**
	 * @brief  Properties getter method.
	 *
	 * @return  flow properties map
	 */
	public Map< String, String > getProperties();

	/**
	 * @brief  Properties setter method.
	 *
	 * @param  properties  properties map
	 * @param  temporary   indicates whether the operation is temporary.
	 *                     If so, the properties will be reset after syste reboot
	 *
	 * @throws  ValidationException  properties validation failed
	 */
	public void setProperties( Map< String, String > properties, boolean temporary ) throws ValidationException;

	/**
	 * @brief  Resets flow's properties.
	 *
	 * @param  properties  properties map
	 * @param  temporary   indicates whether the operation is temporary
	 */
	public void resetProperties( List< String > properties, boolean temporary ) throws ValidationException;

	/**
	 * @brief  Temporary getter method.
	 *
	 * @return  true  iff the flow is not persistent
	 */
	public boolean isTemporary();

}
