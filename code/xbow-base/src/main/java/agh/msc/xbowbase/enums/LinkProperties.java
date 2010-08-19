package agh.msc.xbowbase.enums;

import java.util.HashMap;

/**
 * Possible link properties to read or set
 * @author robert boczek
 */
public enum LinkProperties {
    	MAXBW, /** Maximum bandwidth*/
	LEARN_LIMIT, /** Number of MAC ADDRESSES THIS LINK CAN LEARN */
	CPUS, /** Names of processors that can perform operations for this link */
	PRIORITY; /** Priority of this link */


	/*
	 * jconsole only
	 */

	public static LinkProperties fromString( String key ) {

		HashMap< String, LinkProperties > mapping = new HashMap< String, LinkProperties >();

		mapping.put( "maxbw", MAXBW );
		mapping.put( "learn_limit", LEARN_LIMIT );
		mapping.put( "cpus", CPUS );
		mapping.put( "priority", PRIORITY );

		return mapping.get( key );

	}

}
