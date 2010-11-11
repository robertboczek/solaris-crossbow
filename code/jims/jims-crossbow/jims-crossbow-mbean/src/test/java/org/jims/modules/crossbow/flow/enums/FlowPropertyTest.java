package org.jims.modules.crossbow.flow.enums;

import org.jims.modules.crossbow.flow.enums.*;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.exception.NoSuchEnumException;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class FlowPropertyTest {

	@Test
	public void testToString() {

		assertEquals( "maxbw",    FlowProperty.MAXBW.toString() );
		assertEquals( "priority", FlowProperty.PRIORITY.toString() );

	}


	@Test
	public void testFromString() throws NoSuchEnumException {

		assertEquals( FlowProperty.MAXBW, FlowProperty.fromString( "maxbw" ) );
		assertEquals( FlowProperty.PRIORITY, FlowProperty.fromString( "priority" ) );

	}


	@Test( expected = NoSuchEnumException.class )
	public void testFromStringThrowsException() throws NoSuchEnumException {

		FlowProperty.fromString( "Jozek" );

	}

}
