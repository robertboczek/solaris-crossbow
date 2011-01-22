package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Machine;
import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author cieplik
 */
public class WorkerIT {

	@BeforeClass
	public static void setUpClass() throws Exception {

		JMXConnector jmxc = JMXConnectorFactory.connect(
			new JMXServiceURL( "service:jmx:rmi:///jndi/rmi://127.0.0.1:7707/jims" ),
			null
		);

		connection = jmxc.getMBeanServerConnection();

		try {

			worker = JMX.newMBeanProxy(
				connection, new ObjectName( "Crossbow:type=XBowWorker" ), WorkerMBean.class
			);

		} catch ( Exception ex ) {

			logger.error( "Error while creating XBowWorker proxy", ex );

		}

	}


	@Test
	public void testSimpleModelInstantiation() {

		String machineId = "MYSQL", portId = machineId + SEP + "LINK0", switchId = "SWITCH0";
		String etherstubId = projectId + SEP + switchId;

		Machine m = new Machine( machineId, projectId );
		Port p = new Port( portId, projectId );
		Switch s = new Switch( switchId, projectId );

		m.addPort( p );
		p.setEndpoint( s );

		ObjectModel model = new ObjectModel();

		model.addPorts( p );
		model.addSwitches( s );
		model.addMachines( m );

		Assignments assignments = new Assignments();

		assignments.setAssignment( p, etherstubId );

		worker.instantiate( model, new Actions(), assignments );

	}


	private static MBeanServerConnection connection;
	private static MBeanServer server;
	private static WorkerMBean worker;
	private static final String SEP = Worker.SEP;
	private static final String projectId = "TESTPROJECT";

	private static final Logger logger = Logger.getLogger( WorkerIT.class );

}