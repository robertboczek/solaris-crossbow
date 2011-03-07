package org.jims.modules.crossbow.gui.statistics;

import java.util.UUID;

public interface StatisticGatherer {

	/**
	 * Returns statistics of Endpoint with equal UUID
	 * @param endpointUUID
	 * @return CrossbowStatistics object with equal UUID or null if no Endpoint object with endpointUUID was found
	 */
	public CrossbowStatistics getEndpointStatistics(UUID endpointUUID);
	
	/**
	 * Returns statistics of Flow with equal UUID
	 * @param endpointUUID
	 * @return CrossbowStatistics object with equal UUID or null if no Flow object with endpointUUID was found
	 */
	public CrossbowStatistics getFlowStatistics(UUID flowUUID);
}
