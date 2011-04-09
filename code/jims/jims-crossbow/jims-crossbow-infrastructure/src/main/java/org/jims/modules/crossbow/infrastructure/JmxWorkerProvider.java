package org.jims.modules.crossbow.infrastructure;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.infrastructure.supervisor.WorkerProvider;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;
import org.jims.modules.sg.service.wnservice.WNDelegateMBean;


/**
 *
 * @author cieplik
 */
public class JmxWorkerProvider implements WorkerProvider {

	public JmxWorkerProvider( WNDelegateMBean delegate ) {
		this.delegate = delegate;
	}


	@Override
	public Map< String, WorkerMBean > getWorkers() {

		Map< String, WorkerMBean > res = new HashMap< String, WorkerMBean >();

		try {

			for ( String url : delegate.scGetAllMBeanServers() ) {

				try {

					MBeanServerConnection mbsc = JMXConnectorFactory.connect(
						new JMXServiceURL( url )
					).getMBeanServerConnection();

					WorkerMBean worker = JMX.newMBeanProxy(
						mbsc,
						new ObjectName( "Crossbow:type=XBowWorker" ),
						WorkerMBean.class
					);

					res.put( url, worker );

					logger.info( "Worker MBean successfully retrieved (url: " + url + ")" );

				} catch ( Exception ex ) {
					logger.error( "Error while querying MBean server (url: " + url + ")", ex );
				}

			}

		} catch ( RemoteException ex ) {
			logger.error( "Error while getting MBean servers list.", ex );
		}

		return res;

	}


	private final WNDelegateMBean delegate;

	private static final Logger logger = Logger.getLogger( JmxWorkerProvider.class );

}
