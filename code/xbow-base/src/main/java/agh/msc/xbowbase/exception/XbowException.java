package agh.msc.xbowbase.exception;

import java.io.Serializable;


/**
 * General Xbow exception.
 *
 * @author cieplik
 */
public class XbowException extends Exception implements Serializable {

	/**
	 * Creates new exception class and sets message to s.
	 *
	 * @param  s  message
	 */
	public XbowException( String s ) {
		super( s );
	}

}
