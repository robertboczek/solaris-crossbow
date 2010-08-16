package agh.msc.xbowbase.jna.util;

import agh.msc.xbowbase.jna.JNAFlowadm.IFlowadm.KeyValuePairStructPtr.ByReference;
import agh.msc.xbowbase.jna.JNAFlowadm.IFlowadm.KeyValuePairsStruct;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author cieplik
 */
public class MapToKeyValuePairsTranslatorTest {

	// TODO-DAWID: napisac porzadnie
	// @Test
	public void testToMap() {

		KeyValuePairsStruct kvps = new KeyValuePairsStruct();

		kvps.keyValuePairsLen = 2;

		kvps.keyValuePairs = new ByReference();
		kvps.keyValuePairs.kvp = new agh.msc.xbowbase.jna.JNAFlowadm.IFlowadm.KeyValuePairStruct.ByReference[ 2 ];
		kvps.keyValuePairs.kvp[ 0 ] = new agh.msc.xbowbase.jna.JNAFlowadm.IFlowadm.KeyValuePairStruct.ByReference();
		kvps.keyValuePairs.kvp[ 0 ].key = "k1";
		kvps.keyValuePairs.kvp[ 0 ].value = "v1";
		kvps.keyValuePairs.kvp[ 1 ] = new agh.msc.xbowbase.jna.JNAFlowadm.IFlowadm.KeyValuePairStruct.ByReference();
		kvps.keyValuePairs.kvp[ 1 ].key = "k2";
		kvps.keyValuePairs.kvp[ 1 ].value = "v2";


		Map< String, String > map = MapToKeyValuePairsTranslator.toMap( kvps );

		assertEquals( kvps.keyValuePairsLen, map.size() );

		assertTrue( map.containsKey( kvps.keyValuePairs.kvp[ 0 ].key ) );
		assertTrue( map.containsKey( kvps.keyValuePairs.kvp[ 1 ].key ) );

		assertEquals( kvps.keyValuePairs.kvp[ 0 ].value, map.get( kvps.keyValuePairs.kvp[ 0 ].key ) );
		assertEquals( kvps.keyValuePairs.kvp[ 1 ].value, map.get( kvps.keyValuePairs.kvp[ 1 ].key ) );

	}


	@Test
	public void testToKeyValuePairs() {

		Map< String, String > kvMap = new HashMap< String, String >();

		kvMap.put( "k1", "v1" );
		kvMap.put( "k2", "v2" );

		KeyValuePairsStruct kvps = MapToKeyValuePairsTranslator.toKeyValuePairs( kvMap );

		assertEquals( kvMap.size(), kvps.keyValuePairsLen );

		assertTrue( kvMap.containsKey( kvps.keyValuePairs.kvp[ 0 ].key ) );
		assertTrue( kvMap.containsKey( kvps.keyValuePairs.kvp[ 1 ].key ) );

		assertEquals( kvMap.get( kvps.keyValuePairs.kvp[ 0 ].key ), kvps.keyValuePairs.kvp[ 0 ].value );
		assertEquals( kvMap.get( kvps.keyValuePairs.kvp[ 1 ].key ), kvps.keyValuePairs.kvp[ 1 ].value );

	}

}
