package org.jims.modules.crossbow.gui.actions;

import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;


public interface SupervisorProxyFactory {
	
	public SupervisorMBean createSupervisor();

}
