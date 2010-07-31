package agh.msc.xbowbase.flow;

import java.util.Map;


/**
 *
 * @author cieplik
 */
public class FlowInfo {

	// TODO-DAWID: remove it. create converter class (looses coupling)

	public FlowInfo() {}


	public String getName() {
		return name;
	}


	public String getLink() {
		return link;
	}


	public String name, link;
	public Map< String, String > attributes, properties;
	public boolean temporary;

}
