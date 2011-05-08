package org.jims.modules.crossbow.vlan;

import java.util.List;
import org.jims.modules.crossbow.exception.XbowException;


/**
 *
 * @author cieplik
 */
public interface VlanManagerMBean {

	void discover();

	void create( VlanMBean vlan ) throws XbowException;
	void create( String name, String link, int tag ) throws XbowException;

	void remove( String name );

	List< String > getVlans();

	VlanMBean getByName( String name );

}
