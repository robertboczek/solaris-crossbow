
package org.jims.modules.crossbow.objectmodel.filters;

import java.io.Serializable;

/**
 * Abstract filter class
 * @author robert boczek
 */
public abstract class Filter implements Serializable{

    public enum Location{
        LOCAL, REMOTE;
    }

}
