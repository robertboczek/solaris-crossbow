package agh.msc.xbowbase.exception;

import java.io.Serializable;


/**
 *
 * @author cieplik
 */
public class XbowException extends Exception implements Serializable {

	public XbowException( String s ) {
		super( s );
	}

}
