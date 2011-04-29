package org.jims.modules.crossbow.vlan;

import org.jims.modules.crossbow.lib.VlanHelper;


/**
 *
 * @author cieplik
 */
public class Vlan implements VlanMBean {

	public Vlan( String name, String link, int tag ) {

		this.name = name;
		this.link = link;
		this.tag = tag;

	}


	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public String getLink() {
		return this.link;
	}


	@Override
	public int getTag() {
		return this.tag;
	}


	public void setVlanHelper( VlanHelper vlanHelper ) {
		this.vlanHelper = vlanHelper;
	}


	@Override
	public boolean equals( Object o ) {

		if ( this == o ) {

			return true;

		} else if ( o instanceof Vlan ) {

			Vlan vlan = ( Vlan ) o;

			return ( getName().equals( vlan.getName() )
			         && getLink().equals( vlan.getLink() )
			         && ( getTag() == vlan.getTag() ) );

		}

		return false;

	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 79 * hash + (this.link != null ? this.link.hashCode() : 0);
		hash = 79 * hash + this.tag;
		return hash;
	}


	private String name, link;
	private int tag;

	private VlanHelper vlanHelper;

}
