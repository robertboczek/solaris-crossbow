package org.jims.modules.crossbow.infrastructure.worker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.Filter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.resources.Port;
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

			throw new ModelInstantiationException();

		}

	}


	private void instantiateREM( List< Object > resources ) throws ActionException {

		// machinesREM( extractByType( resources, Machine.class ) );

		policiesREM( extractByType( resources, Policy.class ) );
		portsREM( extractByType( resources, Port.class ) );
		switchesREM( extractByType( resources, Switch.class ) );

	}


	private void instantiateADD( List< Object > resources, Assignments assignments ) throws ActionException {

		switchesADD( extractByType( resources, Switch.class ) );
		portsADD( extractByType( resources, Port.class ), assignments );
		policiesADD( extractByType( resources, Policy.class ) );

		// machinesADD( extractByType( resources, Machine.class ) );

	}


	private void instantiateUPD( List< Object > resources ) {

		// policiesUPD( extractByType( resources, Policy.class ) );
		// portsUPD( extractByType( resources, Port.class ) );
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
	public void discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private void policiesREM( List< Policy > policies ) throws ActionException {

		for ( Policy policy : policies ) {

			try {

				flowManager.remove( policyName( policy ), TEMPORARY );

			} catch ( XbowException ex ) {

				throw new ActionException( "Policy REM error", ex );

			}

		}

	}


	private void policiesADD( List< Policy > policies ) throws ActionException {

		for ( Policy p : policies ) {

			Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();

			for ( Filter filter : p.getFiltersList() ) {

				if ( filter instanceof IpFilter ) {

					IpFilter ipFilter = ( IpFilter ) filter;

					if ( IpFilter.Location.LOCAL.equals( ipFilter.getLocation() ) ) {

						IpAddress address = ipFilter.getAddress();

						attrs.put( FlowAttribute.LOCAL_IP,
						           address.getAddress() + "/" + String.valueOf( address.getNetmask() ) );

					}

				}

			}

			Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();

			if ( p instanceof PriorityPolicy ) {
				props.put( FlowProperty.PRIORITY, ( ( PriorityPolicy ) p ).getPriorityAsString() );
			} else if ( p instanceof BandwidthPolicy ) {
				props.put( FlowProperty.MAXBW, String.valueOf( ( ( BandwidthPolicy ) p ).getLimit() ) );
			}

			Flow flow = new Flow();

			flow.setAttrs( attrs );
			flow.setProps( props );
			flow.setLink( portName( p.getPort() ) );
			flow.setName( policyName( p ) );
			flow.setTemporary( TEMPORARY );

			try {

				flowManager.create( flow );

			} catch ( XbowException ex ) {

				throw new ActionException( "Policy ADD error", ex );

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


	private void portsREM( List< Port > ports ) throws ActionException {

		for ( Port p : ports ) {

			try {

				vNicManager.delete( portName( p ), TEMPORARY );

			} catch ( LinkException ex ) {

				throw new ActionException( "Port REM error", ex );

			}

		}

	}


	private void portsADD( List< Port > ports, Assignments assignments ) throws ActionException {

		for ( Port p : ports ) {

			try {

				vNicManager.create( new VNic( portName( p ), TEMPORARY, assignments.getAssignment( p ) ) );

			} catch ( LinkException ex ) {

				throw new ActionException( "Port ADD error", ex );

			}

		}

	}


	private String switchName( Switch s ) {
		return s.getProjectId() + SEP + s.getResourceId();
	}

	private String portName( Port p ) {
		return p.getProjectId() + SEP + p.getResourceId();
	}

	private String policyName( Policy p ) {
		return p.getPort().getProjectId() + SEP + p.getPort().getResourceId() + SEP + p.getName();
	}


	/**
	 * The separator used in entities' names (e.g. MYPROJECT..SWITCH..0)
	 */
	public final static String SEP = "..";

	private final VNicManagerMBean vNicManager;
	private final EtherstubManagerMBean etherstubManager;
	private final FlowManagerMBean flowManager;
	private final ZoneCopierMBean zoneCopier;

	private final boolean TEMPORARY = false;

}
