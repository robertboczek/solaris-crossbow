package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.ValidationException;
import java.util.Map;


/**
 * LinkMBean interface.
 *
 * Provides operations and attributes to manage and monitor links.
 *
 * @author cieplik
 */
public interface LinkMBean {

	/**
	 * @brief  Name getter method.
	 *
	 * @return  link name
	 */
	public String getName();

	/**
	 * @brief  Properties getter method.
	 *
	 * @return  link properties
	 */
	public Map<LinkProperties, String> getProperties() throws LinkException;

	/**
	 * Sets requested property value specified by the user
	 *
	 * @param property Type of property to be set
	 * @param value Requested value
	 */
	public void setProperty(LinkProperties property, String value) throws LinkException;

	/**
	 * @brief  Parameters getter method.
	 *
	 * @return  link parameters
	 */
	public Map<LinkParameters, String> getParameters() throws LinkException;

	/**
	 * @brief   Statistics setter method
	 *
	 * @return  link statistics
	 */
	public Map<LinkStatistics, String> getStatistics() throws LinkException;

	/**
	 * @brief  IP address getter method.
	 *
	 * @return  link IP address
	 */
	public String getIpAddress() throws LinkException;

	/**
	 * @brief  IP address setter method.
	 *
	 * @param  ipAddress  IP address
	 */
	public void setIpAddress(String ipAddress) throws LinkException, ValidationException;

	/**
	 * @brief  IP mask getter method.
	 *
	 * @return  link IP mask
	 */
	public String getIpMask() throws LinkException;

	/**
	 * @brief  IP mask setter method.
	 *
	 * @param  ipMask  IP mask
	 */
	public void setIpMask(String ipMask) throws ValidationException, LinkException;

	/**
	 * @brief  Plumbed getter method.
	 *
	 * @return  true iff the link is plumbed
	 */
	public boolean isPlumbed() throws LinkException;

	/**
	 * @brief  Plumbed setter method.
	 *
	 * @param  plumbed  true iff the link is plumbed
	 */
	public void setPlumbed(boolean plumbed) throws LinkException;

	/**
	 * @brief  Up getter method.
	 *
	 * @return  true iff the link is up
	 */
	public boolean isUp() throws LinkException;

	/**
	 * @brief  Up setter method.
	 *
	 * @param  up  true iff the link is up
	 */
	public void setUp(boolean up) throws LinkException;


	/*
	 * jconsole only
	 */

	public Map< String, String > get_Properties() throws LinkException;

	public void _setProperty( String property, String value ) throws LinkException;

	public Map< String, String > get_Parameters() throws LinkException;

        public Map<String, String> get_Statistics2() throws LinkException;

}
