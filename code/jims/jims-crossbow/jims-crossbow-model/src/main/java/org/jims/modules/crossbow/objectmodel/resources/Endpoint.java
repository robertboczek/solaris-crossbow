package org.jims.modules.crossbow.objectmodel.resources;

import java.util.UUID;

/**
 * Endpoint Resource
 *
 * @author robert boczek
 */
public abstract class Endpoint extends Resource {

	public Endpoint(String resourceId, String projectId) {
		super(resourceId, projectId);
	}

	public UUID getUUID(){
		return this.uuid;
	}


	public abstract void update( Endpoint e );

	private UUID uuid = UUID.randomUUID();

}
