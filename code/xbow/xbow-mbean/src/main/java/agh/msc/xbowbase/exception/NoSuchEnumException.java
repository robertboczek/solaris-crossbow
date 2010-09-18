package agh.msc.xbowbase.exception;


/**
 * Exception thrown if specified enum cannot be found (e.g. while mapping).
 *
 * @author cieplik
 */
public class NoSuchEnumException extends XbowException {

	/**
	 * Creates the exception object and sets message to s.
	 *
	 * @param  s  message
	 */
	public NoSuchEnumException( String s ) {
		super( s );
	}

}
