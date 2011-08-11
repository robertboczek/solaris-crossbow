public interface WorkerMBean {

	/**
	 * Maps domain model to low-level resources.
	 */
	public void instantiate(
		ObjectModel model,
		Actions actions,
		Assignments assignments
	) throws ModelInstantiationException;

	/**
	 * Analyzes present system entities and reconstructs the domain model.
	 */
	public Map< String, Pair< ObjectModel, Assignments > > discover();

}
