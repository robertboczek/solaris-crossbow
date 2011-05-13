package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.LinkedList;
import java.util.List;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;


/**
 *
 * @author cieplik
 */
public abstract class MBeanPublisher < T > implements Publisher< T > {

	/**
	 * @brief  Creates MBeanPublisher for specified MBeanServer.
	 *
	 * @param  mBeanServer  MBean Server the MBeans will be registered in
	 */
	public MBeanPublisher( MBeanServer mBeanServer ) {
		this.mBeanServer = mBeanServer;
	}


	/**
	 * @brief  Registers object in MBean Server.
	 *
	 * @param  object  object to be registered
	 *
	 * @see  Publisher#publish(java.lang.Object)
	 */
	@Override
	public void publish( T object ) {

		if ( published.contains( object ) ) {

			logger.debug( object.toString() + " already registered. Ignoring." );

		} else {

			try {

				ObjectName objectName = createObjectName( object );
				mBeanServer.registerMBean( object, objectName );
				published.add( object );

				logger.info( object.toString() + " successfully registered with name == "
				             + objectName.getCanonicalName() + " ." );

			} catch ( Exception e ) {

				logger.error( "Error while registering " + object.toString() + " .", e );

			}

		}

	}


	/**
	 * @brief  Unpublishes object.
	 *
	 * Unregisters object identified with id.
	 *
	 * @param  id  registered object identifier
	 *
	 * @see  Publisher#unpublish(java.lang.Object)
	 */
	@Override
	public void unpublish( Object id ) throws NotPublishedException {

		// Assume the object is not published.

		boolean found = false;

		for ( T o : published ) {

			if ( identifies( id, o )  ) {

				found = true;

				try {

					mBeanServer.unregisterMBean( createObjectName( o ) );
					published.remove( o );

					logger.info( "Object " + o + " successfully unregistered." );

					break;

				} catch ( Exception e ) {

					logger.error( "Error while unregistering object " + o + " .", e );

				}

			}

		}

		if ( ! found ) {
			throw new NotPublishedException( id );
		}

	}


	/**
	 * @see  Publisher#getPublished()
	 */
	@Override
	public List< T > getPublished() {
		return published;
	}


	/**
	 * @see  Publisher#getProxy(java.lang.Object)
	 */
	@Override
	public T getProxy( Object id ) throws NotPublishedException {

		for ( T object : published ) {

			if ( identifies( id, object ) ) {

				try {
					return ( T ) JMX.newMBeanProxy( mBeanServer, createObjectName( object ), Object.class );
				} catch ( MalformedObjectNameException ex ) {
					break;
				}

			}

		}

		throw new NotPublishedException( "Object not published (id: " + id + ")" );
	
	}


	/**
	 * Creates ObjectName for given object.
	 *
	 * @param  object  object to register
	 *
	 * @throws  MalformedObjectNameException
	 *
	 * @return  ObjectName for object
	 */
	protected abstract ObjectName createObjectName( T object ) throws MalformedObjectNameException;

	protected abstract boolean identifies( Object id, T o );


	private MBeanServer mBeanServer = null;
	private List< T > published = new LinkedList< T >();

	private static final Logger logger = Logger.getLogger( MBeanPublisher.class );

}
