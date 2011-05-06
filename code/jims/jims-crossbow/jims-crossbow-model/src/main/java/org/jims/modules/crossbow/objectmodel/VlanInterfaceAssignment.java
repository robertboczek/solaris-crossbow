package org.jims.modules.crossbow.objectmodel;


/**
 *
 * @author cieplik
 */
public class VlanInterfaceAssignment extends InterfaceAssignment {

	public VlanInterfaceAssignment( int tag ) {
		this( null, tag, null );
	}

	public VlanInterfaceAssignment( String name, int tag, String link ) {
		this.name = name;
		this.tag = tag;
		this.link = link;
	}


	public String getLink() {
		return link;
	}

	public String getName() {
		return name;
	}

	public int getTag() {
		return tag;
	}


	int tag;
	String name, link;

}
