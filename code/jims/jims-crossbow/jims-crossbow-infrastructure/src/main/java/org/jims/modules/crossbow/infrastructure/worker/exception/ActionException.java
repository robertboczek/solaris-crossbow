package org.jims.modules.crossbow.infrastructure.worker.exception;


/**
 *
 * @author cieplik
 */
public class ActionException extends Exception {

	public ActionException( String msg ) {
		this( msg, null );
	}

	public ActionException( String msg, Exception cause ) {
		this.msg = msg;
		this.cause = cause;
	}

	@Override
	public Exception getCause() {
		return cause;
	}

	public String getMsg() {
		return msg;
	}


	private String msg;

	private Exception cause;

}
