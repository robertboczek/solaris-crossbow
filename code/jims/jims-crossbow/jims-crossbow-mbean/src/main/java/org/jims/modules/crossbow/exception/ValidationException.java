package org.jims.modules.crossbow.exception;


/**
 * The exception thrown after (attribute, parameter, value) validation fails.
 *
 * @author cieplik
 */
public class ValidationException extends XbowException {

	/**
	 * Creates the exception object and sets message to s.
	 *
	 * @param  s  message
	 */
	public ValidationException( String s ) {
		super( s );
	}

}
