package agh.msc.xbowbase.publisher;

import agh.msc.xbowbase.flow.FlowMBean;
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
public class FlowMBeanPublisher extends MBeanPublisher {

	/**
	 * @see  MBeanPublisher#MBeanPublisher( javax.management.MBeanServer )
	 */
	public FlowMBeanPublisher( MBeanServer mBeanServer ) {
		super( mBeanServer );
	}


	@Override
	protected boolean identifies( Object id, Object o ) {
		return ( ( FlowMBean ) o ).getName().equals( id );
	}


	@Override
	protected ObjectName createObjectName( Object object ) throws MalformedObjectNameException {

		FlowMBean flowMBean = ( FlowMBean ) object;

		return new ObjectName( String.format(
			"agh.msc.xbowbase:type=Flow,link=%s,name=%s",
			flowMBean.getLink(), flowMBean.getName()
		) );

	}


	private static final Logger logger = Logger.getLogger( FlowMBeanPublisher.class );

}
