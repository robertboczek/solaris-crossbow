package agh.msc.xbowbase.link;

import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * The class implements NIC MBean functionality.
 *
 * @author cieplik
 */
public class Nic implements NicMBean {

	/**
	 * @see  NicMBean#getName()
	 */
	@Override
	public String getName() {
		return name;
	}


	public void setName( String name ) {
		this.name = name;
	}


	/**
	 * @see  NicMBean#getProperties()
	 */
	@Override
	public Map< LinkProperties, String > getProperties() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#setProperties( java.util.Map )
	 */
	@Override
	public void setProperty( LinkProperties property, String value ) throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#getParameters()
	 */
	@Override
	public Map< LinkParameters, String > getParameters() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public Map<LinkStatistics, String> getStatistics() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#getIpAddress()
	 */
	@Override
	public String getIpAddress() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#setIpAddress( java.lang.String )
	 */
	@Override
	public void setIpAddress( String ipAddress ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#getIpMask()
	 */
	@Override
	public String getIpMask() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#setIpMask( java.lang.String )
	 */
	@Override
	public void setIpMask( String ipMask ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#isPlumbed()
	 */
	@Override
	public boolean isPlumbed() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#setPlumbed( boolean )
	 */
	@Override
	public void setPlumbed( boolean plumbed ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#isUp()
	 */
	@Override
	public boolean isUp() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	/**
	 * @see  NicMBean#setUp( boolean )
	 */
	@Override
	public void setUp( boolean up ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	public void setNicHelper( NicHelper nicHelper ) {
		this.nicHelper = nicHelper;
	}


	@Override
	public boolean equals( Object o ) {

		if ( o == this ) {

			return true;

		} else if ( o instanceof Nic ) {

			return name.equals( ( ( Nic ) o ).getName() );

		} else {

			return false;

		}

	}


	@Override
	public int hashCode() {

		int hash = 7;
		hash = 83 * hash + ( this.name != null ? this.name.hashCode() : 0 );

		return hash;

	}


	String name;
	private Map< LinkStatistics, String > statisticsMap = new HashMap< LinkStatistics, String >();
	private Map< LinkProperties, String > propertiesMap = new HashMap< LinkProperties, String >();
	private Map< LinkParameters, String > parametersMap = new HashMap< LinkParameters, String >();
	private String ipAddress;
	private String ipMask;
	private boolean plumbed;
	private boolean up;

	NicHelper nicHelper;

	private static final Logger logger = Logger.getLogger( Nic.class );

}
