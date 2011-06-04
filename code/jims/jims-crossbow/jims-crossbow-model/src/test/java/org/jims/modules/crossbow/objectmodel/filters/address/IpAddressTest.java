package org.jims.modules.crossbow.objectmodel.filters.address;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class IpAddressTest {

	@Test
	public void testFromStringConversion() {

		IpAddress ipAddress;

		ipAddress = IpAddress.fromString( "192.168.1.0" );

		assertEquals( "192.168.1.0", ipAddress.getAddress() );
		assertEquals( 24, ipAddress.getNetmask() );

		ipAddress = IpAddress.fromString( "1.2.3.4/22" );

		assertEquals( "1.2.3.4", ipAddress.getAddress() );
		assertEquals( 22, ipAddress.getNetmask() );

	}

}
