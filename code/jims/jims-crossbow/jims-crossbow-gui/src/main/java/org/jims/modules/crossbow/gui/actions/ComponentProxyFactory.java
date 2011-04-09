package org.jims.modules.crossbow.gui.actions;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManagerMBean;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGathererMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;

public class ComponentProxyFactory {

	public SupervisorMBean createSupervisor() {

		if (refreshMBeanServerConnection()) {

			try {

				supervisor = JMX.newMBeanProxy(mbsc, new ObjectName(
						"Crossbow:type=Supervisor"), SupervisorMBean.class);

			} catch (Exception e) {

				// TODO log here

			}

		}

		return supervisor;
	}

	public RepoManagerMBean createRepoManager() {

		if (refreshMBeanServerConnection()) {

			try {

				repoManager = JMX.newMBeanProxy(mbsc, new ObjectName(
						"Crossbow:type=RepoManager"), RepoManagerMBean.class);

			} catch (Exception e) {

				// TODO log here

			}

		}

		return repoManager;
	}

	public CrossbowNotificationMBean createCrossbowNotification() {

		if (refreshMBeanServerConnection()) {

			try {

				crossbowNotificationMBean = JMX.newMBeanProxy(mbsc,
						new ObjectName("Crossbow:type=CrossbowNotification"),
						CrossbowNotificationMBean.class);

			} catch (Exception e) {

				// TODO log here

			}
		}
		return crossbowNotificationMBean;
	}
	
	public StatisticsGathererMBean createStatisticAnalyzer() {

		if (refreshMBeanServerConnection()) {

			try {

				statisticsGathererMBean = JMX.newMBeanProxy(mbsc,
						new ObjectName("Crossbow:type=StatisticsGatherer"),
						StatisticsGathererMBean.class);

			} catch (Exception e) {

				// TODO log here

			}
		}
		return statisticsGathererMBean;
	}

	private boolean refreshMBeanServerConnection() {

		boolean actuallyRefreshed = false;

		if ((!ruMbServer.equals(mbServer)) || (!ruMbPort.equals(mbPort))) {

			// JMX address or port changed. Refresh server connection.

			jmxConnector = new JmxConnector(mbServer, Integer.parseInt(mbPort));

			try {
				mbsc = jmxConnector.getMBeanServerConnection();
			} catch (Exception e) {
				
				logger.error("Couldn't get MBeanServerConnection");
				e.printStackTrace();

			}

			actuallyRefreshed = true;

		}

		return actuallyRefreshed;

	}

	public String getMbServer() {
		return mbServer;
	}

	public void setMbServer(String mbServer) {
		this.mbServer = mbServer;
	}

	public String getMbPort() {
		return mbPort;
	}

	public void setMbPort(String mbPort) {
		this.mbPort = mbPort;
	}
	
	public JmxConnector getJmxConnector() {
		return this.jmxConnector;
	}

	private String mbServer = "", mbPort = "";
	private String ruMbServer = "", ruMbPort = ""; // Recently used values

	private JmxConnector jmxConnector;
	private MBeanServerConnection mbsc;

	private SupervisorMBean supervisor;
	private RepoManagerMBean repoManager;
	private CrossbowNotificationMBean crossbowNotificationMBean;
	private StatisticsGathererMBean statisticsGathererMBean;

	private static final Logger logger = Logger
			.getLogger(ComponentProxyFactory.class);

}
