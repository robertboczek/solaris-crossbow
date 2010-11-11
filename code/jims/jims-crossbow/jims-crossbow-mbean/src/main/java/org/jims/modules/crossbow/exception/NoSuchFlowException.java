package org.jims.modules.crossbow.exception;


/**
 * The exception throws when specified flow could ot be found.
 *
 * @author cieplik
 */
public class NoSuchFlowException extends XbowException {

	/**
	 * Creates the exception object and sets message to s.
	 *
	 * @param  s  message
	 */
	public NoSuchFlowException( String message ) {
		super( message );
	}

}
