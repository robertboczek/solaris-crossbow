public interface WorkerMBean {

	public void instantiate(
		ObjectModel model,
		Actions actions,
		Assignments assignments
	) throws ModelInstantiationException;

	public Map< String, Pair< ObjectModel, Assignments > > discover();

	public String getInstantiatedName( Appliance app );

}
