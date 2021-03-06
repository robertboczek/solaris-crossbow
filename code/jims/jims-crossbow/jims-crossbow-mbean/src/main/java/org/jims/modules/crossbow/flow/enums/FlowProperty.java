package org.jims.modules.crossbow.flow.enums;

import org.jims.modules.crossbow.exception.NoSuchEnumException;


/**
 *
 * @author cieplik
 */
public enum FlowProperty {

	MAXBW,
	PRIORITY;


	@Override
	public String toString() {

		if ( this.equals( MAXBW ) ) {
			return "maxbw";
		} else if ( this.equals( PRIORITY ) ) {
			return "priority";
		}

		return "";

	}


	public static FlowProperty fromString( String key ) throws NoSuchEnumException {

		if ( key.equals( "maxbw" ) ) {
			return MAXBW;
		} else if ( key.equals( "priority" ) ) {
			return PRIORITY;
		}

		throw new NoSuchEnumException( key );

	}

}
