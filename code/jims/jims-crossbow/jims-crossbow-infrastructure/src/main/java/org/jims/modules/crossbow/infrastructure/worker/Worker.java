package org.jims.modules.crossbow.infrastructure.worker;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jims.modules.crossbow.etherstub.Etherstub;
import org.jims.modules.crossbow.etherstub.EtherstubManagerMBean;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.flow.FlowManagerMBean;
import org.jims.modules.crossbow.link.VNic;
import org.jims.modules.crossbow.link.VNicManagerMBean;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.zones.ZoneCopierMBean;


/**
 *
 * @author cieplik
 */
public class Worker implements WorkerMBean {

	public Worker( VNicManagerMBean vNicManager, EtherstubManagerMBean etherstubManager,
	               FlowManagerMBean flowManager, ZoneCopierMBean zoneCopier ) {

		this.vNicManager = vNicManager;
		this.etherstubManager = etherstubManager;
		this.flowManager = flowManager;
		this.zoneCopier = zoneCopier;

	}


	@Override
	public void instantiate( ObjectModel model, Actions actions, Assignments assignments ) {

		instantiateSwitches( model.getSwitches(), actions );
		instantiatePorts( model.getPorts(), actions, assignments );

	}


	@Override
	public void discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private void instantiateSwitches( List< Switch > switches, Actions actions ) {

		for ( Switch s : switches ) {

			Actions.ACTION action = actions.get( s );

			if ( Actions.ACTION.ADD.equals( action ) ) {

				try {

					etherstubManager.create(
						new Etherstub( s.getProjectId() + SEP + s.getResourceId(), false )
					);

				} catch ( EtherstubException ex ) {
					// TODO-DAWID what now?
					Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else if ( Actions.ACTION.REM.equals( action ) ) {

				try {

					etherstubManager.delete( s.getProjectId() + SEP + s.getResourceId(), true );

				} catch ( EtherstubException ex ) {

				}

			} else if ( Actions.ACTION.REMREC.equals( action ) ) {

				// try {
				//
				// 	// TODO-DAWID prerequisites -> existing vnics;
				//
				// }

			}

			// TODO pozostale akcje

		}

	}


	private void instantiatePorts( List< Port > ports, Actions actions, Assignments assignments ) {

		for ( Port p : ports ) {

			Actions.ACTION action = actions.get( p );

			if ( Actions.ACTION.ADD.equals( action ) ) {

				try {
					vNicManager.create( new VNic( p.getProjectId() + SEP + p.getResourceId(), false, assignments.getAssignment( p ) ) );
				} catch ( LinkException ex ) {

					// TODO what now?
					Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else if ( Actions.ACTION.REM.equals( action ) ) {

				try {

					vNicManager.delete( p.getProjectId() + SEP + p.getResourceId(), false );

				} catch ( LinkException ex ) {

				}

			}

			// TODO pozostale akcje

		}

	}


	/**
	 * The separator used in entities' names (e.g. MYPROJECT..SWITCH..0)
	 */
	public final static String SEP = "..";

	private final VNicManagerMBean vNicManager;
	private final EtherstubManagerMBean etherstubManager;
	private final FlowManagerMBean flowManager;
	private final ZoneCopierMBean zoneCopier;

}
