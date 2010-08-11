package agh.msc.xbowbase.link;


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
	 */
	public NicInfo( String name ) {
		this.name = name;
	}


	/**
	 * @brief  Name getter method.
	 *
	 * @return  link name
	 */
	public String getName() {
		return name;
	}


	private String name;

}
