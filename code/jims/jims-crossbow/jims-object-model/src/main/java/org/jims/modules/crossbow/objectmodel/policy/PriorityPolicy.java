package org.jims.modules.crossbow.objectmodel.policy;

/**
 * Policy describing priority of traffic
 *
 * @author robert boczek
 */
public class PriorityPolicy extends Policy{

    protected PriorityPolicyEnum priority;

    public PriorityPolicy(PriorityPolicyEnum priority) {
        this.priority = priority;
    }

    /**
     * Get the value of priority
     *
     * @return the value of priority
     */
    public PriorityPolicyEnum getPriority() {
        return priority;
    }

    /**
     * Set the value of priority
     *
     * @param priority new value of priority
     */
    public void setPriority(PriorityPolicyEnum priority) {
        this.priority = priority;
    }

    public enum PriorityPolicyEnum{
        LOW, HIGH, MEDIUM;
    }

}
