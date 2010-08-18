package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.jna.mapping.FlowHandle;
import agh.msc.xbowbase.jna.util.MapToKeyValuePairsTranslator;
import agh.msc.xbowbase.lib.FlowHelper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Flow helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNAFlowHelper implements FlowHelper {

	/**
	 * Creates the helper object and initializes underlying handler.
	 */
	public JNAFlowHelper() {

		handle = ( FlowHandle ) Native.loadLibrary( LIB_NAME, FlowHandle.class );
		handle.init();

	}


	/**
	 * Creates the helper object using user-provided JNA handle.
	 *
	 * @param  handle  JNA handle
	 */
	public JNAFlowHelper( FlowHandle handle ) {
		this.handle = handle;
	}


	/**
	 * @see  FlowHelper#remove(java.lang.String, boolean)
	 */
	@Override
	public void remove( String flow, boolean temporary ) throws XbowException,
	                                                            NoSuchFlowException {

		int rc = handle.remove_flow( flow, temporary );

		logger.debug( "remove_flow returned with rc == " + rc + " ." );

		// If remove_flow failed, map rc to exception and throw it.

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {

			if ( XbowStatus.XBOW_STATUS_NOTFOUND.ordinal() == rc ) {
				throw new NoSuchFlowException( flow );
			} else {
				throw new XbowException( "Could not remove " + flow );
			}
		}

	}


	/**
	 * @see  FlowHelper#getAttributes(java.lang.String)
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
	 * @see  FlowHelper#setProperties(java.lang.String, java.util.Map, boolean)
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
	 * @see  FlowHelper#getProperties(java.lang.String)
	 */
	@Override
	public Map< String, String > getProperties( String flowName ) throws NoSuchFlowException {

		// TODO-DAWID: rc z helpera (obsluga sytuacji, gdy flow nie istnieje)

		// Query the library.

		FlowHandle.KeyValuePairsStruct kvps = handle.get_properties( flowName );

		Map< String, String > properties = MapToKeyValuePairsTranslator.toMap( kvps.fill() );

		// Free the memory.

		handle.free_key_value_pairs( kvps );

		return properties;

	}


	/**
	 * @see  FlowHelper#resetProperties(java.lang.String, java.util.List, boolean)
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
	 * @see  FlowHelper#create(agh.msc.xbowbase.flow.FlowInfo)
	 */
	@Override
	public void create( FlowInfo flowInfo ) throws XbowException {

		int rc = handle.create( new FlowHandle.FlowInfoStruct( flowInfo ), flowInfo.isTemporary() );

		logger.debug( "create returned with rc == " + rc + " ." );

		if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
			throw new XbowException( "Creation failed." );
		}

	}


	/**
	 * @see  FlowHelper#getFlowsInfo()
	 */
	@Override
	public List< FlowInfo > getFlowsInfo() {
		return getFlowsInfo( null );
	}


	/**
	 * @see  FlowHelper#getFlowsInfo(java.util.List)
	 */
	@Override
	public List< FlowInfo > getFlowsInfo( List< String > links ) {

		List< FlowInfo > res = new LinkedList< FlowInfo >();

		// Call helper function.

		FlowHandle.FlowInfosStruct flowInfosStruct = handle.get_flows_info(
			( null == links ) ? null : ( String[] ) links.toArray()
		);

		logger.debug( "get_flows_info returned " + flowInfosStruct.flowInfosLen + " FlowInfoStruct(s)." );

		// Process returned structs.

		for ( Pointer p : flowInfosStruct.flowInfos.getPointerArray( 0, flowInfosStruct.flowInfosLen ) ) {

			FlowHandle.FlowInfoStruct struct = new FlowHandle.FlowInfoStruct( p );

			// Append to the resulting list.

			res.add( new FlowInfo(
				struct.name,
				struct.link,
				MapToKeyValuePairsTranslator.toMap( struct.attrs.fill() ),
				MapToKeyValuePairsTranslator.toMap( struct.props.fill() ),
				struct.temporary
			) );

		}

		// Free the memory.

		handle.free_flow_infos( flowInfosStruct );

		return res;

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	FlowHandle handle = null;

	private static final Logger logger = Logger.getLogger( JNAFlowHelper.class );

}
