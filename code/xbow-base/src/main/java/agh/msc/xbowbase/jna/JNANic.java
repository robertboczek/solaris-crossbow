package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.NoSuchFlowException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.jna.mapping.IFlowadm;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.jna.util.MapToKeyValuePairsTranslator;
import agh.msc.xbowbase.lib.Flowadm;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.link.NicInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;


/**
 * Link helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNANic implements NicHelper {

	/**
	 * Creates the helper object and initializes underlying handler.
	 */
	public JNANic() {

		handle = ( LinkHandle ) Native.loadLibrary( LIB_NAME, LinkHandle.class );
		handle.init();

	}


	/**
	 * Creates the helper object using user-provided JNA handle.
	 *
	 * @param  handle  JNA handle
	 */
	public JNANic( LinkHandle handle ) {
		this.handle = handle;
	}


	/**
	 * @see  NicHelper#getNicsInfo( java.util.List )
	 */
	@Override
	public List< NicInfo > getNicsInfo() {

		List< NicInfo > res = new LinkedList< NicInfo >();

		// Call helper function.

		LinkHandle.LinkInfosStruct nicInfosStruct = handle.get_nic_infos();

		// logger.debug( "get_flows_info returned " + flowInfosStruct.flowInfosLen + " FlowInfoStruct(s)." );

		// Process returned structs.

		for ( Pointer p : nicInfosStruct.linkInfos.getPointerArray( 0, nicInfosStruct.linkInfosLen ) ) {

			LinkHandle.LinkInfoStruct struct = new LinkHandle.LinkInfoStruct( p );

			// Append to the resulting list.

			res.add( new NicInfo(
				struct.name
			) );

		}

		// Free the memory.

		// handle.free_flow_infos( flowInfosStruct );

		return res;

	}


	private static final String LIB_NAME = "link_wrapper";

	LinkHandle handle = null;

	private static final Logger logger = Logger.getLogger( JNANic.class );

}
