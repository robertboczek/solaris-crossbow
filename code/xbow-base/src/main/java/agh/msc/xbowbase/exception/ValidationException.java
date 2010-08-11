package agh.msc.xbowbase.exception;


/**
 * The exception thrown after (attribute, parameter, value) validation fails.
 *
 * @author cieplik
 */
public class ValidationException extends XbowException {

	/**
	 * Creates the exception class and sets message to s.
	 *
	 * @param  s  message
	 */
	public ValidationException( String s ) {
		super( s );
	}

}
