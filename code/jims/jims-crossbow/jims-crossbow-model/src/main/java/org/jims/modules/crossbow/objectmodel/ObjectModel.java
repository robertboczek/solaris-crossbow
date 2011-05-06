package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class ObjectModel implements Serializable {

	public Switch register( Switch entity ) {
		this.switches.add( entity );
		return entity;
	}

	public Interface register( Interface entity ) {
		this.ports.add( entity );
		return entity;
	}

	public Appliance register( Appliance entity ) {
		this.appliances.add( entity );
		return entity;
	}

	public Policy register( Policy entity ) {
		this.policies.add( entity );
		return entity;
	}

	/*
	 * Just for convenience.
	 */
	public Object register( Object entity ) {

		if ( entity instanceof Switch ) {
			return register( ( Switch ) entity );
		} else if ( entity instanceof Interface ) {
			return register( ( Interface ) entity );
		} else if ( entity instanceof Appliance ) {
			return register( ( Appliance ) entity );
		} else if ( entity instanceof Policy ) {
			return register( ( Policy ) entity );
		}

		throw new RuntimeException( "Unsupported entity class (class: " + entity.getClass().toString() + ")" );

	}

	public void registerAll( Object ... entities ) {
		for ( Object entity : entities ) {
			register( entity );
		}
	}

	public void registerAll( Collection< Object > entities ) {
		for ( Object entity : entities ) {
			register( entity );
		}
	}

	public void remove( Appliance app ) {
		appliances.remove( app );
	}


	public List< Switch > getSwitches() {
		return switches;
	}

	public List< Interface > getInterfaces() {
		return ports;
	}

	public List< Appliance > getAppliances() {
		return appliances;
	}

	public List< Appliance > getRouters() {

		List< Appliance > res = new LinkedList< Appliance >();

		for ( Appliance app : appliances ) {
			if ( ApplianceType.ROUTER.equals( app.getType() ) ) {
				res.add( app );
			}
		}

		return res;

	}

	public List< Appliance > getMachines() {

		List< Appliance > res = new LinkedList< Appliance >();

		for ( Appliance app : appliances ) {
			if ( ApplianceType.MACHINE.equals( app.getType() ) ) {
				res.add( app );
			}
		}

		return res;

	}

	public List< Policy > getPolicies() {
		return policies;
	}


	List< Switch > switches = new LinkedList< Switch >();
	List< Interface > ports = new LinkedList< Interface >();
	List< Appliance > appliances = new LinkedList< Appliance >();
	List< Policy > policies = new LinkedList< Policy >();

}
