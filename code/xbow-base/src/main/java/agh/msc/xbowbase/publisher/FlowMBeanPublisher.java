package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowMBean;
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
public class FlowMBeanPublisher implements Publisher {

	/**
	 * @brief  Creates FlowMBeanPublisher for specified MBeanServer.
	 *
	 * @param  mBeanServer  MBean Server the MBeans will be registered in
	 */
	public FlowMBeanPublisher( MBeanServer mBeanServer ) {
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

			FlowMBean flowMBean = ( FlowMBean ) object;

			try {

				ObjectName objectName = createObjectName( flowMBean );
				mBeanServer.registerMBean( flowMBean, objectName );
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

			String flowName = ( String ) object;

			for ( Object o : published ) {

				Flow flow = ( Flow ) o;

				if ( flow.getName().equals( flowName ) ) {

					found = true;

					try {

						mBeanServer.unregisterMBean( createObjectName( flow ) );
						logger.info( "Flow " + flowName + " successfully unregistered." );

					} catch ( Exception e ) {

						logger.error( "Error while unregistering flow " + flowName + " .", e );

					}

				}

			}

			if ( ! found ) {
				throw new NotPublishedException( flowName );
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
	private ObjectName createObjectName( FlowMBean flowMBean ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"agh.msc.xbowbase:type=Flow,link=%s,name=%s",
			flowMBean.getLink(), flowMBean.getName()
		) );

	}


	private MBeanServer mBeanServer = null;
	private List published = new LinkedList();

	private static final Logger logger = Logger.getLogger( FlowMBeanPublisher.class );

}
