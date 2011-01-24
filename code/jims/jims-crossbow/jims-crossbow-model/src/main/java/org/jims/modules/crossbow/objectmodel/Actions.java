package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class Actions implements Serializable {

	public enum ACTION {

		ADD,
		REM,
		REMREC,  // recursive removal
		UPD
	}


	public void insert( Object o, ACTION a ) {
		actions.put( o, a );
	}


	public ACTION get( Object o ) {

		ACTION a = actions.get( o );
		return ( null == a ) ? ACTION.ADD : a;

	}


	private Map< Object, ACTION > actions = new HashMap< Object, ACTION >();

}
