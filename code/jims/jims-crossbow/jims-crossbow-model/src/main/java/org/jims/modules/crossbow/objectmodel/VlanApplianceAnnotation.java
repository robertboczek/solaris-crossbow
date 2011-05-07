package org.jims.modules.crossbow.objectmodel;


/**
 *
 * @author cieplik
 */
public class VlanApplianceAnnotation extends ApplianceAnnotation {

	public VlanApplianceAnnotation( int tag ) {
		this.tag = tag;
	}

	public int getTag() {
		return tag;
	}


	private int tag;

}
