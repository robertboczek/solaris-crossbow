package org.jims.modules.crossbow.vlan;


/**
 *
 * @author cieplik
 */
public class VlanInfo {

	public VlanInfo( String link, String name, int tag ) {

		this.link = link;
		this.name = name;
		this.tag = tag;

	}


	public String getLink() {
		return link;
	}

	public void setLink( String link ) {
		this.link = link;
	}


	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}


	public int getTag() {
		return tag;
	}

	public void setTag( int tag ) {
		this.tag = tag;
	}


	private String link, name;
	private int tag;

}
