package agh.msc.xbowbase.flow;


/**
 * Interface for publishers.
 *
 * @author cieplik
 */
public interface Publisher {

	/**
	 * Publishes object.
	 *
	 * @param  object  object to be published
	 */
	public void publish( Object object );

	/**
	 * Unpublishes object.
	 *
	 * @param  object  implementation-specific identifier of the object to be unregistered.
	 */
	public void unpublish( Object object );

}
