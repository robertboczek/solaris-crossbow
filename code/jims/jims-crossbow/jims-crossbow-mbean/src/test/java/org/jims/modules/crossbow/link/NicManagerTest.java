package org.jims.modules.crossbow.link;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.jims.modules.crossbow.lib.NicHelper;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class NicManagerTest {

	@Before
	public void setUp() {

		nicHelper = mock( NicHelper.class );

		nicManager = new NicManager();
		nicManager.setNicHelper( nicHelper );

	}


	@Test
	public void testGettingNicsList() {

		List< String > names = Arrays.asList( "anic", "another nic" );
		List< NicInfo > nicInfos = new LinkedList< NicInfo >();

		for ( String name : names ) {
			nicInfos.add( new NicInfo( name, true ) );
		}

		when( nicHelper.getNicsInfo() )
			.thenReturn( nicInfos );

		for ( String nic : nicManager.getNicsList() ) {
			assertTrue( names.contains( nic ) );
		}

	}


	NicManager nicManager;
	NicHelper nicHelper;

}
