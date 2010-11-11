package org.jims.modules.crossbow.publisher.exception;


/**
 * The exception thrown after trying to unpublish object that hasnt't been
 * published yet.
 *
 * @author cieplik
 */
public class NotPublishedException extends Exception {

	/**
	 * Creates exception instance with specified message.
	 *
	 * @param  message  message
	 */
	public NotPublishedException( String message ) {
		super( message );
	}


	public NotPublishedException( Object o ) {
		super( o.toString() );
	}

}
