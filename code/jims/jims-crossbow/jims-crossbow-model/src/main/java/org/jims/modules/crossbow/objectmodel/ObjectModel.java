package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
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

	public List< Switch > getSwitches() {
		return switches;
	}

	public List< Interface > getPorts() {
		return ports;
	}

	public List< Appliance > getAppliances() {
		return appliances;
	}

	public List< Policy > getPolicies() {
		return policies;
	}


	List< Switch > switches = new LinkedList< Switch >();
	List< Interface > ports = new LinkedList< Interface >();
	List< Appliance > appliances = new LinkedList< Appliance >();
	List< Policy > policies = new LinkedList< Policy >();

}
