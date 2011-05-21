package org.jims.modules.crossbow.util.struct;

import java.io.Serializable;


/**
 *
 * @author cieplik
 */
public class Pair< T, U > implements Serializable {

	public Pair( T first, U second ) {
		this.first = first;
		this.second = second;
	}


	public static < V, W > Pair< V, W > create( V first, W second ) {
		return new Pair< V, W >( first, second );
	}


	public T first;
	public U second;

}
