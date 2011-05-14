package org.jims.modules.crossbow.vlan;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.lib.VlanHelper;
import org.jims.modules.crossbow.manager.BaseManager;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import org.jims.modules.crossbow.vlan.util.VlanToVlanInfoTranslator;


/**
 *
 * @author cieplik
 */
public class VlanManager extends BaseManager< VlanMBean > implements VlanManagerMBean, NotificationListener {

	@Override
	public void create( VlanMBean vlan ) throws XbowException {

		vlanHelper.create( VlanToVlanInfoTranslator.translate( vlan ) );

		logger.info( "New VLAN created (name: " + vlan.getName()
		             + ", link: " + vlan.getLink()
		             + ", tag: " + vlan.getTag() + ")." );

	}


	@Override
	public void create( String name, String link, int tag ) throws XbowException {
		create( new Vlan( name, link, tag ) );
	}


	@Override
	public void remove( String name ) {

		vlanHelper.remove( name );

		logger.info( "VLAN removed (name: " + name + ")." );

		if ( null != publisher ) {

			try {

				synchronized ( publisher ) {
					publisher.unpublish( name );
				}

				logger.info( "VLAN unpublished (name: " + name + ")" );

			} catch ( NotPublishedException ex ) {
				logger.error( "Error while unpublishing VLAN (name: " + name + ").", ex );
			}

		}

	}


	@Override
	public List< String > getVlans() {

		List< VlanInfo > vlanInfos = vlanHelper.getVlanInfos();
		List< String > res = new LinkedList< String >();

		for ( VlanInfo info : vlanInfos ) {
			res.add( info.getName() );
		}

		return res;

	}


	@Override
	public VlanMBean getByName( String name ) {

		VlanMBean res = null;

		for ( VlanMBean vlan : publisher.getPublished() ) {
			if ( name.equals( vlan.getName() ) ) {
				res = vlan;
				break;
			}
		}

		return res;

	}


	@Override
	public void discover() {

		if ( null != publisher ) {

			synchronized ( publisher ) {

				List< VlanInfo > vlanInfos = vlanHelper.getVlanInfos();

				logger.debug( vlanInfos.size() + " VLAN(s) discovered.");

				for ( VlanInfo info : vlanInfos ) {

					Vlan vlan = VlanToVlanInfoTranslator.translate( info );
					vlan.setVlanHelper( vlanHelper );

					publisher.publish( vlan );

				}

				// Unpublish VLANs not present any more.

				Set< String > published = new HashSet< String >();
				for ( Object vlan : publisher.getPublished() ) {
					published.add( ( ( VlanMBean ) vlan ).getName() );
				}

				Set< String > discovered = new HashSet< String >();
				for ( VlanInfo info : vlanInfos ) {
					discovered.add( info.getName() );
				}

				published.removeAll( discovered );
				for ( Object vlanName : published ) {

					try {
						publisher.unpublish( vlanName );
					} catch ( NotPublishedException e ) {
						logger.fatal( "Error while removing stale VLANs.", e );
					}

				}

			}

		}

	}


	@Override
	public void handleNotification( Notification notification, Object handback ) {
		discover();
	}


	public void setVlanHelper( VlanHelper vlanHelper ) {
		this.vlanHelper = vlanHelper;
	}


	private VlanHelper vlanHelper;

	private final static Logger logger = Logger.getLogger( VlanManager.class );

}
