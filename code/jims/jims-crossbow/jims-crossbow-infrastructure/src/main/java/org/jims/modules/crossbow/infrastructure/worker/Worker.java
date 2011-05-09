 package org.jims.modules.crossbow.infrastructure.worker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jims.agent.exception.CommandException;
import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.etherstub.Etherstub;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.NoSuchFlowException;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.Flow;
import org.jims.modules.crossbow.flow.FlowMBean;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.infrastructure.Pair;
import org.jims.modules.crossbow.infrastructure.worker.exception.ActionException;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.link.VNic;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.InterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.VlanInterfaceAssignment;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.filters.Filter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.PortFilter;
import org.jims.modules.crossbow.objectmodel.filters.TransportFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.vlan.VlanMBean;
import org.jims.modules.crossbow.vlan.VlanManagerMBean;
import org.jims.modules.solaris.commands.CreateZoneFromSnapshotCommand;
import org.jims.modules.solaris.commands.ModifyZoneCommand;
import org.jims.modules.solaris.commands.RemoveZoneCommand;
import org.jims.modules.solaris.commands.SolarisCommandFactory;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneManagementMBean;


/**
 *
 * @author cieplik
 */
public class Worker implements WorkerMBean {

	// TODO mechanizm wycofywania zmian w przypadku bledu

	public Worker( VNicManagerMBean vNicManager, EtherstubManagerMBean etherstubManager,
	               FlowManagerMBean flowManager, VlanManagerMBean vlanManager,
	               GlobalZoneManagementMBean globalZoneManagement,
	               SolarisCommandFactory commandFactory ) {

		this.vNicManager = vNicManager;
		this.etherstubManager = etherstubManager;
		this.flowManager = flowManager;
		this.vlanManager = vlanManager;
		this.globalZoneManagement = globalZoneManagement;

		this.commandFactory = commandFactory;

	}


