package agh.msc.xbowbase.jna.mapping;

import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.jna.util.MapToKeyValuePairsTranslator;
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

	public void free_key_value_pairs( KeyValuePairsStruct kvp );
	public void free_flow_infos( FlowInfosStruct fis );

}
