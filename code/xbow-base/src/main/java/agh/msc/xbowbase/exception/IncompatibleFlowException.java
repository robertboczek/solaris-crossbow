package agh.msc.xbowbase.exception;


/**
 * The exception thrown if creation of a flow is impossible because of
 * incompatibilities with another existing flow.
 *
 * @author cieplik
 */
public class IncompatibleFlowException extends XbowException {

	/**
	 * Creates the exception object and sets message to s.
	 *
	 * @param  s  message
	 */
	public IncompatibleFlowException( String s ) {
		super( s );
	}

}
