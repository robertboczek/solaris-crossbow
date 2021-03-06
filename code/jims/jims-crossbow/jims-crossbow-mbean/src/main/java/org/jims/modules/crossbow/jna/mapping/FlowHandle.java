package org.jims.modules.crossbow.jna.mapping;

import org.jims.modules.crossbow.flow.FlowInfo;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.jna.util.MapToKeyValuePairsTranslator;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;


/**
 * C <-> Java mappings.
 *
 * @author cieplik
 */
public interface FlowHandle extends Library {

	/*
	 * Types
	 */

	public class KeyValuePairsStruct extends Structure {

		public static class ByReference extends KeyValuePairsStruct implements Structure.ByReference {}

		public KeyValuePairsStruct() {}

		public KeyValuePairsStruct( Pointer p ) {
			super( p );
			read();
		}

		public KeyValuePairsStruct fill() {

			KeyValuePairStruct.ByReference tmp[] = new KeyValuePairStruct.ByReference[ keyValuePairsLen ];
			Pointer kvpsPointer = keyValuePairs.getPointer();

			for ( int i = 0; i < keyValuePairsLen; ++i ) {

				tmp[ i ] = new KeyValuePairStruct.ByReference( kvpsPointer.getPointer( Pointer.SIZE * i ) );
				tmp[ i ].read();

			}

			keyValuePairs.kvp = tmp;

			return this;

		}

		public KeyValuePairStructPtr.ByReference keyValuePairs;
		public int keyValuePairsLen;
	}

	public class KeyValuePairStructPtr extends Structure {

		public static class ByReference extends KeyValuePairStructPtr implements Structure.ByReference {}

		public KeyValuePairStruct.ByReference kvp[];

	}

	public class KeyValuePairStruct extends Structure {

		public static class ByReference extends KeyValuePairStruct implements Structure.ByReference {

			public ByReference() {}

			public ByReference( Pointer p ) {
				super( p );
				read();
			}

		}

		public KeyValuePairStruct() {}

		public KeyValuePairStruct( Pointer p ) {
			super( p );
			read();
		}

		public String key, value;

	}

	public class FlowInfosStruct extends Structure {
		public Pointer flowInfos;
		public int flowInfosLen;
	}

	public class FlowInfoStruct extends Structure {

		public FlowInfoStruct() {}

		public FlowInfoStruct( Pointer p ) {
			super( p );
			read();
		}

		public FlowInfoStruct( FlowInfo flowInfo ) {

			this.name = flowInfo.getName();
			this.link = flowInfo.getLink();
			this.attrs = MapToKeyValuePairsTranslator.toKeyValuePairs( flowInfo.getAttributes() );
			this.props = MapToKeyValuePairsTranslator.toKeyValuePairs( flowInfo.getProperties() );
			this.temporary = flowInfo.isTemporary();

		}

		public String name, link;
		public KeyValuePairsStruct.ByReference attrs, props;
		public boolean temporary;
	}

	public class FlowStatsStruct extends Structure {

		public long ipackets;
		public long rbytes;
		public long opackets;
		public long obytes;

	}

	/*
	 * Functions
	 */

	public void init();
	public FlowInfosStruct get_flows_info( String links[] );
	public int create( FlowInfoStruct flowInfo, boolean temporary );
	public int remove_flow( String flow, boolean temporary );

	public int set_property( String flow, String key, String values[], int values_len, boolean temporary );
	public int reset_property( String flow, String key, boolean temporary );
	public KeyValuePairsStruct get_properties( String flow );

	public FlowStatsStruct get_statistics( String flow, String startTime, String endTime );

	public void free_key_value_pairs( KeyValuePairsStruct kvp );
	public void free_flow_infos( FlowInfosStruct fis );
	public void free_flow_stats( FlowStatsStruct fs );

}
