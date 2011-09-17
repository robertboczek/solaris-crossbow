public interface FlowManagerMBean extends GenericManager< FlowMBean >
{

	public List< String > getFlows();

	public FlowMBean getByName( String name );

	public void discover();

	public void create( FlowMBean flow ) throws XbowException;

	public void remove( String flowName, boolean temporary )
		throws XbowException;

}
