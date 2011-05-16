package org.jims.modules.crossbow.objectmodel.resources;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;


/**
 *
 * @author cieplik
 */
public class RoutingTable implements Serializable {

	public void routeAdd( IpAddress destination, IpAddress gateway ) {
		staticRoutes.put( destination, gateway );
	}


	public IpAddress routeDel( IpAddress destination ) {
		return staticRoutes.remove( destination );
	}


	public Map< IpAddress, IpAddress > getRoutes() {
		return staticRoutes;
	}


	private Map< IpAddress, IpAddress > staticRoutes = new HashMap< IpAddress, IpAddress >();

}
