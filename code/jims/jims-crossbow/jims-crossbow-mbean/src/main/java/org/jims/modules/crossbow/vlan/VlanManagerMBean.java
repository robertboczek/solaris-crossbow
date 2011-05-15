package org.jims.modules.crossbow.vlan;

import java.util.List;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.manager.GenericManager;


/**
 *
 * @author cieplik
 */
public interface VlanManagerMBean extends GenericManager< VlanMBean > {

	void discover();

	void create( VlanMBean vlan ) throws XbowException;
	void create( String name, String link, int tag ) throws XbowException;

	void remove( String name );

	List< String > getVlans();

	VlanMBean getByName( String name );

}
