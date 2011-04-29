package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.flow.FlowMBean;
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
public class FlowMBeanPublisher extends MBeanPublisher< FlowMBean > {

	/**
	 * @see  MBeanPublisher#MBeanPublisher( javax.management.MBeanServer )
	 */
	public FlowMBeanPublisher( MBeanServer mBeanServer ) {
		super( mBeanServer );
	}


	@Override
	protected boolean identifies( Object id, FlowMBean o ) {
		return ( ( FlowMBean ) o ).getName().equals( id );
	}


	@Override
	protected ObjectName createObjectName( FlowMBean object ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"Crossbow:type=Flow,link=%s,name=%s",
			object.getLink(), object.getName()
		) );

	}


	private static final Logger logger = Logger.getLogger( FlowMBeanPublisher.class );

}
