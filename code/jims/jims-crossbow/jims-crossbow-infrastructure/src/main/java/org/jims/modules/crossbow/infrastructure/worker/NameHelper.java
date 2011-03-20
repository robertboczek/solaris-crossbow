package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;


/**
 *
 * @author cieplik
 */
public class NameHelper {

	public static String interfaceName( Interface iface ) {
		return iface.getProjectId() + SEP + iface.getAppliance().getResourceId() + SEP + iface.getResourceId();
	}

	public static String policyName( Policy p ) {
		return p.getInterface().getProjectId() + SEP + p.getInterface().getResourceId() + SEP + p.getName();
	}

	public static String switchName( Switch s ) {
		return s.getProjectId() + SEP + s.getResourceId();
	}

	public static String machineName( Appliance a ) {
		return a.getProjectId() + SEP + a.getResourceId();
	}


	public final static String SEP = "..";

}
