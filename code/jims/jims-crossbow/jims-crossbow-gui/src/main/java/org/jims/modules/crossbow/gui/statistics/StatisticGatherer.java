package org.jims.modules.crossbow.gui.statistics;

import java.util.Map;

import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

public interface StatisticGatherer {

	/**
	 * Returns statistics of Interface
	 * @param interfac
	 * @return Map<LinkStatistics, Integer>
	 */
	public Map<LinkStatistics, Integer> getInterfaceStatistics(Interface interfac);
	
	/**
	 * Returns statistics of Policy
	 * 
	 * @param Policy object
	 * @return Map<LinkStatistics, Integer>
	 */
	public Map<LinkStatistics, Integer> getFlowStatistics(Policy policy);
}
