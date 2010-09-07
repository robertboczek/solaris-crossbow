package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;
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
	public void setIpAddress(String ipAddress) throws LinkException;

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

	/*
	public Map< String, String > getPropertiesJC() throws LinkException;

	public void setPropertyJC( String property, String value ) throws LinkException;

	public Map< String, String > getParametersJC() throws LinkException;
	 */

}
