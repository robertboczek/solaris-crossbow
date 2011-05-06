package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class Assignments implements Serializable {

	public void put( Object o, String s ) {
		assignments.put( o, s );
	}

	public void putAnnotation( Interface iface, InterfaceAssignment ass ) {
		annotations.put( iface, ass );
	}

	// public void put( Interface iface, InterfaceAssignment ass ) {
	// 		assignments.put( iface, ass );
	// 	}

	// 	public void put( Switch s, String assignment ) {
	// 		assignments.put( s, assignments );
	// 	}

	// public void put( Object o, Object s ) {
	// 	assignments.put( o, s );
	// }


	// public String get( Object o ) {
	// 	return assignments.get( o );
	// }

	public String get( Object o ) {
		return assignments.get( o );
	}

	// public String get( Switch s ) {
	// 	return ( String ) assignments.get( s );
	// }

	public InterfaceAssignment getAnnotation( Interface iface ) {
		return ( InterfaceAssignment ) annotations.get( iface );
	}


	public Object remove( Object o ) {
		return assignments.remove( o );
	}


	public List< Object > filterByTarget( String key ) {

		List< Object > res = new LinkedList< Object >();

		for ( Map.Entry< Object, String > e : assignments.entrySet() ) {
			if ( key.equals( e.getValue() ) ) {
				res.add( e.getKey() );
			}
		}

		return res;

	}


	Map< Object, String > assignments = new HashMap< Object, String >();
	Map< Object, Object > annotations = new HashMap< Object, Object >();

}
