package org.jims.modules.crossbow.lib;

import java.util.List;
import org.jims.modules.crossbow.vlan.VlanInfo;


public interface VlanHelper {

	void create( VlanInfo vlanInfo );
	void remove( String name );

	List< VlanInfo > getVlanInfos();

}
