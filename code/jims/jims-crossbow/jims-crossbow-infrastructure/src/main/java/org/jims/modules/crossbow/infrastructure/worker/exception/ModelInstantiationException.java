package org.jims.modules.crossbow.infrastructure.worker.exception;


/**
 *
 * @author cieplik
 */
public class ModelInstantiationException extends Exception {

	public ModelInstantiationException( Exception cause ) {
		this.cause = cause;
	}

	@Override
	public Exception getCause() {
		return cause;
	}


	private Exception cause;

}
