package org.jims.modules.crossbow.infrastructure.worker;

import java.util.LinkedList;
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
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Resource;
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

		List< Resource > invalid = new LinkedList< Resource >();  // TODO  invalidated ENTITIES instead of Resources only

		instantiateSwitches( model.getSwitches(), actions, invalid );
		instantiatePorts( model.getPorts(), actions, assignments, invalid );

	}


	@Override
	public void discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private void instantiateSwitches( List< Switch > switches, Actions actions, List< Resource > invalid ) {

		for ( Switch s : switches ) {

			if ( invalid.contains( s ) ) {
				continue;
			}

			Actions.ACTION action = actions.get( s );

			if ( Actions.ACTION.ADD.equals( action ) ) {

				try {

					etherstubManager.create(
						new Etherstub( s.getProjectId() + SEP + s.getResourceId(), TEMPORARY )
					);

				} catch ( EtherstubException ex ) {
					// TODO-DAWID what now?
					Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else if ( Actions.ACTION.REM.equals( action ) ) {

				try {

					etherstubManager.delete( s.getProjectId() + SEP + s.getResourceId(), TEMPORARY );

				} catch ( EtherstubException ex ) {

				}

			} else if ( Actions.ACTION.REMREC.equals( action ) ) {

				removeRecursively( s, invalid );

				// try {
				//
				// 	// TODO-DAWID prerequisites -> existing vnics;
				//
				// }

			}

			invalid.add( s );

			// TODO pozostale akcje

		}

	}


	private void removeRecursively( Switch s, List< Resource > invalid ) {

		// TODO-DAWID  just remove vnics created over the etherstub?
		// TODO-DAWID  switch nie zawsze mapuje sie na etherstub? (kilka vnikow nad nikiem)

		for ( Endpoint e : s.getEndpoints() ) {

			if ( e instanceof Port ) {

				// TODO-DAWID  remove ports recursively here (flows may be defined on top)

				try {
					vNicManager.delete( e.getProjectId() + SEP + e.getResourceId(), TEMPORARY );
				} catch (LinkException ex) {
					Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}

				invalid.add( e );

			}

		}

		try {
			etherstubManager.delete(s.getProjectId() + SEP + s.getResourceId(), TEMPORARY);
		} catch (EtherstubException ex) {
			Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
		}

	}


	private void instantiatePorts( List< Port > ports, Actions actions, Assignments assignments, List< Resource > invalid ) {

		for ( Port p : ports ) {

			if ( invalid.contains( p ) ) {
				continue;
			}

			Actions.ACTION action = actions.get( p );

			if ( Actions.ACTION.ADD.equals( action ) ) {

				try {
					vNicManager.create( new VNic( p.getProjectId() + SEP + p.getResourceId(), TEMPORARY, assignments.getAssignment( p ) ) );
				} catch ( LinkException ex ) {

					// TODO what now?
					Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}

			} else if ( Actions.ACTION.REM.equals( action ) ) {

				try {

					vNicManager.delete( p.getProjectId() + SEP + p.getResourceId(), TEMPORARY );

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

	private final boolean TEMPORARY = false;

}
