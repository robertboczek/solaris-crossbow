
package org.jims.modules.crossbow.objectmodel.resources;

/**
 * Machine Node
 *
 * @author robert boczek
 */
public class Machine extends Node {

    /**
     * Variable identifies name of remote zone to be installed
     */
    private String repoId;

    public Machine(String resourceId, String projectId) {
        super(resourceId, projectId);
    }

    public Machine(String resourceId, String projectId, String repoId) {
        this(resourceId, projectId);
        this.repoId = repoId;
    }


    /**
     * Get the value of repoId
     *
     * @return the value of repoId
     */
    public String getRepoId() {
        return repoId;
    }

    /**
     * Set the value of repoId
     *
     * @param repoId new value of repoId
     */
    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

}
