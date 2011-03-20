package org.jims.modules.crossbow.infrastructure.worker;

import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;


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


	public final static String SEP = "..";

}
