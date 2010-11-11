package org.jims.modules.crossbow.link;


/**
 * Provides information about specific link.
 *
 * @author cieplik
 */
public class NicInfo {

	/**
	 * @brief  Constructs NicInfo with specific attributes' values.
	 *
	 * @param  name  link name
	 * @param  up    true iff the link is up
	 */
	public NicInfo( String name, boolean up ) {
		this.name = name;
		this.up = up;
	}


	/**
	 * @brief  Name getter method.
	 *
	 * @return  link name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @brief  Up getter method.
	 *
	 * @return  true iff the link is up
	 */
	public boolean isUp() {
		return up;
	}


	private String name;
	private boolean up;

}
