package org.jims.modules.crossbow.infrastructure.progress;

import java.util.List;
import java.util.LinkedList;
import java.rmi.RemoteException;

import javax.management.ListenerNotFoundException;
import javax.management.JMX;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.MBeanServerConnection;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;

import org.jims.modules.sg.service.wnservice.WNDelegateMBean;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgressMBean;
import org.jims.modules.crossbow.infrastructure.progress.notification.ProgressNotification;
import org.jims.modules.crossbow.infrastructure.progress.notification.LogNotification;
import org.jims.modules.crossbow.infrastructure.progress.notification.TaskCompletedNotification;
import org.jims.modules.crossbow.infrastructure.JimsMBeanServer;

import org.apache.log4j.Logger;


public class CrossbowNotification implements CrossbowNotificationMBean {

	private int totalTasks = Integer.MAX_VALUE;

	private static final Integer SINGLE_NODE_OPERATIONS = 3;
	private int index;

	private Logger log = Logger.getLogger( CrossbowNotification.class );

	private StringBuilder sb = new StringBuilder();
	private ProgressNotification progressNotification = null;

	private final WorkerProgressMBean workerProgress;

	private final WNDelegateMBean delegate;

	public CrossbowNotification(WorkerProgressMBean workerProgress, WNDelegateMBean delegate) {

		this.index = 0;
		this.delegate = delegate;
		this.workerProgress = workerProgress;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4845007778991652486L;

	private void registerNotificationListener() {

		log.debug("Reseting and registering at all WorkerProgressMBeans as listener");

		ObjectName workerProgressObjectName = null;
		ObjectName crossbowNotificationObjectName = null;
		try { 
			crossbowNotificationObjectName = ObjectName.getInstance( "Crossbow:type=CrossbowNotification" );
			workerProgressObjectName = ObjectName.getInstance( "Crossbow:type=WorkerProgress" );

		} catch( Exception e ) {
			log.error("Exception while creating workerProgress ObjectName", e);
			e.printStackTrace();
		}

		MBeanServer server = JimsMBeanServer.findJimsMBeanServer();

		try {

			for ( String url : delegate.scGetAllMBeanServers() ) {

				try {

					MBeanServerConnection mbsc = JMXConnectorFactory.connect(
						new JMXServiceURL( url )
					).getMBeanServerConnection();

					CrossbowNotificationMBean crossbowNotification = JMX.newMBeanProxy(
						mbsc,
						crossbowNotificationObjectName,
						CrossbowNotificationMBean.class
					);

					crossbowNotification.reset();

					try{
						mbsc.removeNotificationListener( workerProgressObjectName, crossbowNotificationObjectName );
					} catch( Exception e ) {
						log.error( "Exception while removing notification listener from MBean server (url: " + url + ")", e );
					}
					mbsc.addNotificationListener( workerProgressObjectName, crossbowNotificationObjectName, null, null);

					log.info( "CrosbowNotification successfully registered lestener at WorkerProgressMBean (url: " + url + ")" );

				} catch ( Exception ex ) {
					log.error( "Error while querying MBean server (url: " + url + ")", ex );
				}
			}
	
			progressNotification = new ProgressNotification(0, totalTasks,
				WorkerProgress.getIpAddress());

		} catch ( RemoteException ex ) {
			log.error( "Error while getting MBean servers list.", ex );
		}

	}

	@Override
	public ProgressNotification getTotalProgress() {

		ObjectName crossbowNotificationObjectName = null;
		try { 
			crossbowNotificationObjectName = ObjectName.getInstance( "Crossbow:type=CrossbowNotification" );

		} catch( Exception e ) {
			log.error("Exception while creating workerProgress ObjectName", e);
			e.printStackTrace();
		}
		
		int total = totalTasks;
		int realised = 0;
		try {

			for ( String url : delegate.scGetAllMBeanServers() ) {

				try {

					MBeanServerConnection mbsc = JMXConnectorFactory.connect(
						new JMXServiceURL( url )
					).getMBeanServerConnection();

					CrossbowNotificationMBean crossbowNotification = JMX.newMBeanProxy(
						mbsc,
						crossbowNotificationObjectName,
						CrossbowNotificationMBean.class
					);

					ProgressNotification progress = crossbowNotification.getProgress();
					realised += progress.getCurrent();
					

				} catch ( Exception ex ) {
					log.error( "Error while querying MBean server (url: " + url + ")", ex );
				}
			}
	
			progressNotification = new ProgressNotification(realised, total,
				null);

		} catch ( RemoteException ex ) {
			log.error( "Error while getting MBean servers list.", ex );
		}

		return progressNotification;
	}

	@Override
	public synchronized ProgressNotification getProgress() {

		log.info("Asking for progressNotification");
		return this.progressNotification;
	}

	public String getTotalNewLogs() {
		
		StringBuilder stringBuilder = new StringBuilder();
		ObjectName crossbowNotificationObjectName = null;
		try { 
			crossbowNotificationObjectName = ObjectName.getInstance( "Crossbow:type=CrossbowNotification" );

		} catch( Exception e ) {
			log.error("Exception while creating workerProgress ObjectName", e);
			e.printStackTrace();
		}
		
		try {

			for ( String url : delegate.scGetAllMBeanServers() ) {

				try {

					MBeanServerConnection mbsc = JMXConnectorFactory.connect(
						new JMXServiceURL( url )
					).getMBeanServerConnection();

					CrossbowNotificationMBean crossbowNotification = JMX.newMBeanProxy(
						mbsc,
						crossbowNotificationObjectName,
						CrossbowNotificationMBean.class
					);

					String logs = crossbowNotification.getNewLogs();
					if(logs != null && !logs.equals("")) {
						stringBuilder.append(crossbowNotification.getNewLogs());
						stringBuilder.append("\n");
					}

				} catch ( Exception ex ) {
					log.error( "Error while querying MBean server (url: " + url + ")", ex );
				}
			}

		} catch ( RemoteException ex ) {
			log.error( "Error while getting MBean servers list.", ex );
		}

		return stringBuilder.toString();
	}

	@Override
	public synchronized String getNewLogs() {

		log.info("Asking for logs");		
		String logs = sb.toString();
		sb = new StringBuilder();
		return logs;
	}

	private synchronized void updateLogs(String logs) {
		sb.append(logs);
		log.info("New log: " + logs);
		sb.append("\n");
	}

	private synchronized void updateProgress(ProgressNotification progressNotification) {
		this.progressNotification = progressNotification;
		log.info("Progress notification " + index + " out of " + SINGLE_NODE_OPERATIONS + " is done");
	}

	/**
	 * Jesli otrzymana notyfikacja jest o stanie procesu deploymentu to
	 * aktualizuje licznik i przekazuje notyfikacje do wszystkich listenerow
	 * postepu procesu deploymentu
	 */
	@Override
	public synchronized void handleNotification(Notification notification, Object handback) {

		Object userData = notification.getUserData();

		log.info("Received notification from WorkerProgress ");

		if (notification.getUserData() != null
				&& notification.getUserData() instanceof TaskCompletedNotification) {
			
			updateProgress(new ProgressNotification(++index, totalTasks,
					((TaskCompletedNotification) notification.getUserData())
						.getNodeIpAddress()));
			
							
		} else if(notification.getUserData() != null
				&& notification.getUserData() instanceof LogNotification) {
			
			updateLogs(((LogNotification)notification.getUserData()).getLog());
		}
	}

	@Override
	public void resetTotal(int numberOfNodes) {

		index = 0;
		sb = new StringBuilder();
		totalTasks = numberOfNodes * SINGLE_NODE_OPERATIONS;

		registerNotificationListener();

	}

	@Override
	public void reset() {

		index = 0;
		sb = new StringBuilder();

	}
}
