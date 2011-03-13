package org.jims.modules.crossbow.infrastructure.worker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.etherstub.Etherstub;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.Flow;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.infrastructure.worker.exception.ActionException;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.link.VNic;
import org.jims.modules.crossbow.link.VNicMBean;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.filters.Filter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.PortFilter;
import org.jims.modules.crossbow.objectmodel.filters.TransportFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;


/**
 *
 * @author cieplik
 */
public class Worker implements WorkerMBean {

	// TODO mechanizm wycofywania zmian w przypadku bledu

	public Worker( VNicManagerMBean vNicManager, EtherstubManagerMBean etherstubManager,
	               FlowManagerMBean flowManager, ZoneCopierMBean zoneCopier ) {

		this.vNicManager = vNicManager;
		this.etherstubManager = etherstubManager;
		this.flowManager = flowManager;
		this.zoneCopier = zoneCopier;

	}


	@Override
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) throws ModelInstantiationException {

		Map< Object, Actions.ACTION > actionsMap = actions.getAll();

		try {

			instantiateREM( extractByAction( actionsMap, Actions.ACTION.REM ) );
			instantiateADD( extractByAction( actionsMap, Actions.ACTION.ADD ), assignments );
			instantiateUPD( extractByAction( actionsMap, Actions.ACTION.UPD ) );

		} catch ( ActionException ex ) {

			throw new ModelInstantiationException( ex );

		}

	}


	private void instantiateREM( List< Object > resources ) throws ActionException {

		// machinesREM( extractByType( resources, Machine.class ) );

		policiesREM( extractByType( resources, Policy.class ) );
		interfacesREM( extractByType( resources, Interface.class ) );
		switchesREM( extractByType( resources, Switch.class ) );

	}


	private void instantiateADD( List< Object > resources, Assignments assignments ) throws ActionException {

		switchesADD( extractByType( resources, Switch.class ) );
		interfacesADD( extractByType( resources, Interface.class ) );
		policiesADD( extractByType( resources, Policy.class ) );

		// machinesADD( extractByType( resources, Machine.class ) );

	}


	private void instantiateUPD( List< Object > resources ) {

		// policiesUPD( extractByType( resources, Policy.class ) );
		// portsUPD( extractByType( resources, Interface.class ) );
		// switchesUPD( extractByType( resources, Switch.class ) );

		// machinesUPD( extractByType( resources, Machine.class ) );

	}


	private List< Object > extractByAction( Map< Object, Actions.ACTION > actionsMap, Actions.ACTION action ) {

		List< Object > res = new LinkedList< Object >();

		for ( Map.Entry< Object, Actions.ACTION > entry : actionsMap.entrySet() ) {

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
	public ObjectModel discover() {

		ObjectModel om = new ObjectModel();
		Map< String, Object > ids = new HashMap< String, Object >();

		discoverSwitches( om, ids );
		discoverInterfaces( om, ids );

		return om;

	}


	private void discoverSwitches( ObjectModel om, Map< String, Object > ids ) {

		List< Switch > switches = new LinkedList< Switch >();

		try {

			for ( Matcher m : filterNames( etherstubManager.getEtherstubsNames(), REG_SWITCH_NAME ) ) {

				logger.info( "Found a switch (name: " + m.group( 2 ) + ", project: " + m.group( 1 ) + ")" );

				Switch s = new Switch( m.group( 2 ), m.group( 1 ) );
				switches.add( s );
				ids.put( m.group(), s );

			}

		} catch ( EtherstubException e ) {
		}


		logger.info( "Adding " + switches.size() + " matching switch(es) to the model." );

		for ( Switch s : switches ) {
			om.register( s );
		}

	}


	private void discoverInterfaces( ObjectModel om, Map< String, Object > ids ) {

		List< Interface > interfaces = new LinkedList< Interface >();

		try {

			for ( Matcher m : filterNames( vNicManager.getVNicsNames(), REG_INTERFACE_NAME ) ) {

				// Basic setup.

				Interface iface = new Interface( m.group( 2 ), m.group( 1 ) );

				// Discover details.

				VNicMBean vnic = vNicManager.getByName( m.group() );

				iface.setEndpoint( ( Switch ) ids.get( vnic.getParent() ) );
				
				interfaces.add( iface );
				ids.put( m.group(), iface );

			}

		} catch ( LinkException ex ) {
		}

		logger.info( "Adding " + interfaces.size() + " interface(s) to the model." );

		for ( Interface iface : interfaces ) {
			om.register( iface );
		}

	}


	private List< Matcher > filterNames( List< String > names, String regexp ) {

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

			if ( policy.getFilter() instanceof AnyFilter ) {

				// Modify underlying VNIC.

				try {

					VNicMBean vnic = vNicManager.getByName( interfaceName( policy.getInterface() ) );

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
					flowManager.remove( policyName( policy ), TEMPORARY );
				} catch ( XbowException ex ) {
					throw new ActionException( "Policy REM error", ex );
				}

			}

		}

	}


	private void policiesADD( List< Policy > policies ) throws ActionException {

		for ( Policy p : policies ) {

			Filter filter = p.getFilter();

			if ( filter instanceof AnyFilter ) {

				// We apply the policy directly to the Interface.

				try {

					VNicMBean vnic = vNicManager.getByName( interfaceName( p.getInterface() ) );

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

					attrs.put( FlowAttribute.TRANSPORT, ( ( TransportFilter ) filter ).getProtocol() );

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
					flowManager.create( new Flow( policyName( p ), attrs, props, interfaceName( p.getInterface() ), TEMPORARY ) );
				} catch ( XbowException ex ) {
					throw new ActionException( "Policy ADD error", ex );
				}

			}

		}

	}


	private void switchesREM( List< Switch > switches ) {

		for ( Switch s : switches ) {

			try {

				etherstubManager.delete( switchName( s ), TEMPORARY );

			} catch ( EtherstubException ex ) {

			}

		}

	}


	private void switchesADD( List< Switch > switches ) throws ActionException {

		for ( Switch s : switches ) {

			try {

				etherstubManager.create(
					new Etherstub( switchName( s ), TEMPORARY )
				);

			} catch ( EtherstubException ex ) {

				throw new ActionException( "Switch ADD error", ex );

			}

		}

	}


	private void interfacesREM( List< Interface > interfaces ) throws ActionException {

		for ( Interface i : interfaces ) {

			try {

				vNicManager.delete( interfaceName( i ), TEMPORARY );

			} catch ( LinkException ex ) {

				throw new ActionException( "Interface REM error", ex );

			}

		}

	}


	private void interfacesADD( List< Interface > interfaces ) throws ActionException {

		for ( Interface i : interfaces ) {

			try {

				if ( i.getEndpoint() instanceof Switch ) {
					vNicManager.create( new VNic( interfaceName( i ), TEMPORARY, switchName( ( Switch ) i.getEndpoint() ) ) );
				} else {
					// TODO what to do now?
				}

			} catch ( LinkException ex ) {

				throw new ActionException( "Interface ADD error", ex );

			}

		}

	}


	private String switchName( Switch s ) {
		return s.getProjectId() + SEP + s.getResourceId();
	}

	// TODO-DAWID: v add ApplianceId
	private String interfaceName( Interface iface ) {
		return iface.getProjectId() + SEP + iface.getResourceId();
	}

	private String policyName( Policy p ) {
		return p.getInterface().getProjectId() + SEP + p.getInterface().getResourceId() + SEP + p.getName();
	}


	/*
	 * JConsole only
	 */

	@Override
	public void _discover() {
		discover();
	}


	/**
	 * The separator used in entities' names (e.g. MYPROJECT..SWITCH..0)
	 */
	public final static String SEP = "..";

	private final static String REG_SEP = "\\.\\.";
	private final static String REG_PROJECT_ID = "[a-zA-Z](?:(?:\\.[a-zA-Z])|(?:[a-zA-Z]))*";  // TODO
	private final static String REG_RESOURCE_ID = "[a-zA-Z]+[0-9]+";  // TODO

	private final static String REG_SWITCH_NAME = "(" + REG_PROJECT_ID + ")" + REG_SEP + "(" + REG_RESOURCE_ID + ")";
	private final static String REG_INTERFACE_NAME =
		"(" + REG_PROJECT_ID + ")" + REG_SEP + "[a-zA-Z]+" + REG_SEP + "(" + REG_RESOURCE_ID + ")";

	private final VNicManagerMBean vNicManager;
	private final EtherstubManagerMBean etherstubManager;
	private final FlowManagerMBean flowManager;
	private final ZoneCopierMBean zoneCopier;

	private final boolean TEMPORARY = false;

	private static final Logger logger = Logger.getLogger( Worker.class );

}
