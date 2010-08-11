package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.lib.FlowHelper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * The class implements Flow MBean functionality.
 *
 * @author cieplik
 */
public class Flow implements FlowMBean {

	/**
	 * @see  FlowMBean#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @brief  Name setter method.
	 *
	 * @param  name  flow name
	 */
	public void setName( String name ) {
		this.name = name;
	}


	/**
	 * @see  FlowMBean#getLink()
	 */
	@Override
	public String getLink() {
		return link;
	}

	/**
	 * @brief  Link setter method.
	 *
	 * @param  link  link name
	 */
	public void setLink( String link ) {
		this.link = link;
	}


	/**
	 * Retrieves flow attributes using underlying flow helper.
	 *
	 * @see  FlowMBean#getAttributes()
	 */
	@Override
	public Map< String, String > getAttributes() throws NoSuchFlowException {
		return flowadm.getAttributes( name );
	}


	/**
	 * @brief  Attrs getter method.
	 *
	 * Returns cached attributes. Doesn't call underlying flow helper.
	 *
	 * @return  flow's attributes map
	 */
	public Map< String, String > getAttrs() {
		return attrs;
	}

	/**
	 * @brief  Attrs setter method.
	 *
	 * Sets attributes to attrs. Doesn't call underlying flow helper.
	 *
	 * @param  attrs  flow's attributes
	 */
	public void setAttrs( Map< String, String > attrs ) {
		this.attrs = attrs;
	}


	/**
	 * Retrieves flow properties using underlying flow helper.
	 *
	 * @see  FlowMBean#getProperties()
	 */
	@Override
	public Map< String, String > getProperties() throws NoSuchFlowException {
		props = flowadm.getProperties( name );
		return props;
	}

	/**
	 * Sets flow's properties using underlying flow helper.
	 *
	 * @see  FlowMBean#setProperties(java.util.Map, boolean)
	 */
	@Override
	public void setProperties( Map< String, String > properties, boolean temporary ) throws NoSuchFlowException,
	                                                                                        ValidationException {

		try {

			flowadm.setProperties( name, properties, temporary );
			props = flowadm.getProperties( name );

		} catch ( NoSuchFlowException e ) {

			logger.error( name + " is not present in the system.", e );
			throw e;

		} catch ( ValidationException e ) {

			logger.error( "Validation failed while setting " + name + "'s properties.", e );
			throw e;

		} catch ( Exception e ) {

			logger.error( "Error while setting " + name + "'s properties.", e );

		}

	}


	/**
	 * @brief  Props getter method.
	 *
	 * Returns cached properties. Doesn't call underlying flow helper.
	 *
	 * @return  flow's properties map
	 */
	public Map< String, String > getProps() {
		return props;
	}

	/**
	 * @brief  Props setter method.
	 *
	 * Sets properties to props. Doesn't call underlying flow helper.
	 *
	 * @param  props  flow's properties
	 */
	public void setProps( Map< String, String > props ) {
		this.props = props;
	}


	/**
	 * @see  FlowMBean#resetProperties(java.util.List, boolean)
	 */
	@Override
	public void resetProperties( List< String > properties, boolean temporary ) throws NoSuchFlowException,
	                                                                                   ValidationException {

		try {

			flowadm.resetProperties( name, properties, temporary );
			props = flowadm.getProperties( name );

		} catch ( NoSuchFlowException e ) {

			logger.error( name + " is not present in the system.", e );
			throw e;

		} catch ( ValidationException e ) {

			logger.error( "Validation failed while resetting " + name + "'s properties.", e );
			throw e;

		} catch ( Exception e ) {

			logger.error( "Error while resetting " + name + "'s properties.", e );

		}

	}


	/**
	 * @see  FlowMBean#isTemporary()
	 */
	@Override
	public boolean isTemporary() {
		return temporary;
	}

	/**
	 * @brief  Temporary setter method.
	 *
	 * @param  temporary  determines whether the flow is temporary
	 */
	public void setTemporary( boolean temporary ) {
		this.temporary = temporary;
	}


	@Override
	public boolean equals( Object o ) {

		if ( o == this ) {

			return true;

		} else if ( o instanceof Flow ) {

			return ( ( Flow ) o ).getName().equals( name );

		} else {

			return false;

		}

	}


	@Override
	public int hashCode() {

		int hash = 7;
		hash = 41 * hash + ( this.name != null ? this.name.hashCode() : 0 );
		hash = 41 * hash + ( this.link != null ? this.link.hashCode() : 0 );
		hash = 41 * hash + ( this.attrs != null ? this.attrs.hashCode() : 0 );
		return hash;

	}


	@Override
	public String toString() {
		return name + "@" + link;
	}


	/**
	 * @brief  Flow helper setter method.
	 *
	 * @param  flowadm  flow helper
	 */
	public void setFlowadm( FlowHelper flowadm ) {
		this.flowadm = flowadm;
	}


	protected String name;
	protected String link;
	protected Map< String, String > attrs = new HashMap< String, String >();
	protected Map< String, String > props = new HashMap< String, String >();
	protected boolean temporary;

	private FlowHelper flowadm = null;

	private static final Logger logger = Logger.getLogger( Flow.class );

}
