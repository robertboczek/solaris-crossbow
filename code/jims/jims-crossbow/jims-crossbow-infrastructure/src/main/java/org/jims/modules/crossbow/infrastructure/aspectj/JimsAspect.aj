package org.jims.modules.crossbow.infrastructure.aspectj;

import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.MBeanServer;

import org.jims.modules.crossbow.infrastructure.worker.Worker;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgressMBean;
import org.jims.modules.crossbow.infrastructure.JimsMBeanServer;

import org.apache.log4j.Logger;

public aspect JimsAspect {

	private Logger log = Logger.getLogger( JimsAspect.class );

	pointcut myClass() : within( Worker );
	pointcut addMethod() : myClass() && execution(* instantiateADD(..)) ;
	pointcut remMethod() : myClass() && execution(* instantiateREM(..)) ;
	pointcut updMethod() : myClass() && execution(* instantiateUPD(..)) ;

	private WorkerProgressMBean workerProgressMBean;
	private MBeanServer server = null;

	before (): addMethod() {
		System.out.println("Before");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting adding new components");
		}
	}

	after (): addMethod() {
		System.out.println("After");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendTaskCompletedNotification();
			workerProgressMBean.sendLogNotification("Finished adding new components");
		}
	}

	before (): remMethod() {
		System.out.println("Before");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting removing components");
		}
	}

	after (): remMethod() {
		System.out.println("After");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendTaskCompletedNotification();
			workerProgressMBean.sendLogNotification("Finished removing components");
		}
	}

	before (): updMethod() {
		System.out.println("Before");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting updating components");
		}
	}

	after (): updMethod() {
		System.out.println("After");

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendTaskCompletedNotification();
			workerProgressMBean.sendLogNotification("Finished updating components");
		}
	}

	public WorkerProgressMBean getWorkerProgressMBean() {

		if(this.workerProgressMBean != null) {
			return this.workerProgressMBean;
		}

		MBeanServer server = JimsMBeanServer.findJimsMBeanServer();

		if(server == null) {
			return null;
		}

		try{

			this.workerProgressMBean = JMX.newMBeanProxy(
				server, new ObjectName( "Crossbow:type=WorkerProgress" ), WorkerProgressMBean.class
			);
		}catch(Exception e) {
			log.error("Exception while getting WorkerProgressMBean");
		}

		return this.workerProgressMBean;
	}

}
