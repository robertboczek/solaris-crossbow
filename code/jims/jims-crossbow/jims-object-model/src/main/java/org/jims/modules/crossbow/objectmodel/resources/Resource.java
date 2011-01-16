
package org.jims.modules.crossbow.objectmodel.resources;

import java.io.Serializable;

/**
 * Abstract Resouce describing Node or Endpoint
 *
 * @author robert boczek
 */
public abstract class Resource implements Serializable{
    
    private String resourceId;
    private String projectId;

    public Resource(String resourceId, String projectId) {
        this.resourceId = resourceId;
        this.projectId = projectId;
    }

    /**
     * Get the value of resourceId
     *
     * @return the value of resourceId
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Set the value of resourceId
     *
     * @param resourceId new value of resourceId
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Get the value of projectId
     *
     * @return the value of projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Set the value of projectId
     *
     * @param projectId new value of projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

}
