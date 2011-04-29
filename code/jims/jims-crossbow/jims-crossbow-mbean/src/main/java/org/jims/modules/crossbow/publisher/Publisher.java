package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.List;


/**
 * Interface for publishers.
 *
 * @author cieplik
 */
public interface Publisher < T > {

	/**
	 * Publishes object.
	 *
	 * @param  object  object to be published
	 */
	public void publish( T object );

	/**
	 * Unpublishes object.
	 *
	 * @param  object  implementation-specific identifier of the object to be unregistered.
	 *
	 * @throws  NotPublishedException  if the object hasn't been registered with the publisher
	 */
	public void unpublish( Object object ) throws NotPublishedException;

	/**
	 * Returns list of published objects.
	 *
	 * @return  list of published objects
	 */
	public List< T > getPublished();

}
