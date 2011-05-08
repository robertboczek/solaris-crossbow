package org.jims.modules.crossbow.infrastructure;


/**
 *
 * @author cieplik
 */
public class Triple < T, U, V > {

	public Triple( T first, U second, V third ) {
		this.first = first;
		this.second = second;
		this.third = third;
	}


	public T first;
	public U second;
	public V third;

}
