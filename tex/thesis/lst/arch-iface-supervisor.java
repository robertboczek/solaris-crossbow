public interface SupervisorMBean {

	public void instantiate( ObjectModel model, Actions actions )
		throws ModelInstantiationException;

	public void instantiate(
		ObjectModel model,
		Actions actions,
		Assignments assignments
	);

	public Map< String, Pair< ObjectModel, Assignments > > discover();

	public List< String > getWorkers();

}
