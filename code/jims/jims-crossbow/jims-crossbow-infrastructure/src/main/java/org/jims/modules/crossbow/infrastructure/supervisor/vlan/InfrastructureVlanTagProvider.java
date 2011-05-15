package org.jims.modules.crossbow.infrastructure.supervisor.vlan;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.infrastructure.supervisor.WorkerProvider;
import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;
import org.jims.modules.crossbow.util.jmx.MBeanProxyHelper;
import org.jims.modules.crossbow.vlan.VlanMBean;
import org.jims.modules.crossbow.vlan.VlanManagerMBean;
import org.jims.modules.gds.notification.WorkerNodeAddedNotification;
import org.jims.modules.gds.notification.WorkerNodeRemovedNotification;


/**
 *
 * @author cieplik
 */
public class InfrastructureVlanTagProvider implements ContiguousVlanTagProvider.UsedTagsProvider, NotificationListener {

	public InfrastructureVlanTagProvider( MBeanProxyHelper componentProxyHelper,  WorkerProvider workerProvider ) {
		this.componentProxyHelper = componentProxyHelper;
		this.workerProvider = workerProvider;
	}

	@Override
	public Collection< Integer > provide() {

		Collection< Integer > res = new HashSet< Integer >();

		synchronized ( agents ) {

			for ( String url : agents ) {

				componentProxyHelper.setUrl( url );
				VlanManagerMBean vlanManager = componentProxyHelper.createProxy( VlanManagerMBean.class );

				for ( String vlan : vlanManager.getVlans() ) {

					try {
						res.add( vlanManager.getProxyFactory( vlan, VlanMBean.class ).create().getTag() );
					} catch ( EntityNotFoundException ex ) {
						logger.error( "VLAN not found (name: " + vlan + ")." );
					}

				}

			}

		}

		logger.info( res.size() + " VLAN tag(s) have already been reserved." );

		return res;

	}

	@Override
	public void handleNotification( Notification notification, Object handback ) {

		if ( ( notification instanceof WorkerNodeAddedNotification )
		     || ( notification instanceof WorkerNodeRemovedNotification ) ) {

			synchronized ( agents ) {
				agents.clear();
				agents.addAll( workerProvider.getWorkers().keySet() );
			}

		}

	}


	Collection< Integer > usedTags;
	private final Collection< String > agents = new LinkedList< String >();
	private final MBeanProxyHelper componentProxyHelper;
	private final WorkerProvider workerProvider;

	private static final Logger logger = Logger.getLogger( InfrastructureVlanTagProvider.class );

}
