package org.jims.modules.crossbow.publisher;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.vlan.VlanMBean;


/**
 * Publisher implementation.
 * Publishes objects in MBeanServer.
 *
 * @author cieplik
 */
public class VlanMBeanPublisher extends MBeanPublisher< VlanMBean > {

	/**
	 * @see  MBeanPublisher#MBeanPublisher( javax.management.MBeanServer )
	 */
	public VlanMBeanPublisher( MBeanServer mBeanServer ) {
		super( mBeanServer );
	}


	@Override
	protected boolean identifies( Object id, VlanMBean vlan ) {
		return vlan.getName().equals( id );
	}


	@Override
	protected ObjectName createObjectName( VlanMBean vlan ) throws MalformedObjectNameException {

		return new ObjectName( String.format(
			"Crossbow:type=Vlan,link=%s,name=%s",
			vlan.getLink(), vlan.getName()
		) );

	}


	private static final Logger logger = Logger.getLogger( VlanMBeanPublisher.class );

}
