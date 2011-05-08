package org.jims.modules.crossbow.infrastructure.aspectj;

import javax.management.JMX;
import javax.management.ObjectName;
import javax.management.MBeanServer;

import org.jims.modules.crossbow.infrastructure.worker.Worker;
import org.jims.modules.crossbow.infrastructure.progress.WorkerProgressMBean;
import org.jims.modules.crossbow.infrastructure.JimsMBeanServer;

import org.jims.modules.crossbow.link.VNicManager;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.etherstub.EtherstubManager;
import org.jims.modules.crossbow.etherstub.EtherstubMBean;

import org.apache.log4j.Logger;

public aspect JimsAspect {

	private Logger log = Logger.getLogger( JimsAspect.class );

	pointcut myClass() : within( Worker );
	pointcut addMethod() : myClass() && execution(* instantiateADD(..)) ;
	pointcut remMethod() : myClass() && execution(* instantiateREM(..)) ;
	pointcut updMethod() : myClass() && execution(* instantiateUPD(..)) ;
	
	pointcut myClass2() : within(VNicManager);
	pointcut addVNic() : myClass2() && execution(* create(..)) ;
	pointcut deleteVNic() : myClass2() && execution(* delete(..)) ;

	pointcut myClass3() : within(EtherstubManager);
	pointcut addEtherstub() : myClass3() && execution(* create(..)) ;
	pointcut deleteEtherstub() : myClass3() && execution(* delete(..)) ;

	private WorkerProgressMBean workerProgressMBean;
	private MBeanServer server = null;

	before (): addMethod() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting adding new components");
		}
	}

	after (): addMethod() {

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

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		if(workerProgressMBean != null) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting updating components");
		}
	}

	after (): updMethod() {

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
			log.error("Couldn't get MBeanServer");
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

	before (): addVNic() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 1 && args[0] instanceof VNicMBean) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting creating new VNic " + ((VNicMBean)args[0]).getName());
		}
	}

	after (): addVNic() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 1 && args[0] instanceof VNicMBean) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Finished creating new VNic " + ((VNicMBean)args[0]).getName());
		}	
	}

	before (): deleteVNic() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 2 && args[0] instanceof String) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting removing new VNic " + ((String)args[0]));
		}
	}

	after (): deleteVNic() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 2 && args[0] instanceof String) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Finished removing new VNic " + ((String)args[0]));
		}	
	}
	
	before (): addEtherstub() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 1 && args[0] instanceof EtherstubMBean) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting creating new Etherstub " + ((VNicMBean)args[0]).getName());
		}
	}

	after (): addEtherstub() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 1 && args[0] instanceof EtherstubMBean) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Finished creating new Etherstub " + ((EtherstubMBean)args[0]).getName());
		}	
	}

	before (): deleteEtherstub() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 2 && args[0] instanceof String) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Starting removing new Etherstub " + ((String)args[0]));
		}
	}

	after (): deleteEtherstub() {

		if(workerProgressMBean == null) {
			getWorkerProgressMBean();
		}

		Object[] args = thisJoinPoint.getArgs();

		if(workerProgressMBean != null && args.length == 2 && args[0] instanceof String) {
			log.info("Sending notification");
			workerProgressMBean.sendLogNotification("Finished removing new Etherstub " + ((String)args[0]));
		}	
	}
}
