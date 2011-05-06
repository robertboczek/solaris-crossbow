package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class Actions implements Serializable {

	public enum Action {
		ADD,
		REM,
		UPD,
		NOOP
	}


	public void put( Object o, Action a ) {
		actions.put( o, a );
	}


	public Map< Object, Action > getAll() {
		return actions;
	}


	public Action get( Object o ) {
		return actions.get( o );

		// Action a = actions.get( o );
		// return ( null == a ) ? Action.ADD : a;

	}


	public void remove( Object o ) {
		actions.remove( o );
	}


	public Actions filterByKeys( List< Object > objs ) {

		Actions res = new Actions();

		for ( Map.Entry< Object, Action > entry : actions.entrySet() ) {
			if ( objs.contains( entry.getKey() ) ) {
				res.put( entry.getKey(), entry.getValue() );
			}
		}

		return res;

	}


	private Map< Object, Action > actions = new HashMap< Object, Action >();

}
