package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.lib.LinkHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * The class implements Link MBean functionality.
 *
 * @see LinkMBean
 *
 * @author cieplik
 */
public class Link implements LinkMBean {

	/**
	 * @see LinkMBean#getName()
	 */
	@Override
	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	/**
	 * @see LinkMBean#getProperties()
	 */
	@Override
	public Map< LinkProperties, String > getProperties() throws LinkException {

		logger.info( "Getting properties for " + this.name );

		Map< LinkProperties, String > properties = new HashMap< LinkProperties, String >();

		for ( LinkProperties property : LinkProperties.values() ) {
			properties.put( property, linkHelper.getLinkProperty( name, property ) );
		}

		return properties;

	}


	/**
	 * @see LinkMBean#setProperty(agh.msc.xbowbase.enums.LinkProperties, java.lang.String)
	 */
	@Override
	public void setProperty( LinkProperties property, String value ) throws LinkException {

		logger.info( "Setting new property " + property + " value "
		             + " to " + value + " for " + this.name );

		linkHelper.setLinkProperty( this.name, property, value );

		//@todo check return value

	}


	/**
	 * @see LinkMBean#getParameters()
	 */
	@Override
	public Map< LinkParameters, String > getParameters() throws LinkException {

		logger.info( "Getting parameters for " + this.name );

		Map< LinkParameters, String > parameters = new HashMap< LinkParameters, String >();

		for ( LinkParameters parameter : LinkParameters.values() ) {
			parameters.put( parameter, linkHelper.getLinkParameter( name, parameter ) );
		}

		return parameters;
	}


	@Override
	public Map<LinkStatistics, String> getStatistics() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see LinkMBean#getIpAddress()
	 */
	@Override
	public String getIpAddress() throws LinkException {
		//@todo use jna to get vnic's ip address
		return this.ipAddress;
	}


	/**
	 * @see LinkMBean#setIpAddress(java.lang.String)
	 */
	@Override
	public void setIpAddress( String ipAddress ) throws LinkException {
		//@todo use jna to set vnic's ip address
		this.ipAddress = ipAddress;
	}


	/**
	 * @see LinkMBean#getIpMask()
	 */
	@Override
	public String getIpMask() throws LinkException {
		return linkHelper.getNetmask( name );
	}

	/**
	 * @see LinkMBean#setIpMask(java.lang.String)
	 */
	@Override
	public void setIpMask( String ipMask ) throws ValidationException, LinkException {
		linkHelper.setNetmask( name, ipMask );
	}


	@Override
	public boolean isPlumbed() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setPlumbed(boolean plumbed) throws LinkException {
		linkHelper.plumb( name );
	}


	@Override
	public boolean isUp() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setUp(boolean up) throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	public void setLinkHelper( LinkHelper linkHelper ) {
		this.linkHelper = linkHelper;
	}


	protected String name;
	protected String ipAddress;

	protected LinkHelper linkHelper;

	private static final Logger logger = Logger.getLogger( Link.class );

}
