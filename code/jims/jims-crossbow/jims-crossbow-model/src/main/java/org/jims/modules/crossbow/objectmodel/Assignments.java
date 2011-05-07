package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;


/**
 *
 * @author cieplik
 */
public class Assignments implements Serializable {

	public void put( Object o, String s ) {
		assignments.put( o, s );
	}

	public String get( Object o ) {
		return assignments.get( o );
	}


	public void putAnnotation( Interface iface, InterfaceAssignment ass ) {
		annotations.put( iface, ass );
	}

	public void putAnnotation( Appliance app, ApplianceAnnotation applianceAnnotation  ) {
		annotations.put( app, applianceAnnotation );
	}

	public InterfaceAssignment getAnnotation( Interface iface ) {
		return ( InterfaceAssignment ) annotations.get( iface );
	}

	public ApplianceAnnotation getAnnotation( Appliance app ) {
		return ( ApplianceAnnotation ) annotations.get( app );
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
