package org.jims.modules.crossbow.infrastructure;

import java.io.Serializable;


/**
 *
 * @author cieplik
 */
public class Pair < T, U > implements Serializable {

	public Pair( T first, U second ) {
		this.first = first;
		this.second = second;
	}


	public T first;
	public U second;

}
