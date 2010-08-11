package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.link.NicMBean;
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
public class NicMBeanPublisher extends MBeanPublisher {

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
	protected boolean identifies( Object id, Object o ) {
		return ( ( NicMBean ) o ).getName().equals( o );
	}


	/**
	 * @see  MBeanPublisher#createObjectName( java.lang.Object )
	 */
	@Override
	protected ObjectName createObjectName( Object object ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"agh.msc.xbowbase:type=Nic,name=%s",
			( ( NicMBean ) object ).getName()
		) );

	}


	private static final Logger logger = Logger.getLogger( NicMBeanPublisher.class );

}
