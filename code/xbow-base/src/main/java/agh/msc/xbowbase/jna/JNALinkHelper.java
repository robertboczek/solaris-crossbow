package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.link.NicInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;


/**
 * Link helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNALinkHelper implements NicHelper {

	/**
	 * Creates the helper object and initializes underlying handler.
	 */
	public JNALinkHelper() {

		handle = ( LinkHandle ) Native.loadLibrary( LIB_NAME, LinkHandle.class );
		handle.init();

	}


	/**
	 * Creates the helper object using user-provided JNA handle.
	 *
	 * @param  handle  JNA handle
	 */
	public JNALinkHelper( LinkHandle handle ) {
		this.handle = handle;
	}


	/**
	 * @see  NicHelper#getNicsInfo( java.util.List )
	 */
	@Override
	public List< NicInfo > getNicsInfo() {

		List< NicInfo > res = new LinkedList< NicInfo >();

		// Call helper function.

		LinkHandle.NicInfosStruct nicInfosStruct = handle.get_nic_infos();

		logger.debug( "get_nic_infos returned " + nicInfosStruct.nicInfosLen + " NicInfoStruct(s)." );

		// Process returned structs.

		for ( Pointer p : nicInfosStruct.nicInfos.getPointerArray( 0, nicInfosStruct.nicInfosLen ) ) {

			LinkHandle.NicInfoStruct struct = new LinkHandle.NicInfoStruct( p );

			// Append to the resulting list.

			res.add( new NicInfo(
				struct.name
			) );

		}

		// Free the memory.

		handle.free_nic_infos( nicInfosStruct );

		return res;

	}


	private static final String LIB_NAME = "link_wrapper";

	LinkHandle handle = null;

	private static final Logger logger = Logger.getLogger( JNALinkHelper.class );

}
