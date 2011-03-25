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

	public static String routerName( Appliance a ) {
		return a.getProjectId() + SEP + ROUTER + SEP + a.getResourceId();
	}


	public final static String SEP = "..";
	public final static String ROUTER = "ROUTER";

	private final static String REG_SEP = "\\.\\.";
	private final static String REG_PROJECT_ID = "[a-zA-Z](?:(?:\\.[a-zA-Z])|(?:[a-zA-Z]))*";  // TODO
	private final static String REG_RESOURCE_ID = "[a-zA-Z]+[0-9]+";  // TODO

	public final static String REG_SWITCH_NAME = "(" + REG_PROJECT_ID + ")" + REG_SEP + "(" + REG_RESOURCE_ID + ")";
	public final static String REG_INTERFACE_NAME =
		"(" + REG_PROJECT_ID + ")" + REG_SEP + "[a-zA-Z]+" + REG_SEP + "(" + REG_RESOURCE_ID + ")";

}