	@Override
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) throws ModelInstantiationException {

		logger.info( "Instantiating new model." );

		Map< Object, Actions.Action > actionsMap = actions.getAll();

		try {

			instantiateREM( extractByAction( actionsMap, Actions.Action.REM ) );
			instantiateADD( extractByAction( actionsMap, Actions.Action.ADD ), assignments );
			instantiateUPD( extractByAction( actionsMap, Actions.Action.UPD ) );

		} catch ( ActionException ex ) {

			throw new ModelInstantiationException( ex );

		}

		logger.info( "Instantiation completed successfully." );

	}


	private void instantiateREM( List< Object > resources ) throws ActionException {

		logger.info( "Removing (REM) resources." );

		appliancesREM( extractByType( resources, Appliance.class ) );

		policiesREM( extractByType( resources, Policy.class ) );
		interfacesREM( extractByType( resources, Interface.class ) );
		switchesREM( extractByType( resources, Switch.class ) );

		logger.info( "Resources removal (REM) finished." );

	}


	private void instantiateADD( List< Object > resources, Assignments assignments ) throws ActionException {

		logger.info( "Creating (ADD) resources." );

		switchesADD( extractByType( resources, Switch.class ) );
		interfacesADD( extractByType( resources, Interface.class ), assignments );
		policiesADD( extractByType( resources, Policy.class ) );

		appliancesADD( extractByType( resources, Appliance.class ) );

		logger.info( "Resources creation (ADD) finished." );

	}


	private void instantiateUPD( List< Object > resources ) {

		// policiesUPD( extractByType( resources, Policy.class ) );
		// portsUPD( extractByType( resources, Interface.class ) );
		// switchesUPD( extractByType( resources, Switch.class ) );

		// machinesUPD( extractByType( resources, Machine.class ) );

	}


	private List< Object > extractByAction( Map< Object, Actions.Action > actionsMap, Actions.Action action ) {

		List< Object > res = new LinkedList< Object >();

		for ( Map.Entry< Object, Actions.Action > entry : actionsMap.entrySet() ) {

			if ( action.equals( entry.getValue() ) ) {
				res.add( entry.getKey() );
			}

		}

		return res;

	}


	private < T > List< T > extractByType( List< Object > resources, Class< T > type ) {

		List< T > res = new LinkedList< T >();

		for ( Object resource : resources ) {
			if ( type.isInstance( resource ) ) {
				res.add( ( T ) resource );
			}
		}

		return res;
	
	}


	@Override
	public Map< String, Pair< ObjectModel, Assignments > > discover() {

		logger.info( "Discovering instantiated projects." );

		Map< String, Pair< ObjectModel, Assignments > > res = new HashMap< String, Pair< ObjectModel, Assignments > >();

		Map< String, Object > ids = new HashMap< String, Object >();
		Map< String, Assignments > assignments = new HashMap< String, Assignments >();

		Map< String, List< Appliance > > apps = discoverAppliances( ids );
		Map< String, List< Switch > > switches = discoverSwitches( ids );
		Map< String, List< Interface > > ifaces = discoverInterfaces( ids, assignments );
		Map< String, List< Policy > > policies = discoverPolicies( ids );

		Set< String > projects = new HashSet< String >();

		for ( Set s : new Set[] { apps.keySet(), switches.keySet(), ifaces.keySet() } ) {
			projects.addAll( s );
		}

		for ( String project : projects ) {

			ObjectModel om = new ObjectModel();

			res.put( project, new Pair< ObjectModel, Assignments >( om, assignments.get( project ) ) );

			for ( List l : new List[] { apps.get( project ),
			                            switches.get( project ),
			                            ifaces.get( project ),
			                            policies.get( project ) } ) {

				if ( null != l ) {
					for ( Object entity : l ) {
						om.register( entity );
					}
				}

			}

		}

		logger.info( "Discovered " + projects.size() + " project(s)." );

		return res;

	}


	private Map< String, List< Appliance > > discoverAppliances( Map< String, Object > ids ) {

		Map< String, List< Appliance > > res = new HashMap< String, List< Appliance > >();
		List< String > zones = globalZoneManagement.getZones();

		for ( Matcher m : filterNames( zones, NameHelper.REG_APPLIANCE_NAME_CG ) ) {

			String project = m.group( 1 ), type = m.group( 2 ), name = m.group( 3 );

			ApplianceType appType = NameHelper.ROUTER.equals( type ) ? ApplianceType.ROUTER : ApplianceType.MACHINE;

			logger.info( "Found a " + appType + " (name: " + name + ", project: " + project + ")" );

			Appliance app = new Appliance( name, project, appType, "dummy" );  // TODO-DAWID  repo id!

			if ( ! res.containsKey( project ) ) {
				res.put( project, new LinkedList< Appliance >() );
			}

			res.get( project ).add( app );
			ids.put( m.group(), app );

		}

		return res;

	}


	private Map< String, List< Switch > > discoverSwitches( Map< String, Object > ids ) {

		Map< String, List< Switch > > res = new HashMap< String, List< Switch > >();

		try {

			for ( Matcher m : filterNames( etherstubManager.getEtherstubsNames(), NameHelper.REG_SWITCH_NAME_CG ) ) {

				String project = m.group( 1 ), name = m.group( 2 );

				logger.info( "Found a switch (name: " + name + ", project: " + project + ")" );

				Switch s = new Switch( name, project );

				if ( ! res.containsKey( project ) ) {
					res.put( project, new LinkedList< Switch >() );
				}

				res.get( project ).add( s );
				ids.put( m.group(), s );

			}

		} catch ( EtherstubException e ) {
		}

		return res;

	}


	private Map< String, List< Interface > > discoverInterfaces( Map< String, Object > ids,
	                                                             Map< String, Assignments > assignments ) {

		Map< String, List< Interface > > res = new HashMap< String, List< Interface > >();

		try {

			for ( Matcher m : filterNames( vNicManager.getVNicsNames(), NameHelper.REG_INTERFACE_NAME_CG ) ) {

				String project = m.group( 1 );

				logger.info( "Found an interface (name: " + m.group( 4 ) + ", project: " + project + ")" );

				// Basic setup.

				Interface iface = new Interface( m.group( 4 ), m.group( 1 ) );

				iface.setIpAddress( new IpAddress( "1.1.1.1", 24 ) );

				// Discover details.

				VNicMBean vnic = vNicManager.getByName( m.group() );

				iface.setEndpoint( ( Switch ) ids.get( vnic.getParent() ) );
				
				Appliance app = ( Appliance ) ids.get( NameHelper.extractAppliance( m.group() ) );

				if ( null != app ) {

					if ( ! res.containsKey( project ) ) {
						res.put( project, new LinkedList< Interface >() );
					}

					res.get( project ).add( iface );
					ids.put( m.group(), iface );

					// Attach the interface to an appliance.

					app.addInterface( iface );

				} else {
					logger.warn( "Ignoring dangling interface (name: " + m.group() + ")." );
				}

			}

			// Now, discover router-internal (VLAN) interfaces.

			for ( Matcher m : filterNames( vlanManager.getVlans(), NameHelper.REG_INTERFACE_NAME_CG ) ) {

				String project = m.group( 1 );

				logger.info( "Found a VLAN interface (name: " + m.group( 4 ) + ", project: " + project + ")" );

				Interface iface = new Interface( m.group( 4 ), project );

				VlanMBean vlan = vlanManager.getByName( m.group() );

				if ( ! assignments.containsKey( project ) ) {
					assignments.put( project, new Assignments() );
				}

				assignments.get( project ).putAnnotation( iface, new VlanInterfaceAssignment( vlan.getTag() ) );

				Appliance app = ( Appliance ) ids.get( NameHelper.extractAppliance( m.group() ) );

				if ( null != app ) {

					if ( ! res.containsKey( project ) ) {
						res.put( project, new LinkedList< Interface >() );
					}

					app.addInterface( iface );

				} else {
					logger.warn( "Ignoring dangling VLAN interface (name: " + m.group() + ")." );
				}

			}

		} catch ( LinkException ex ) {
		}

		return res;

	}


	private Map< String, List< Policy > > discoverPolicies( Map< String, Object > ids ) {

		Map< String, List< Policy > > res = new HashMap< String, List< Policy > >();

		try {

			for ( Matcher m : filterNames( flowManager.getFlows(), NameHelper.REG_POLICY_NAME_CG ) ) {

				String name = m.group();

				FlowMBean flow = flowManager.getByName( name );

				Map< FlowAttribute, String > attrs = flow.getAttributes();

				Filter filter = null;

				if ( attrs.containsKey( FlowAttribute.LOCAL_PORT )
				     || attrs.containsKey( FlowAttribute.REMOTE_PORT ) ) {

					// We've found PortFilter

					boolean isLocal = attrs.containsKey( FlowAttribute.LOCAL_PORT );
					PortFilter.Protocol proto = PortFilter.Protocol.valueOf( attrs.get( FlowAttribute.TRANSPORT ).toUpperCase() );
					int port = Integer.parseInt( attrs.get( isLocal ? FlowAttribute.LOCAL_PORT
					                                                : FlowAttribute.REMOTE_PORT ) );

					filter = new PortFilter(
						proto,
						port,
						isLocal ? Filter.Location.LOCAL : Filter.Location.REMOTE
					);

				} else if ( attrs.containsKey( FlowAttribute.TRANSPORT ) ) {

					// We have a TransportFilter

					filter = new TransportFilter(
						TransportFilter.Transport.valueOf( attrs.get( FlowAttribute.TRANSPORT ).toUpperCase() )
					);

				} else if ( attrs.containsKey( FlowAttribute.LOCAL_IP )
				            || attrs.containsKey( FlowAttribute.REMOTE_IP ) ) {

					// IpFilter

					boolean isLocal = attrs.containsKey( FlowAttribute.LOCAL_IP );
					String s = attrs.get( isLocal ? FlowAttribute.LOCAL_IP : FlowAttribute.REMOTE_IP );
					IpAddress addr = IpAddress.fromString( s );

					if ( null == addr ) {

						logger.error( "Could not parse address (address: " + s + ")" );

					} else {

						filter = new IpFilter(
							addr,
							isLocal ? IpFilter.Location.LOCAL : IpFilter.Location.REMOTE
						);

					}

				} else {

					// Unknown filter type.

					logger.error( "Unknown filter type." );

				}

				if ( null != filter ) {

					// We have retrieved the filter. Gonna get the policy now.

					Policy policy = null;
					Map< FlowProperty, String > props = flow.getProperties();

					String policyName = m.group( 5 );

					String maxbw;
					if ( ( null != ( maxbw = props.get( FlowProperty.MAXBW ) ) )
					     && ( ! "".equals( maxbw.trim() ) ) ) {

						policy = new BandwidthPolicy( policyName, Integer.parseInt( maxbw.trim() ), filter );

					}

					String priority;
					if ( ( null != ( priority = props.get( FlowProperty.PRIORITY ) ) )
					     && ( ! "".equals( priority ) ) ) {

						policy = new PriorityPolicy(
							policyName,
							PriorityPolicy.Priority.valueOf( priority.toUpperCase() ),
							filter
						);

					}

					if ( null != policy ) {

						Interface iface = ( Interface ) ids.get( NameHelper.extractInterface( m.group() ) );
							
						if ( null != iface ) {

							// Attach the policy to the interface.

							iface.addPolicy( policy );

							String project = m.group( 1 );

							if ( ! res.containsKey( project ) ) {
								res.put( project, new LinkedList< Policy >() );
							}

							res.get( project ).add( policy );
							ids.put( m.group( 0 ), policy );

						} else {

							logger.warn( "Could not find parent interface for policy (name: "
							             + m.group() + ")" );

						}

					}

				}

			}

		} catch ( NoSuchFlowException ex ) {
		}

		return res;

	}


	private List< Matcher > filterNames( Collection< String > names, String regexp ) {

		List< Matcher > res = new LinkedList< Matcher >();
		Pattern p = Pattern.compile( regexp );

		for ( String name : names ) {

			Matcher m = p.matcher( name );

			if ( m.matches() ) {
				res.add( m );
			}

		}

		return res;

	}


	private void policiesREM( List< Policy > policies ) throws ActionException {

		for ( Policy policy : policies ) {

			logger.info( "Removing (REM) policy (name: " + policy.getName() + ")." );

			if ( policy.getFilter() instanceof AnyFilter ) {

				// Modify underlying VNIC.

				try {

					VNicMBean vnic = vNicManager.getByName( NameHelper.interfaceName( policy.getInterface() ) );

					LinkProperties property = null;

					if ( policy instanceof PriorityPolicy ) {
						property = LinkProperties.PRIORITY;
					} else if ( policy instanceof BandwidthPolicy ) {
						property = LinkProperties.MAXBW;
					}

					assert ( null != property ) : "Unknown Policy type";

					vnic.resetProperty( property );

				} catch ( LinkException ex ) {

					throw new ActionException( "Policy REM error", ex );

				}

			} else {

				try {
					flowManager.remove( NameHelper.policyName( policy ), TEMPORARY );
				} catch ( XbowException ex ) {
					throw new ActionException( "Policy REM error", ex );
				}

			}

		}

	}


	private void policiesADD( List< Policy > policies ) throws ActionException {

		for ( Policy p : policies ) {

			logger.info( "Creating (ADD) policy (name: " + p.getName() + ")." );

			Filter filter = p.getFilter();

			if ( filter instanceof AnyFilter ) {

				// TODO-DAWID to jest prawdopodobnie do wywalenia

				// We apply the policy directly to the Interface.

				try {

					VNicMBean vnic = vNicManager.getByName( NameHelper.interfaceName( p.getInterface() ) );

					if ( p instanceof PriorityPolicy ) {
						vnic.setProperty( LinkProperties.PRIORITY, ( ( PriorityPolicy ) p ).getPriorityAsString() );
					} else if ( p instanceof BandwidthPolicy ) {
						vnic.setProperty( LinkProperties.MAXBW, String.valueOf( ( ( BandwidthPolicy ) p ).getLimit() ) );
					}

				} catch ( LinkException e ) {

					throw new ActionException( "Policy ADD error", e );

				}

			} else {

				Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();

				if ( filter instanceof IpFilter ) {

					IpFilter ipFilter = ( IpFilter ) filter;

					FlowAttribute flowAttribute = IpFilter.Location.LOCAL.equals( ipFilter.getLocation() )
					                              ? FlowAttribute.LOCAL_IP : FlowAttribute.REMOTE_IP;

					IpAddress address = ipFilter.getAddress();

					attrs.put( flowAttribute,
					           address.getAddress() + "/" + String.valueOf( address.getNetmask() ) );

				} else if ( filter instanceof TransportFilter ) {

					attrs.put( FlowAttribute.TRANSPORT, ( ( TransportFilter ) filter ).getTransport().toString().toLowerCase() );

				} else if ( filter instanceof PortFilter ) {

					PortFilter portFilter= ( PortFilter ) filter;

					FlowAttribute flowAttribute = PortFilter.Location.LOCAL.equals( portFilter.getLocation() )
					                              ? FlowAttribute.LOCAL_PORT : FlowAttribute.REMOTE_PORT;

					attrs.put( FlowAttribute.TRANSPORT, portFilter.getProtocolAsString() );
					attrs.put( flowAttribute, String.valueOf( portFilter.getPort() ) );

				}

				Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();

				if ( p instanceof PriorityPolicy ) {
					props.put( FlowProperty.PRIORITY, ( ( PriorityPolicy ) p ).getPriorityAsString() );
				} else if ( p instanceof BandwidthPolicy ) {
					props.put( FlowProperty.MAXBW, String.valueOf( ( ( BandwidthPolicy ) p ).getLimit() ) );
				}

				try {
					flowManager.create( new Flow( NameHelper.policyName( p ), attrs, props, NameHelper.interfaceName( p.getInterface() ), TEMPORARY ) );
				} catch ( XbowException ex ) {
					throw new ActionException( "Policy ADD error", ex );
				}

			}

		}

	}


	private void switchesREM( List< Switch > switches ) {

		for ( Switch s : switches ) {

			logger.info( "Removing (REM) switch (name: " + s.getResourceId() + ")." );

			try {

				etherstubManager.delete( NameHelper.switchName( s ), TEMPORARY );

			} catch ( EtherstubException ex ) {

			}

		}

	}


	private void switchesADD( List< Switch > switches ) throws ActionException {

		for ( Switch s : switches ) {

			logger.info( "Creating (ADD) switch (name:" + s.getResourceId() + ")" );

			try {

				etherstubManager.create(
					new Etherstub( NameHelper.switchName( s ), TEMPORARY )
				);

			} catch ( EtherstubException ex ) {

				throw new ActionException( "Switch ADD error", ex );

			}

		}

	}


	private void interfacesREM( List< Interface > interfaces ) throws ActionException {

		for ( Interface i : interfaces ) {

			logger.info( "Removing (REM) interface (name: " + i.getResourceId() + ")." );

			try {

				vNicManager.delete( NameHelper.interfaceName( i ), TEMPORARY );

			} catch ( LinkException ex ) {

				throw new ActionException( "Interface REM error", ex );

			}

		}

	}


	private void interfacesADD( List< Interface > interfaces, Assignments assignments ) throws ActionException {

		for ( Interface iface : interfaces ) {

			logger.info( "Creating (ADD) interface (name: " + iface.getResourceId() + ")." );

			InterfaceAssignment annotation = assignments.getAnnotation( iface );

			if ( ( null != annotation ) && ( annotation instanceof VlanInterfaceAssignment ) ) {

				// We have to instantiate VLAN interface

				VlanInterfaceAssignment assign = ( VlanInterfaceAssignment ) annotation;

				if ( null != assign.getName() ) {
					logger.warn( "Ignoring annotation field name (name: " + assign.getName() + ")." );
				}

				if ( null != assign.getLink() ) {
					logger.warn( "Ignoring annotation field line (linek: " + assign.getLink() + ")." );
				}

				logger.info( "VLAN interface instantiation (name: " + NameHelper.interfaceName( iface )
				             + ", tag: " + assign.getTag() + ")." );

				try {

					vlanManager.create( NameHelper.interfaceName( iface ),
					                    getDefaultPhysical(),
					                    assign.getTag() );

				} catch ( XbowException ex ) {
					throw new ActionException( "Interface ADD error", ex );
				}

			} else {

				try {

					if ( iface.getEndpoint() instanceof Switch ) {
						vNicManager.create( new VNic( NameHelper.interfaceName( iface ), TEMPORARY,
						                    NameHelper.switchName( ( Switch ) iface.getEndpoint() ) ) );
					} else {
						// TODO what to do now?
					}

				} catch ( LinkException ex ) {
					throw new ActionException( "Interface ADD error", ex );
				}

			}

		}

	}


	private void appliancesREM( List< Appliance > appliances ) throws ActionException {

		RemoveZoneCommand cmd = null;

		try {
			cmd = commandFactory.getRemoveZoneCommand();
		} catch ( CommandException ex ) {
			throw new ActionException( "Appliance REM error", ex );
		}

		for ( Appliance app : appliances ) {

			logger.info( "Removing (REM) appliance (name: " + app.getResourceId()
			             + ", repoId: " + app.getRepoId() + ")" );

			try {
				cmd.removeZone( new ZoneInfo( ApplianceType.MACHINE.equals( app.getType() )
				                              ? NameHelper.machineName( app )
				                              : NameHelper.routerName( app ) ) );
			} catch ( CommandException ex ) {
				throw new ActionException( "Appliance REM error", ex );
			}

		}

	}


	private void appliancesADD( List< Appliance > appliances ) throws ActionException {

		CreateZoneFromSnapshotCommand createCmd = null;
		ModifyZoneCommand modifyCmd = null;

		try {
			createCmd = commandFactory.getCreateZoneFromSnapshotCommand();
			modifyCmd = commandFactory.getModifyZoneCommand();
		} catch ( CommandException ex ) {
			throw new ActionException( "Appliance ADD error", ex );
		}

		for ( Appliance app : appliances ) {

			logger.info( "Creating (ADD) appliance (name: " + app.getResourceId()
			             + ", repoId: " + app.getRepoId() + ")." );

			try {

				// TODO the following is temporary
				if ( ApplianceType.ROUTER.equals( app.getType() ) ) {
					app.setRepoId( "dummy" );
				}
				// TODO END

				String name = ApplianceType.MACHINE.equals( app.getType() )
				              ? NameHelper.machineName( app )
				              : NameHelper.routerName( app );

				ZoneInfo zoneInfo = new ZoneInfo();

				zoneInfo.setName( name );
				zoneInfo.setPhysical( null );  // Don't set up any interfaces now.
				zoneInfo.setAutoboot( false );
				zoneInfo.setZfsPool( "rpool/Appliances" );
				zoneInfo.setPool( null );

				createCmd.createZone( zoneInfo, "/appliance/" + app.getRepoId() );

				// All the interfaces have been instantiated before, collect the names
				// and attach them to the appliance.

				List< String > ifaces = new LinkedList< String >();
				for ( Interface iface : app.getInterfaces() ) {
					ifaces.add( NameHelper.interfaceName( iface ) );
				}

				modifyCmd.attachInterfaces( name, ifaces );

				// Boot the appliance.

				modifyCmd.bootZone( name );

				// Setup IP stack and address the interfaces.

				List< String > addresses = new LinkedList< String >();
				for ( Interface i : app.getInterfaces() ) {
					addresses.add( i.getIpAddress().toString() );
				}

				modifyCmd.configureInterfaces( name, ifaces, addresses );

				// For ROUTER appliance only, enable IP forwarding.

				if ( ApplianceType.ROUTER.equals( app.getType() ) ) {
					modifyCmd.setupForwarding( name, true );
				}

			} catch ( CommandException ex ) {

				throw new ActionException( "Appliance ADD error", ex );

			}

		}

	}


	private String getDefaultPhysical() {
		return "vnet0";  // TODO  change it to return the interface with proper IP.
	}


	/*
	 * JConsole only
	 */

	@Override
	public void _discover() {
		discover();
	}


	private final VNicManagerMBean vNicManager;
	private final EtherstubManagerMBean etherstubManager;
	private final FlowManagerMBean flowManager;
	private final GlobalZoneManagementMBean globalZoneManagement;
	private final VlanManagerMBean vlanManager;

	private final SolarisCommandFactory commandFactory;

	private final boolean TEMPORARY = false;

	private static final Logger logger = Logger.getLogger( Worker.class );

}
