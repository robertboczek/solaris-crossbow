package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.link.NicMBean;
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
public class NicMBeanPublisher extends MBeanPublisher< NicMBean > {

	/**
	 * @see  MBeanPublisher#MBeanPublisher( javax.management.MBeanServer )
	 */
	public NicMBeanPublisher( MBeanServer mBeanServer ) {
		super( mBeanServer );
	}


	/**
	 * @see  MBeanPublisher#identifies( java.lang.Object, java.lang.Object )
	 */
	@Override
	protected boolean identifies( Object id, NicMBean o ) {
		return o.getName().equals( o );
	}


	/**
	 * @see  MBeanPublisher#createObjectName( java.lang.Object )
	 */
	@Override
	protected ObjectName createObjectName( NicMBean object ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"Crossbow:type=Nic,name=%s",
			object.getName()
		) );

	}


	private static final Logger logger = Logger.getLogger( NicMBeanPublisher.class );

}
