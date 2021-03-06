package org.jims.modules.crossbow.exception;

import java.io.Serializable;


/**
 * General Xbow exception.
 *
 * @author cieplik
 */
public class XbowException extends Exception implements Serializable {

	/**
	 * Creates new exception object and sets message to s.
	 *
	 * @param  s  message
	 */
	public XbowException( String s ) {
		super( s );
	}

}
