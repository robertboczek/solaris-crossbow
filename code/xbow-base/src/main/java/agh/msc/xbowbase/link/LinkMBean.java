package agh.msc.xbowbase.link;

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
	public Map< String, String > getProperties();

	/**
	 * @brief  Properties setter method.
	 *
	 * @param  properties  properties map
	 */
	public void setProperties( Map< String, String > properties );

	/**
	 * @brief  Parameters getter method.
	 *
	 * @return  link parameters
	 */
	public Map< String, String > getParameters();

	/**
	 * @brief  Parameters setter method.
	 *
	 * @param  parameters map
	 */
	public void setParameters( Map< String, String > parameters );

	/**
	 * @brief  IP address getter method.
	 *
	 * @return  link IP address
	 */
	public String getIpAddress();

	/**
	 * @brief  IP address setter method.
	 *
	 * @param  ipAddress  IP address
	 */
	public void setIpAddress( String ipAddress );

	/**
	 * @brief  IP mask getter method.
	 *
	 * @return  link IP mask
	 */
	public String getIpMask();

	/**
	 * @brief  IP mask setter method.
	 *
	 * @param  ipMask  IP mask
	 */
	public void setIpMask( String ipMask );

	/**
	 * @brief  Plumbed getter method.
	 *
	 * @return  true iff the link is plumbed
	 */
	public boolean isPlumbed();

	/**
	 * @brief  Plumbed setter method.
	 *
	 * @param  plumbed  true iff the link is plumbed
	 */
	public void setPlumbed( boolean plumbed );

	/**
	 * @brief  Up getter method.
	 *
	 * @return  true iff the link is up
	 */
	public boolean isUp();

	/**
	 * @brief  Up setter method.
	 *
	 * @param  up  true iff the link is up
	 */
	public void setUp( boolean up );

}
