package org.jims.modules.crossbow.objectmodel.resources;

import java.util.LinkedList;
import java.util.List;


/**
 * Switch Endpoint type
 *
 * @author robert boczek
 */
public class Switch extends Endpoint {

	public Switch( String resourceId, String projectId ) {
		super( resourceId, projectId );
	}


	public List< Endpoint > getEndpoints() {
		return endpoints;
	}

	public void setEndpoints( List< Endpoint > endpoints ) {
		this.endpoints = endpoints;
	}


	@Override
	public void update( Endpoint e ) {
		endpoints.add( e );
	}


	private List< Endpoint > endpoints = new LinkedList< Endpoint >();

}
