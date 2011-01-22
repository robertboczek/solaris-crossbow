package org.jims.modules.crossbow.objectmodel.resources;


/**
 * Endpoint Resource
 *
 * @author robert boczek
 */
public abstract class Endpoint extends Resource {

	public Endpoint(String resourceId, String projectId) {
		super(resourceId, projectId);
	}


	public abstract void update( Endpoint e );

}
