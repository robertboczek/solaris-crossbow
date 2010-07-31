package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.flow.FlowMBean;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class JNAFlowadm implements Flowadm {

	public JNAFlowadm() {

		handle = ( IFlowadm ) Native.loadLibrary( LIB_NAME, IFlowadm.class );
		handle.init();

	}


	@Override
	public int remove( String flow ) {
		return handle.remove( flow );
	}


	@Override
	public String[] getNames() {
		return handle.get_names();
	}

	@Override
	public void setAttributes( String flowName, Map< String, String > attributes ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getAttributes(String flowName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException {

		for ( Map.Entry< String, String > entry : properties.entrySet() ) {

			String values[] = entry.getValue().split( "," );
			handle.set_property( flowName, entry.getKey(), values, values.length, temporary ? 1 : 0 );

		}

	}

	@Override
	public Map< String, String > getProperties( String flowName ) {

		Map< String, String > properties = new HashMap< String, String >();

		IFlowadm.KeyValuePair kvp = handle.get_properties( flowName );
		properties.put( kvp.key, kvp.value );

		return properties;

	}

	@Override
	public void resetProperties( String flowName, List< String > properties, boolean temporary ) throws ValidationException {

		for ( String property : properties ) {
			handle.reset_property( flowName, property, temporary ? 1 : 0 );
		}

	}

	@Override
	public void create( FlowInfo flowInfo ) {
		handle.create( new IFlowadm.FlowInfoStruct( flowInfo ) );
	}

	@Override
	public List< FlowInfo > getFlowsInfo() {

		List< FlowInfo > res = new LinkedList< FlowInfo >();

		// TODO-DAWID: translator, multiple links

		IntByReference flowInfoLen = new IntByReference();

		IFlowadm.FlowInfoStruct flowInfoStruct = handle.get_flows_info(
			new String[]{ "e1000g0" },
			flowInfoLen
		);

		IFlowadm.FlowInfoStruct flowInfoStructArray[] = new IFlowadm.FlowInfoStruct[ flowInfoLen.getValue() ];
		flowInfoStruct.toArray( flowInfoStructArray );

		for ( IFlowadm.FlowInfoStruct struct : flowInfoStructArray ) {

			FlowInfo flowInfo = new FlowInfo();

			flowInfo.name = struct.name;
			flowInfo.link = struct.link;
			flowInfo.attributes = new HashMap< String, String >();
			flowInfo.properties = new HashMap< String, String >();
			flowInfo.temporary = ( struct.temporary != 0 );

			res.add( flowInfo );

		}

		return res;

	}


	private interface IFlowadm extends Library {

		public class KeyValuePair extends Structure {
			public String key, value;
		}

		public class FlowInfoStruct extends Structure {

			public FlowInfoStruct() {}


			public FlowInfoStruct( FlowInfo flowInfo ) {
				this.name = flowInfo.getName();
				this.link = flowInfo.getLink();
				this.attrs = flatten( flowInfo.attributes );
				this.props = flatten( flowInfo.properties );
				this.temporary = ( flowInfo.temporary ? 1 : 0 );
			}


			String flatten( Map< String, String > attrs ) {

				StringBuffer stringBuffer = new StringBuffer();
				final String link = ",";

				for ( Map.Entry< String, String > entry : attrs.entrySet() ) {
					stringBuffer.append( entry.getKey() + "=" + entry.getValue() + link );
				}

				stringBuffer.setLength( stringBuffer.length() - link.length() );

				return stringBuffer.toString();

			}


			/*
			String join( String arr[], String link ) {

				StringBuffer stringBuffer = new StringBuffer();
				for ( String s : arr ) {
					stringBuffer.append( s + link );
				}

				if ( stringBuffer.length() > 0 ) {
					stringBuffer.setLength( stringBuffer.length() - link.length() );
				}

				return stringBuffer.toString();

			}
			 */

			public String name, link, attrs, props;
			public int temporary;
		}

		public void init();
		public String[] get_names();
		public FlowInfoStruct get_flows_info( String links[], IntByReference flow_info_len );
		public int create( FlowInfoStruct flowInfo );
		public int remove( String flow );

		public int set_property( String flow, String key, String values[], int values_len, int temporary );
		public int reset_property( String flow, String key, int temporary );
		public KeyValuePair get_properties( String flow );

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	IFlowadm handle = null;

}
