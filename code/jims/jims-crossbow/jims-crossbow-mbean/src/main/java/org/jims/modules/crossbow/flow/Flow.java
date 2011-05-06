package org.jims.modules.crossbow.flow;

import java.text.SimpleDateFormat;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.exception.NoSuchEnumException;
import org.jims.modules.crossbow.exception.NoSuchFlowException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.lib.FlowHelper;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.flow.enums.FlowStatistics;


/**
 * The class implements Flow MBean functionality.
 *
 * @author cieplik
 */
public class Flow implements FlowMBean {

    private static final long DAY_MILIS = 8640000;
    private static final long HOUR_MILIS = 360000;
    private static final long FIVE_MINUTES_MILIS = 30000;
    private static final long MINUTE_MILIS = 6000;

	public Flow() {}


	public Flow( String name, Map< FlowAttribute, String > attrs, Map< FlowProperty, String > props,
	             String link, boolean temporary ) {

		this.name = name;
		this.attrs = attrs;
		this.props = props;
		this.link = link;
		this.temporary = temporary;

	}


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
	public Map< FlowAttribute, String > getAttributes() throws NoSuchFlowException {
		return flowadm.getAttributes( name );
	}


	/**
	 * @brief  Attrs getter method.
	 *
	 * Returns cached attributes. Doesn't call underlying flow helper.
	 *
	 * @return  flow's attributes map
	 */
	public Map< FlowAttribute, String > getAttrs() {
		return attrs;
	}

	/**
	 * @brief  Attrs setter method.
	 *
	 * Sets attributes to attrs. Doesn't call underlying flow helper.
	 *
	 * @param  attrs  flow's attributes
	 */
	public void setAttrs( Map< FlowAttribute, String > attrs ) {
		this.attrs = attrs;
	}


	/**
	 * Retrieves flow properties using underlying flow helper.
	 *
	 * @see  FlowMBean#getProperties()
	 */
	@Override
	public Map< FlowProperty, String > getProperties() throws NoSuchFlowException {
		props = flowadm.getProperties( name );
		return props;
	}

	/**
	 * Sets flow's properties using underlying flow helper.
	 *
	 * @see  FlowMBean#setProperties(java.util.Map, boolean)
	 */
	@Override
	public void setProperties( Map< FlowProperty, String > properties, boolean temporary )
		throws NoSuchFlowException,
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
	public Map< FlowProperty, String > getProps() {
		return props;
	}

	/**
	 * @brief  Props setter method.
	 *
	 * Sets properties to props. Doesn't call underlying flow helper.
	 *
	 * @param  props  flow's properties
	 */
	public void setProps( Map< FlowProperty, String > props ) {
		this.props = props;
	}


	/**
	 * @see  FlowMBean#resetProperties(java.util.List, boolean)
	 */
	@Override
	public void resetProperties( List< FlowProperty > properties, boolean temporary )
		throws NoSuchFlowException,
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


	@Override
	public Map< FlowStatistics, Long > getStatistics() {

		// Gather stats since the beginning of the Epoch.

		logger.info( "Gathering statistics for " + name + " since 1970." );

		return flowadm.getUsage( name, "1970", "now" );

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


	/*
	 * jconsole only
	 */

	@Override
	public Map< String, String > get_Attributes() throws NoSuchFlowException {

		Map< String, String > res = new HashMap< String, String >();

		for ( Map.Entry< FlowAttribute, String > entry : getAttributes().entrySet() ) {
			res.put( entry.getKey().toString(), entry.getValue() );
		}

		return res;

	}


	@Override
	public void set_Property( String name, String value, boolean temporary ) throws NoSuchFlowException,
	                                                                                ValidationException {

		Map< FlowProperty, String > map = new HashMap< FlowProperty, String >();

		try {
			map.put( FlowProperty.fromString( name ), value );
		} catch ( NoSuchEnumException e ) {
			throw new ValidationException( name );
		}

		setProperties( map, temporary );

	}


	@Override
	public Map< String, String > get_Properties() throws NoSuchFlowException {

		Map< String, String > res = new HashMap< String, String >();

		for ( Map.Entry< FlowProperty, String > entry : getProperties().entrySet() ) {
			res.put( entry.getKey().toString(), entry.getValue() );
		}

		return res;

	}


	@Override
	public void _resetProperty( String name, boolean temporary ) throws NoSuchFlowException,
	                                                                    ValidationException {

		try {
			resetProperties( Arrays.asList( FlowProperty.fromString( name ) ), temporary );
		} catch ( NoSuchEnumException e ) {
			throw new ValidationException( name );
		}

	}


	@Override
	public Map< String, String > get_Statistics() {

		Map< String, String > res = new HashMap< String, String >();

		for ( Map.Entry< FlowStatistics, Long > entry : getStatistics().entrySet() ) {
			res.put( entry.getKey().toString(), entry.getValue().toString() );
		}

		return res;

	}


	protected String name;
	protected String link;
	protected Map< FlowAttribute, String > attrs = new HashMap< FlowAttribute, String >();
	protected Map< FlowProperty, String > props = new HashMap< FlowProperty, String >();
	protected boolean temporary;

	private FlowHelper flowadm;

	private static final Logger logger = Logger.getLogger( Flow.class );

    @Override
    public List<Map<FlowStatistics, Long>> getStatistics(LinkStatisticTimePeriod period) {

        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.DD,hh:mm:ss");

        if(flowadm == null) {
            return null;
        }

        //pobieramy 10 wartosci
        long diff = 0;
        long date = now.getTime();
        if(period.equals(LinkStatisticTimePeriod.MINUTELY)) {
            diff = MINUTE_MILIS;
        } else if(period.equals(LinkStatisticTimePeriod.FIVE_MINUTELY)) {
            diff = FIVE_MINUTES_MILIS;
        } else if(period.equals(LinkStatisticTimePeriod.HOURLY)) {
            diff = HOUR_MILIS;
        } else if(period.equals(LinkStatisticTimePeriod.DAILY)) {
            diff =  DAY_MILIS;
        }

        LinkedList<Map<FlowStatistics, Long>> list = new LinkedList<Map<FlowStatistics, Long>>();
        for(int i=0; i<10; i++) {
            list.addFirst(flowadm.getUsage(name, sdf.format(new Date(date-diff)), sdf.format(date)));
            date -= diff;
        }
        
        return list;
    }

}
