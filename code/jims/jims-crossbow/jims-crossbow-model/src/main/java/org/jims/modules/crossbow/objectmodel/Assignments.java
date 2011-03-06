package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class Assignments implements Serializable {

	public void setAssignment( Object o, String s ) {
		assignments.put( o, s );
	}


	public String getAssignment( Object o ) {
		return assignments.get( o );
	}


	Map< Object, String > assignments = new HashMap< Object, String >();

}
