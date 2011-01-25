package org.jims.modules.crossbow.objectmodel.policy;

import java.util.Arrays;
import org.jims.modules.crossbow.objectmodel.filters.Filter;

/**
 * Policy describing priority of traffic
 *
 * @author robert boczek
 */
public class PriorityPolicy extends Policy {

    public enum Priority {
        LOW, HIGH, MEDIUM;
    }


		public PriorityPolicy( Priority priority, Filter... filters ) {

			super( Arrays.asList( filters ) );

			this.priority = priority;

		}


    /**
     * Get the value of priority
     *
     * @return the value of priority
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Set the value of priority
     *
     * @param priority new value of priority
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }


    private Priority priority;

}
