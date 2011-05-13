package org.jims.modules.crossbow.infrastructure.gatherer;

import java.util.EnumMap;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
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
import org.jims.modules.crossbow.objectmodel.Assignments;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;


/**
 *
 * @author cieplik
 */
public class StatisticsGatherer implements StatisticsGathererMBean {

	@Override
	public Map< LinkStatistics, Long > getInterfaceStatistics( Interface iface,
	                                                           Assignments assignments ) {

		String url = assignments.get( iface );
		VNicManagerMBean vNicManager = getVNicManager( url );

		Map< LinkStatistics, Long > res = new EnumMap< LinkStatistics, Long >( LinkStatistics.class );
		Map< LinkStatistics, String > stats = null;

		try {
			stats = vNicManager.getProxyByName( NameHelper.interfaceName( iface ) ).getStatistics();
		} catch ( EntityNotFoundException ex ) {
			logger.error( "Error while getting statistics for interface (name: "
			              + NameHelper.interfaceName( iface ) + ").", ex );
		} catch ( LinkException ex ) {
			logger.error( "Error while getting statistics for interface (name: "
			              + NameHelper.interfaceName( iface ) + ").", ex );
		}

		if ( null != stats ) {
			for ( Map.Entry< LinkStatistics, String > s : stats.entrySet() ) {
				res.put( s.getKey(), Long.valueOf( s.getValue() ) );
			}
		}

		return res;

	}


	@Override
	public List< Map< LinkStatistics, Long > > getInterfacePeriodStatistics(
		Interface iface,
		LinkStatisticTimePeriod period,
		Assignments assignments
	) {

		List< Map< LinkStatistics, Long > > res = new LinkedList< Map< LinkStatistics, Long > >();

		String url = assignments.get( iface );
		VNicManagerMBean vNicManager = getVNicManager( url );

		try {
			res = vNicManager.getProxyByName( NameHelper.interfaceName( iface ) ).getStatistics( period );
		} catch ( EntityNotFoundException ex ) {
			logger.error( "Error while getting statistics for interface (name: "
			              + NameHelper.interfaceName( iface ) + ").", ex );
		}

		return res;

	}


	@Override
	public List< Map< LinkStatistics, Long > > getPolicyPeriodStatistics(
		Policy policy,
		LinkStatisticTimePeriod period,
		Assignments assignments
	) {

		Map< LinkStatistics, Long > res = new EnumMap< LinkStatistics, Long >( LinkStatistics.class );

		String url = assignments.get( policy );
		FlowManagerMBean flowManager = getFlowManager( url );

		List< Map< LinkStatistics, Long > > list = new LinkedList< Map< LinkStatistics, Long > >();

		try {

			for( Map< FlowStatistics, Long > map : flowManager.getProxyByName( NameHelper.policyName( policy ) )
			                                                  .getStatistics( period ) ) {

				Map< LinkStatistics, Long > linkMap = new EnumMap< LinkStatistics, Long >( LinkStatistics.class );
				for ( Map.Entry< FlowStatistics, Long > s : map.entrySet() ) {
					linkMap.put( convert( s.getKey() ), s.getValue() );
				}

				list.add( linkMap );

			}

		} catch ( EntityNotFoundException ex ) {

			// TODO

		}

		return list;

	}


	@Override
	public Map< LinkStatistics, Long > getPolicyStatistics( Policy policy, Assignments assignments ) {

		Map< LinkStatistics, Long > res = new EnumMap< LinkStatistics, Long >( LinkStatistics.class );

		String url = assignments.get( policy );
		FlowManagerMBean flowManager = getFlowManager( url );
		String name = NameHelper.policyName( policy );

		try {

			for ( Map.Entry<FlowStatistics, Long> s : flowManager.getProxyByName( name )
			                                                     .getStatistics().entrySet() ) {
				res.put( convert( s.getKey() ), s.getValue() );
			}

		} catch ( EntityNotFoundException ex ) {
			logger.error( "Error while getting statistics for policy (name: " + name + ")" );
		}

		return res;

	}


	public FlowManagerMBean getFlowManager( String url ) {

		FlowManagerMBean flowManager = null;
		try {
			MBeanServerConnection mbsc = JMXConnectorFactory.connect(
				new JMXServiceURL( url )
			).getMBeanServerConnection();

			flowManager = JMX.newMBeanProxy(
				mbsc, new ObjectName( "Crossbow:type=FlowManager" ), FlowManagerMBean.class
			);
		} catch ( Exception ex ) {
			logger.error( "Error while querying MBean server (url: " + url + ")", ex );
		}

		return flowManager;
	}

	public VNicManagerMBean getVNicManager( String url ) {

		VNicManagerMBean vnicManager = null;

		try {
			MBeanServerConnection mbsc = JMXConnectorFactory.connect(
				new JMXServiceURL( url )
			).getMBeanServerConnection();

			vnicManager = JMX.newMBeanProxy(
				mbsc, new ObjectName( "Crossbow:type=VNicManager" ), VNicManagerMBean.class
		);
		} catch ( Exception ex ) {
			logger.error( "Error while querying MBean server (url: " + url + ")", ex );
		}

		return vnicManager;
	}


	/*
	 * JConsole only
	 */

	@Override
	public Map< String, String > get_InterfaceStatistics( String projectId, String appliance, String resourceId, Assignments assignments  ) {

		Map< String, String > res = new HashMap< String, String >();

		Interface iface = new Interface( resourceId, projectId );

		Appliance app = new Appliance( appliance, projectId, ApplianceType.MACHINE );
		app.addInterface( iface );

		for ( Map.Entry< LinkStatistics, Long > s : getInterfaceStatistics( iface, assignments ).entrySet() ) {
			res.put( s.getKey().toString(), s.getValue().toString() );
		}

		return res;

	}


	private LinkStatistics convert( FlowStatistics flowStatistics ) {

		switch ( flowStatistics ) {
			case IPACKETS: return LinkStatistics.IPACKETS;
			case OBYTES:   return LinkStatistics.OBYTES;
			case OPACKETS: return LinkStatistics.OPACKETS;
			default:       return LinkStatistics.RBYTES;
		}

	}


	private static final Logger logger = Logger.getLogger( StatisticsGatherer.class );

}
