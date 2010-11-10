package org.jims.modules.crossbow.flow.enums;

import org.jims.modules.crossbow.flow.enums.*;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.exception.NoSuchEnumException;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class FlowAttributeTest {

	@Test
	public void testToString() {

		assertEquals( "remote_port", FlowAttribute.REMOTE_PORT.toString() );
		assertEquals( "local_port",  FlowAttribute.LOCAL_PORT.toString() );
		assertEquals( "transport",   FlowAttribute.TRANSPORT.toString() );
		assertEquals( "dsfield",     FlowAttribute.DSFIELD.toString() );
		assertEquals( "remote_ip",   FlowAttribute.REMOTE_IP.toString() );
		assertEquals( "local_ip",    FlowAttribute.LOCAL_IP.toString() );

	}


	@Test
	public void testFromString() throws NoSuchEnumException {

		assertEquals( FlowAttribute.REMOTE_PORT, FlowAttribute.fromString( "remote_port" ) );
		assertEquals( FlowAttribute.LOCAL_PORT, FlowAttribute.fromString( "local_port" ) );
		assertEquals( FlowAttribute.TRANSPORT, FlowAttribute.fromString( "transport" ) );
		assertEquals( FlowAttribute.DSFIELD, FlowAttribute.fromString( "dsfield" ) );
		assertEquals( FlowAttribute.REMOTE_IP, FlowAttribute.fromString( "remote_ip" ) );
		assertEquals( FlowAttribute.LOCAL_IP, FlowAttribute.fromString( "local_ip" ) );

	}


	@Test( expected = NoSuchEnumException.class )
	public void testFromStringThrowsException() throws NoSuchEnumException {

		FlowAttribute.fromString( "Ala Makota" );

	}

}
