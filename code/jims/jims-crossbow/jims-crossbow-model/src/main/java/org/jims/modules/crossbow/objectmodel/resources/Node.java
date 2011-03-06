package org.jims.modules.crossbow.objectmodel.resources;

import java.util.LinkedList;
import java.util.List;


/**
 * Node resource
 *
 * @author robert boczek
 */
public class Node extends Resource {

	public Node( String resourceId, String projectId ) {
		super( resourceId, projectId );
	}


	public void addPort( Port p ) {
		ports.add( p );
	}

	public Port getPort( int i ) {
		return ports.get( i );
	}

	public List< Port > getPorts() {
		return ports;
	}


	List< Port > ports = new LinkedList< Port >();

}
