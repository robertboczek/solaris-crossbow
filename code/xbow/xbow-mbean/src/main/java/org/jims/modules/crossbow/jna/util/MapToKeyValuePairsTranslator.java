package org.jims.modules.crossbow.jna.util;

import org.jims.modules.crossbow.exception.NoSuchEnumException;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.jna.mapping.FlowHandle;
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
	 * @brief  Translates between KeyValuePairsStruct instance and Map< FlowAttribute, String > instance.
	 *
	 * @param  kvps  KeyValuePairsStruct instance
	 *
	 * @throws  NoSuchEnumException  Could not map one of kvps's keys to FlowAttribute.
	 *
	 * @return  Map< FlowAttribute, String > instance
	 */
	public static Map< FlowAttribute, String > toAttrMap( FlowHandle.KeyValuePairsStruct kvps )
		throws NoSuchEnumException {

		Map< FlowAttribute, String > map = new HashMap< FlowAttribute, String >();

		for ( FlowHandle.KeyValuePairStruct kvp : kvps.keyValuePairs.kvp ) {
			map.put( FlowAttribute.fromString( kvp.key ), kvp.value );
		}

		return map;

	}


	/**
	 * @brief  Translates between KeyValuePairsStruct instance and Map< FlowProperty, String > instance.
	 *
	 * @param  kvps  KeyValuePairsStruct instance
	 *
	 * @throws  NoSuchEnumException  Could not map one of kvps's keys to FlowProperty.
	 *
	 * @return  Map< FlowProperty, String > instance
	 */
	public static Map< FlowProperty, String > toPropMap( FlowHandle.KeyValuePairsStruct kvps )
		throws NoSuchEnumException {

		Map< FlowProperty, String > map = new HashMap< FlowProperty, String >();

		for ( FlowHandle.KeyValuePairStruct kvp : kvps.keyValuePairs.kvp ) {
			map.put( FlowProperty.fromString( kvp.key ), kvp.value );
		}

		return map;

	}


	/**
	 * @brief  Translates between Map< K, String > instance and KeyValuePairsStruct instance.
	 *
	 * @param  map  Map< K, String > instance
	 *
	 * @return  KeyValuePairsStruct instance
	 */
	public static < K > FlowHandle.KeyValuePairsStruct.ByReference toKeyValuePairs( Map< K, String > map ) {

		FlowHandle.KeyValuePairsStruct.ByReference kvps = new FlowHandle.KeyValuePairsStruct.ByReference();

		kvps.keyValuePairs = new FlowHandle.KeyValuePairStructPtr.ByReference();
		kvps.keyValuePairs.kvp = new FlowHandle.KeyValuePairStruct.ByReference[ map.size() ];

		kvps.keyValuePairsLen = map.size();

		int i = 0;

		for ( Entry< K, String > entry : map.entrySet() ) {

			kvps.keyValuePairs.kvp[ i ] = new FlowHandle.KeyValuePairStruct.ByReference();

			kvps.keyValuePairs.kvp[ i ].key = entry.getKey().toString();
			kvps.keyValuePairs.kvp[ i ].value = entry.getValue();

			++i;

		}

		return kvps;

	}

}
