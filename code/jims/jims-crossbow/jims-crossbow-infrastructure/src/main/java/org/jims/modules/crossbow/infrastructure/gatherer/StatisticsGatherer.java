package org.jims.modules.crossbow.infrastructure.gatherer;

import java.util.HashMap;
import java.util.Map;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.flow.enums.FlowStatistics;
import org.jims.modules.crossbow.infrastructure.worker.NameHelper;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;


/**
 *
 * @author cieplik
 */
public class StatisticsGatherer implements StatisticsGathererMBean {

	@Override
	public Map< LinkStatistics, Long > getInterfaceStatistics( Interface iface ) {

		Map< LinkStatistics, Long > res = new HashMap< LinkStatistics, Long >();
		Map< LinkStatistics, String > stats = null;

		try {
			stats = vNicManager.getByName( NameHelper.interfaceName( iface ) ).getStatistics();
		} catch ( LinkException ex ) {
			// TODO-DAWID
		}

		if ( null != stats ) {
			for ( Map.Entry< LinkStatistics, String > s : stats.entrySet() ) {
				res.put( s.getKey(), Long.valueOf( s.getValue() ) );
			}
		}

		return res;

	}


	@Override
	public Map< LinkStatistics, Long > getPolicyStatistics( Policy policy ) {

		Map< LinkStatistics, Long > res = new HashMap< LinkStatistics, Long >();

		for ( Map.Entry< FlowStatistics, Long > s : flowManager.getByName( NameHelper.policyName( policy ) ).getStatistics().entrySet() ) {
			res.put( convert( s.getKey() ), s.getValue() );
		}

		return res;

	}


	/*
	 * JConsole only
	 */

	@Override
	public Map< String, String > get_InterfaceStatistics( String projectId, String appliance, String resourceId ) {

		Map< String, String > res = new HashMap< String, String >();

		Interface iface = new Interface( resourceId, projectId );

		Appliance app = new Appliance( appliance, projectId, ApplianceType.MACHINE );
		app.addInterface( iface );

		for ( Map.Entry< LinkStatistics, Long > s : getInterfaceStatistics( iface ).entrySet() ) {
			res.put( s.getKey().toString(), s.getValue().toString() );
		}

		return res;

	}


	public void setvNicManager( VNicManagerMBean vNicManager ) {
		this.vNicManager = vNicManager;
	}


	private LinkStatistics convert( FlowStatistics flowStatistics ) {

		switch ( flowStatistics ) {
			case IPACKETS: return LinkStatistics.IPACKETS;
			case OBYTES:   return LinkStatistics.OBYTES;
			case OPACKETS: return LinkStatistics.OPACKETS;
			default:       return LinkStatistics.RBYTES;
		}

	}


	VNicManagerMBean vNicManager;
	FlowManagerMBean flowManager;

}
