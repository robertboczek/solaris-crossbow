package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Flow helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNAFlowadm implements Flowadm {

	/**
	 * Creates the helper object and initializes underlying handler.
	 */
	public JNAFlowadm() {

		handle = ( IFlowadm ) Native.loadLibrary( LIB_NAME, IFlowadm.class );
		handle.init();

	}


	/**
	 * Creates the helper object using user-provided JNA handle.
	 *
	 * @param  handle  JNA handle
	 */
	public JNAFlowadm( IFlowadm handle ) {
		this.handle = handle;
	}


	/**
	 * @see  Flowadm#remove(java.lang.String, boolean)
	 */
	@Override
	public void remove( String flow, boolean temporary ) throws XbowException,
	                                                            NoSuchFlowException {

		int rc = handle.remove_flow( flow, temporary );

		logger.debug( "remove_flow returned with rc == " + rc + " ." );

		// If remove_flow didn't finish successfully, map rc to exception and throw it.

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {

			if ( XbowStatus.XBOW_STATUS_NOTFOUND.ordinal() == rc ) {
				throw new NoSuchFlowException( flow );
			} else {
				throw new XbowException( "Could not remove " + flow );
			}
		}

	}


	/**
	 * @see  Flowadm#getAttributes(java.lang.String)
	 */
	@Override
	public Map< String, String > getAttributes( String flowName ) throws NoSuchFlowException {

		for ( FlowInfo flowInfo : getFlowsInfo() ) {
			if ( flowInfo.getName().equals( flowName ) ) {
				return flowInfo.getAttributes();
			}
		}

		// The flow could not be found - raise exception.

		throw new NoSuchFlowException( flowName );

	}


	/**
	 * @see  Flowadm#setProperties(java.lang.String, java.util.Map, boolean)
	 */
	@Override
	public void setProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException {

		// Call set_property sequentially, each time setting single property.

		for ( Map.Entry< String, String > entry : properties.entrySet() ) {

			String values[] = entry.getValue().split( "," );

			int rc = handle.set_property( flowName, entry.getKey(), values, values.length, temporary );

			logger.debug( "set_property returned with rc == " + rc + " ." );

			// Check the rc and map it to exception, if necessary.

			if ( rc == XbowStatus.XBOW_STATUS_PROP_PARSE_ERR.ordinal() ) {

				throw new ValidationException( entry.getKey() + "=" + values );

			}

		}

	}


	/**
	 * @see  Flowadm#getProperties(java.lang.String)
	 */
	@Override
	public Map< String, String > getProperties( String flowName ) throws NoSuchFlowException {

		// TODO-DAWID: rc z helpera (obsluga sytuacji, gdy flow nie istnieje)

		Map< String, String > properties = new HashMap< String, String >();

		// Query the library.

		IFlowadm.KeyValuePairsStruct kvps = handle.get_properties( flowName );

		// Put the properties into map.

		for ( Pointer p : kvps.keyValuePairs.getPointerArray( 0, kvps.keyValuePairsLen ) ) {

			IFlowadm.KeyValuePairStruct kvp = new IFlowadm.KeyValuePairStruct( p );
			properties.put( kvp.key, kvp.value );

		}

		// Free the memory.

		handle.free_key_value_pairs( kvps );

		return properties;

	}


	/**
	 * @see  Flowadm#resetProperties(java.lang.String, java.util.List, boolean)
	 */
	@Override
	public void resetProperties( String flowName, List< String > properties, boolean temporary )
		throws NoSuchFlowException,
		       ValidationException {

		// TODO-DAWID: NoSuchFlowException

		// Reset properties sequentially.

		for ( String property : properties ) {

			int rc = handle.reset_property( flowName, property, temporary );

			logger.debug( "reset_property returned with rc == " + rc );

			// Check the rc and map it to exception, if necessary.

			if ( XbowStatus.XBOW_STATUS_PROP_PARSE_ERR.ordinal() == rc ) {
				throw new ValidationException( property );
			}

		}

	}


	/**
	 * @see  Flowadm#create(agh.msc.xbowbase.flow.FlowInfo)
	 */
	@Override
	public void create( FlowInfo flowInfo ) throws XbowException {

		int rc = handle.create( new IFlowadm.FlowInfoStruct( flowInfo ) );

		logger.debug( "create returned with rc == " + rc + " ." );

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
			throw new XbowException( "Creation failed." );
		}

	}


	/**
	 * @see  Flowadm#getFlowsInfo()
	 */
	@Override
	public List< FlowInfo > getFlowsInfo() {
		return getFlowsInfo( null );
	}


	/**
	 * @see  Flowadm#getFlowsInfo(java.util.List)
	 */
	@Override
	public List< FlowInfo > getFlowsInfo( List< String > links ) {

		List< FlowInfo > res = new LinkedList< FlowInfo >();

		// Call helper function.

		IFlowadm.FlowInfosStruct flowInfosStruct = handle.get_flows_info(
			( null == links ) ? null : ( String[] ) links.toArray()
		);

		logger.debug( "get_flows_info returned " + flowInfosStruct.flowInfosLen + " FlowInfoStruct(s)." );

		// Process returned structs.

		for ( Pointer p : flowInfosStruct.flowInfos.getPointerArray( 0, flowInfosStruct.flowInfosLen ) ) {

			IFlowadm.FlowInfoStruct struct = new IFlowadm.FlowInfoStruct( p );

			Map< String, String > attrs = new HashMap< String, String >();

			for ( String attr : struct.attrs.split( "," ) ) {
				attrs.put( attr.split( "=" )[ 0 ], attr.split( "=" )[ 1 ] );
			}

			// Append to the resulting list.

			res.add( new FlowInfo(
				struct.name,
				struct.link,
				attrs,
				new HashMap< String, String >(),
				struct.temporary
			) );

		}

		// Free the memory.

		handle.free_flow_infos( flowInfosStruct );

		return res;

	}


	// TODO-DAWID: v that becomes ugly and has to be refactored


	/**
	 * C <-> Java mappings.
	 */
	public interface IFlowadm extends Library {

		/*
		 * Types
		 */

		public class KeyValuePairsStruct extends Structure {
			public Pointer keyValuePairs;
			public int keyValuePairsLen;
		}

		public class KeyValuePairStruct extends Structure {

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

		/*
		 * Functions
		 */

		public void init();
		public FlowInfosStruct get_flows_info( String links[] );
		public int create( FlowInfoStruct flowInfo );
		public int remove_flow( String flow, boolean temporary );

		public int set_property( String flow, String key, String values[], int values_len, boolean temporary );
		public int reset_property( String flow, String key, boolean temporary );
		public KeyValuePairsStruct get_properties( String flow );

		public void free_key_value_pairs( KeyValuePairsStruct kvp );
		public void free_flow_infos( FlowInfosStruct fis );

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	IFlowadm handle = null;

	private static final Logger logger = Logger.getLogger( JNAFlowadm.class );

}
