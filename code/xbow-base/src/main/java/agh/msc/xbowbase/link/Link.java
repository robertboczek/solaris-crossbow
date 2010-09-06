package agh.msc.xbowbase.link;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.lib.LinkHelper;
import java.util.Map;


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


	@Override
	public Map<LinkProperties, String> getProperties() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProperty(LinkProperties property, String value) throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<LinkParameters, String> getParameters() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<LinkStatistics, String> getStatistics() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getIpAddress() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setIpAddress(String ipAddress) throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
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
	public void setIpMask(String ipMask) throws LinkException {
		linkHelper.setNetmask( name, ipMask );
	}


	@Override
	public boolean isPlumbed() throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setPlumbed(boolean plumbed) throws LinkException {
		throw new UnsupportedOperationException("Not supported yet.");
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

	protected LinkHelper linkHelper;

}
