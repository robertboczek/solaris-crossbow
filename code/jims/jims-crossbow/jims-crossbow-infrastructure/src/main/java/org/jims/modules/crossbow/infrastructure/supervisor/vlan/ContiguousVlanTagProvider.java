package org.jims.modules.crossbow.infrastructure.supervisor.vlan;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author cieplik
 */
public class ContiguousVlanTagProvider implements VlanTagProvider {

	public static interface UsedTagsProvider {
		Collection< Integer > provide();
	}


	/**
	 *
	 *
	 * @param  begin  range start, inclusive
	 * @param  end    range end, exclusive
	 */
	public ContiguousVlanTagProvider( int begin, int end, UsedTagsProvider provider ) {

		this.begin = begin;
		this.end = end;
		this.provider = provider;

		for ( int i = begin; i < end; ++i ) {
			availableTags.add( i );
		}

	}


	@Override
	public int provide() {
		return provide( false );
	}

	public int provide( boolean refresh ) {

		if ( refresh ) {
			refresh();
		}

		int tag = -1;

		if ( availableTags.size() > 0 ) {
			tag = availableTags.iterator().next();
			availableTags.remove( tag );
		}

		return tag;

	}


	@Override
	public void refresh() {
		for ( Integer tag : provider.provide() ) {
			availableTags.remove( tag );
		}
	}


	private int begin, end;
	private UsedTagsProvider provider;
	private Set< Integer > availableTags = new HashSet< Integer >();

}
