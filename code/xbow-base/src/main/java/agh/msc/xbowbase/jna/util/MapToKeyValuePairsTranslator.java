package agh.msc.xbowbase.jna.util;

import agh.msc.xbowbase.jna.JNAFlowadm;
import agh.msc.xbowbase.jna.mapping.IFlowadm;
import com.sun.jna.Pointer;
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
	public static Map< String, String > toMap( IFlowadm.KeyValuePairsStruct kvps ) {

		Map< String, String > map = new HashMap< String, String >();

		if ( kvps.keyValuePairsLen > 0 ) {

			for ( int i = 0; i < kvps.keyValuePairsLen; ++i ) {

				IFlowadm.KeyValuePairStruct.ByReference k = new IFlowadm.KeyValuePairStruct.ByReference( kvps.keyValuePairs.getPointer().getPointer( Pointer.SIZE * i ) );
				k.read();

				map.put( k.key, k.value );

			}

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
	public static IFlowadm.KeyValuePairsStruct.ByReference toKeyValuePairs( Map< String, String > map ) {

		IFlowadm.KeyValuePairsStruct.ByReference kvps = new IFlowadm.KeyValuePairsStruct.ByReference();

		kvps.keyValuePairs = new IFlowadm.KeyValuePairStructPtr.ByReference();
		kvps.keyValuePairs.kvp = new IFlowadm.KeyValuePairStruct.ByReference[ map.size() ];

		kvps.keyValuePairsLen = map.size();

		int i = 0;

		for ( Entry< String, String > entry : map.entrySet() ) {

			kvps.keyValuePairs.kvp[ i ] = new IFlowadm.KeyValuePairStruct.ByReference();

			kvps.keyValuePairs.kvp[ i ].key = entry.getKey();
			kvps.keyValuePairs.kvp[ i ].value = entry.getValue();

			++i;

		}

		return kvps;

	}

}
