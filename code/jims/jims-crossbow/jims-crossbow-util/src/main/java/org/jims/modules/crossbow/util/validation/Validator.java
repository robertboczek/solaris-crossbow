package org.jims.modules.crossbow.util.validation;


/**
 *
 * @author cieplik
 */
public interface Validator< T > {

	boolean isValid( T object );

}
