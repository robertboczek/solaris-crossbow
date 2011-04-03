package org.jims.modules.crossbow.infrastructure.appliance;

import java.util.List;


/**
 *
 * @author cieplik
 */
public interface RepoManagerMBean {

	public List< String > getIds();

	public String getRepoPath();

	public void setRepoPath( String path );

}
