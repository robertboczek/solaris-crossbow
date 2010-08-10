package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
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
	public void remove( String flow, boolean temporary ) throws XbowException {

		int rc = handle.remove_flow( flow, temporary );

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
			throw new XbowException( "Could not remove " + flow );
		}
	}


	@Override
	public void setAttributes( String flowName, Map< String, String > attributes ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map< String, String > getAttributes( String flowName ) {

		for ( FlowInfo flowInfo : getFlowsInfo() ) {
			if ( flowInfo.getName().equals( flowName ) ) {
				return flowInfo.getAttributes();
			}
		}

		return null;

	}

	@Override
	public void setProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException {

		for ( Map.Entry< String, String > entry : properties.entrySet() ) {

			String values[] = entry.getValue().split( "," );

			int rc = handle.set_property( flowName, entry.getKey(), values, values.length, temporary );

			if ( rc == XbowStatus.XBOW_STATUS_PROP_PARSE_ERR.ordinal() ) {

				throw new ValidationException( entry.getKey() + "=" + values );

			}

		}

	}

	@Override
	public Map< String, String > getProperties( String flowName ) {

		Map< String, String > properties = new HashMap< String, String >();

		// TODO-DAWID: use array of kvp

		IFlowadm.KeyValuePair kvp = handle.get_properties( flowName );
		handle.free_key_value_pair( kvp );

		for ( String entry : kvp.value.split( "," ) ) {

			if ( entry.split( "=" ).length > 1 ) {
				properties.put( entry.split( "=" )[ 0 ], entry.split( "=" )[ 1 ] );
			}

		}

		return properties;

	}

	@Override
	public void resetProperties( String flowName, List< String > properties, boolean temporary ) throws ValidationException {

		for ( String property : properties ) {
			handle.reset_property( flowName, property, temporary );
		}

	}

	@Override
	public void create( FlowInfo flowInfo ) throws XbowException {

		int rc = handle.create( new IFlowadm.FlowInfoStruct( flowInfo ) );

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
			throw new XbowException( "Creation failed." );
		}

	}


	@Override
	public List< FlowInfo > getFlowsInfo() {
		return getFlowsInfo( null );
	}


	@Override
	public List< FlowInfo > getFlowsInfo( List< String > links ) {

		List< FlowInfo > res = new LinkedList< FlowInfo >();

		IntByReference flowInfoLen = new IntByReference();

		IFlowadm.FlowInfosStruct flowInfosStruct = handle.get_flows_info(
			( null == links ) ? null : ( String[] ) links.toArray()
		);

		for ( Pointer p : flowInfosStruct.flowInfos.getPointerArray( 0, flowInfosStruct.flowInfosLen ) ) {

			IFlowadm.FlowInfoStruct struct = new IFlowadm.FlowInfoStruct( p );

			Map< String, String > attrs = new HashMap< String, String >();

			for ( String attr : struct.attrs.split( "," ) ) {
				attrs.put( attr.split( "=" )[ 0 ], attr.split( "=" )[ 1 ] );
			}

			res.add( new FlowInfo(
				struct.name,
				struct.link,
				attrs,
				new HashMap< String, String >(),
				struct.temporary
			) );

		}

		handle.free_flow_infos( flowInfosStruct );

		return res;

	}


	private interface IFlowadm extends Library {

		public class KeyValuePair extends Structure {
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
				this.attrs = flatten( flowInfo.getAttributes() );
				this.props = flatten( flowInfo.getProperties() );
				this.temporary = flowInfo.isTemporary();
			}


			String flatten( Map< String, String > attrs ) {

				StringBuffer stringBuffer = new StringBuffer();
				final String separator = ",";

				for ( Map.Entry< String, String > entry : attrs.entrySet() ) {
					stringBuffer.append( entry.getKey() + "=" + entry.getValue() + separator );
				}

				stringBuffer.setLength( stringBuffer.length() - separator.length() );

				return stringBuffer.toString();

			}


			public String name, link, attrs, props;
			public boolean temporary;
		}

		public void init();
		public FlowInfosStruct get_flows_info( String links[] );
		public int create( FlowInfoStruct flowInfo );
		public int remove_flow( String flow, boolean temporary );

		public int set_property( String flow, String key, String values[], int values_len, boolean temporary );
		public int reset_property( String flow, String key, boolean temporary );
		public KeyValuePair get_properties( String flow );

		public void free_key_value_pair( KeyValuePair kvp );
		public void free_flow_infos( FlowInfosStruct fis );

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	IFlowadm handle = null;

}
