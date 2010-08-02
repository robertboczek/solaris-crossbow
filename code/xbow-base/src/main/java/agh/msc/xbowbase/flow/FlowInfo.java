package agh.msc.xbowbase.flow;

import java.util.Map;


/**
 *
 * @author cieplik
 */
public class FlowInfo {

	public FlowInfo() {}


	public FlowInfo( String name, String link,
	                 Map< String, String > attributes, Map< String, String > properties,
	                 boolean temporary ) {

		this.name = name;
		this.link = link;
		this.attributes = attributes;
		this.properties = properties;
		this.temporary = temporary;

	}


	public String getName() {
		return name;
	}

	public String getLink() {
		return link;
	}

	public Map< String, String > getAttributes() {
		return attributes;
	}

	public Map< String, String > getProperties() {
		return properties;
	}

	public boolean isTemporary() {
		return temporary;
	}


	private String name, link;
	private Map< String, String > attributes, properties;
	private boolean temporary;

}
