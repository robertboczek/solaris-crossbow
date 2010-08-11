package agh.msc.xbowbase.flow;

import java.util.Map;


/**
 * Provides information about specific flow.
 *
 * @author cieplik
 */
public class FlowInfo {

	/**
	 * @brief  Default constructor.
	 */
	public FlowInfo() {}


	/**
	 * @brief  Constructs FlowInfo with specific attributes values.
	 *
	 * @param  name        flow name
	 * @param  link        link name
	 * @param  attributes  attributes map
	 * @param  properties  properties map
	 * @param  temporary   is the flow temporary
	 */
	public FlowInfo( String name, String link,
	                 Map< String, String > attributes, Map< String, String > properties,
	                 boolean temporary ) {

		this.name = name;
		this.link = link;
		this.attributes = attributes;
		this.properties = properties;
		this.temporary = temporary;

	}


	/**
	 * @brief  Name getter method.
	 *
	 * @return  flow name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @brief  Link getter method.
	 *
	 * @return  link name
	 */
	public String getLink() {
		return link;
	}


	/**
	 * @brief  Attributes getter method.
	 *
	 * @return  flow attributes map
	 */
	public Map< String, String > getAttributes() {
		return attributes;
	}


	/**
	 * @brief  Properties getter method.
	 *
	 * @return  flow properties map
	 */
	public Map< String, String > getProperties() {
		return properties;
	}


	/**
	 * @brief  Temporary getter method.
	 *
	 * @return  true  iff the flow is temporary
	 */
	public boolean isTemporary() {
		return temporary;
	}


	private String name, link;
	private Map< String, String > attributes, properties;
	private boolean temporary;

}
