package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.link.Nic;
import agh.msc.xbowbase.link.NicMBean;
import agh.msc.xbowbase.publisher.exception.NotPublishedException;
import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;


/**
 * Publisher implementation.
 * Publishes objects in MBeanServer.
 *
 * @author cieplik
 */
public class NicMBeanPublisher implements Publisher {

	/**
	 * @brief  Creates NicMBeanPublisher for specified MBeanServer.
	 *
	 * @param  mBeanServer  MBean Server the MBeans will be registered in
	 */
	public NicMBeanPublisher( MBeanServer mBeanServer ) {
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
	public void publish( Object object ) {

		if ( published.contains( object ) ) {

			logger.debug( object.toString() + " already registered. Ignoring." );

		} else {

			NicMBean nicMBean = ( NicMBean ) object;

			try {

				ObjectName objectName = createObjectName( nicMBean );
				mBeanServer.registerMBean( nicMBean, objectName );
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
	 * Unregisters flow identified with name object.
	 *
	 * @param  object  flow name
	 *
	 * @see  Publisher#unpublish(java.lang.Object)
	 */
	@Override
	public void unpublish( Object object ) throws NotPublishedException {

		// Assume the object is not published.

		boolean found = false;

		if ( object instanceof String ) {

			String nicName = ( String ) object;

			for ( Object o : published ) {

				Nic nic = ( Nic ) o;

				if ( nic.getName().equals( nicName ) ) {

					found = true;

					try {

						mBeanServer.unregisterMBean( createObjectName( nic ) );
						logger.info( "Flow " + nicName + " successfully unregistered." );

					} catch ( Exception e ) {

						logger.error( "Error while unregistering flow " + nicName + " .", e );

					}

				}

			}

			if ( ! found ) {
				throw new NotPublishedException( nicName );
			}

		} else {

			logger.error( "Not an instance of String passed." );

		}

	}


	/**
	 * @see  Publisher#getPublished()
	 */
	@Override
	public List< Object > getPublished() {
		return published;
	}


	/**
	 * Creates ObjectName for given FlowMBean object.
	 *
	 * @param  flowMBean  FlowMBean object
	 *
	 * @throws  MalformedObjectNameException
	 *
	 * @return  ObjectName for flowMBean
	 */
	private ObjectName createObjectName( NicMBean flowMBean ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"agh.msc.xbowbase:type=Nic,name=%s",
			flowMBean.getName()
		) );

	}


	private MBeanServer mBeanServer = null;
	private List published = new LinkedList();

	private static final Logger logger = Logger.getLogger( NicMBeanPublisher.class );

}
