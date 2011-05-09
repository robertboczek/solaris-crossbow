package org.jims.modules.crossbow.infrastructure.gatherer;

import java.util.Map;
import java.util.List;

import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.Assignments;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;


public interface StatisticsGathererMBean {

	/**
	 * Returns statistics of Interface
	 *
	 * @param  iface  interface object with at least projectId, resourceId and appliance set;
	 *                the appliance has to have resourceId set
	 *
	 * @return  usage statistics for the specified interface
	 */
	public Map< LinkStatistics, Long > getInterfaceStatistics( Interface iface, Assignments assignments );

	public List<Map< LinkStatistics, Long >> getInterfacePeriodStatistics( Interface iface, LinkStatisticTimePeriod period, Assignments assignments );


	/**
	 * Returns statistics of Policy
	 *
	 * @param  policy  policy object with at least interface and name set;
	 *                 the interface has to have projectId and resourceId set
	 *
	 * @return  usage statistics for the specified interface
	 */
	public Map< LinkStatistics, Long > getPolicyStatistics( Policy policy, Assignments assignments );

	public List<Map< LinkStatistics, Long >> getPolicyPeriodStatistics( Policy policy, LinkStatisticTimePeriod period, Assignments assignments );


	public Map< String, String > get_InterfaceStatistics( String projectId, String appliance, String resourceId, Assignments assignments  );

}
