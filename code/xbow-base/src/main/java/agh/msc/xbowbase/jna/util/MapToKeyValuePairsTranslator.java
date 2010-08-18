package agh.msc.xbowbase.jna.util;

import agh.msc.xbowbase.jna.mapping.FlowHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Translator class used to transform between Map< String, String > and KeyValuePairsStruct classes.
 *
 * @author cieplik
 */
public class MapToKeyValuePairsTranslator {

	/**
	 * @brief  Translates between KeyValuePairsStruct instance and Map< String, String > instance.
	 *
	 * @param  kvps  KeyValuePairsStruct instance
	 *
	 * @return  Map< String, String > instance
	 */
	public static Map< String, String > toMap( FlowHandle.KeyValuePairsStruct kvps ) {

		Map< String, String > map = new HashMap< String, String >();

		for ( FlowHandle.KeyValuePairStruct kvp : kvps.keyValuePairs.kvp ) {
			map.put( kvp.key, kvp.value );
		}

		return map;

	}


	/**
	 * @brief  Translates between Map< String, String > instance and KeyValuePairsStruct instance.
	 *
	 * @param  map  Map< String, String > instance
	 *
	 * @return  KeyValuePairsStruct instance
	 */
	public static FlowHandle.KeyValuePairsStruct.ByReference toKeyValuePairs( Map< String, String > map ) {

		FlowHandle.KeyValuePairsStruct.ByReference kvps = new FlowHandle.KeyValuePairsStruct.ByReference();

		kvps.keyValuePairs = new FlowHandle.KeyValuePairStructPtr.ByReference();
		kvps.keyValuePairs.kvp = new FlowHandle.KeyValuePairStruct.ByReference[ map.size() ];

		kvps.keyValuePairsLen = map.size();

		int i = 0;

		for ( Entry< String, String > entry : map.entrySet() ) {

			kvps.keyValuePairs.kvp[ i ] = new FlowHandle.KeyValuePairStruct.ByReference();

			kvps.keyValuePairs.kvp[ i ].key = entry.getKey();
			kvps.keyValuePairs.kvp[ i ].value = entry.getValue();

			++i;

		}

		return kvps;

	}

}
