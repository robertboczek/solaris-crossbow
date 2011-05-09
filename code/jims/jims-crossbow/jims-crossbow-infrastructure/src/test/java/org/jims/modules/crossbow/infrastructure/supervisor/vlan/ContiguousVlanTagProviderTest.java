package org.jims.modules.crossbow.infrastructure.supervisor.vlan;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author cieplik
 */
public class ContiguousVlanTagProviderTest {

	@Before
	public void setUp() {
		usedTagsProvider = mock( ContiguousVlanTagProvider.UsedTagsProvider.class );
	}


	@Test
	public void testOneTagAvailable() {

		tagProvider = new ContiguousVlanTagProvider( 1, 2, usedTagsProvider );

		assert ( -1 != tagProvider.provide() );
		assert ( -1 == tagProvider.provide() );

	}


	@Test
	public void testProviderReactsToAvailableTagsChange() {

		tagProvider = new ContiguousVlanTagProvider( 1, 3, usedTagsProvider );

		when( usedTagsProvider.provide() )
			.thenReturn( Arrays.asList( 1 ) );

		tagProvider.refresh();

		assert ( 2 == tagProvider.provide() );
		assert ( -1 == tagProvider.provide() );

	}


	private VlanTagProvider tagProvider;
	private ContiguousVlanTagProvider.UsedTagsProvider usedTagsProvider;

}
