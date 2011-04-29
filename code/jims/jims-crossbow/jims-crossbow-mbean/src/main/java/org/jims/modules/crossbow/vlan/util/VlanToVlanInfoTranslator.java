package org.jims.modules.crossbow.vlan.util;

import org.jims.modules.crossbow.vlan.Vlan;
import org.jims.modules.crossbow.vlan.VlanInfo;
import org.jims.modules.crossbow.vlan.VlanMBean;


/**
 *
 * @author cieplik
 */
public class VlanToVlanInfoTranslator {

	public static VlanInfo translate( VlanMBean vlan ) {
		return new VlanInfo( vlan.getLink(), vlan.getName(), vlan.getTag() );
	}


	public static Vlan translate( VlanInfo info ) {
		return new Vlan( info.getName(), info.getLink(), info.getTag() );
	}

}
