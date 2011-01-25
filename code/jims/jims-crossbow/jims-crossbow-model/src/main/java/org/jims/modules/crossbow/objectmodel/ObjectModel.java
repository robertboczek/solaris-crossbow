package org.jims.modules.crossbow.objectmodel;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Machine;
import org.jims.modules.crossbow.objectmodel.resources.Port;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class ObjectModel implements Serializable {

	public void addSwitches( Switch... switches ) {
		this.switches.addAll( Arrays.asList( switches ) );
	}

	public void addPorts( Port... ports ) {
		this.ports.addAll( Arrays.asList( ports ) );
	}

	public void addMachines( Machine... machines ) {
		this.machines.addAll( Arrays.asList( machines ) );
	}

	public void addPolicies( Policy... policies ) {
		this.policies.addAll( Arrays.asList( policies ) );
	}

	public List< Switch > getSwitches() {
		return switches;
	}

	public List< Port > getPorts() {
		return ports;
	}

	public List< Machine > getMachines() {
		return machines;
	}

	public List< Policy > getPolicies() {
		return policies;
	}


	List< Switch > switches = new LinkedList< Switch >();
	List< Port > ports = new LinkedList< Port >();
	List< Machine > machines = new LinkedList< Machine >();
	List< Policy > policies = new LinkedList< Policy >();

}
