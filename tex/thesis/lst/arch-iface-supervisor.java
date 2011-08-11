public interface SupervisorMBean {

	/**
	 * Performs actions on the supplied object model.
	 */
	public void instantiate( ObjectModel model, Actions actions )
		throws ModelInstantiationException;

	/**
	 * Performs actions on the supplied object model.
	 * Uses provided assignment descriptor.
	 */
	public void instantiate(
		ObjectModel model,
		Actions actions,
		Assignments assignments
	) throws ModelInstantiationException;

	/**
	 * Discovers all the topologies created.
	 * Returns topology names together with domain model representation.
	 */
	public Map< String, Pair< ObjectModel, Assignments > > discover();

	/**
	 * Retrieves the list of managed workers.
	 */
	public List< String > getWorkers();

}
