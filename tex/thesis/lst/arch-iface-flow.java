public interface FlowMBean {

	public String getName();

	public String getLink();

	public Map< FlowAttribute, String > getAttributes()
		throws NoSuchFlowException;

	public Map< FlowProperty, String > getProperties()
		throws NoSuchFlowException;

	public void setProperties( Map< FlowProperty, String > properties,
	                           boolean temporary )
		throws NoSuchFlowException,
		       ValidationException;

}
