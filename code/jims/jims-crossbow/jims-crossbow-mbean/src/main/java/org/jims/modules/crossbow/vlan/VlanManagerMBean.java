package org.jims.modules.crossbow.vlan;

import java.util.List;


/**
 *
 * @author cieplik
 */
public interface VlanManagerMBean {

	void discover();

	void create( VlanMBean vlan );
	void create( String name, String link, int tag );

	void remove( String name );

	List< String > getVlans();

}
