package org.jims.modules.crossbow.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.jna.mapping.VlanHandle;
import org.jims.modules.crossbow.lib.VlanHelper;
import org.jims.modules.crossbow.vlan.VlanInfo;


public class JNAVlanHelper implements VlanHelper {

	public JNAVlanHelper( String libraryPath ) {

		String filePath= libraryPath + File.separator + LIB_NAME;

		logger.info( "Loading Crossbow native library (path: " + filePath + ")" );
		handle = ( VlanHandle ) Native.loadLibrary( filePath, VlanHandle.class );
		logger.info( "Crossbow native library loaded." );

		handle.init();

	}


	@Override
	public void create( VlanInfo info ) {
		handle.vlan_create( new VlanHandle.VlanInfoStruct( info ) );
	}


	@Override
	public void remove( String name ) {
		handle.vlan_remove( name );
	}


	@Override
	public List< VlanInfo > getVlanInfos() {

		List< VlanInfo > res = new LinkedList< VlanInfo >();

		VlanHandle.VlanInfosStruct infos = handle.get_vlan_infos();

		for ( Pointer p : infos.infos.getPointerArray( 0, infos.infosLen ) ) {

			VlanHandle.VlanInfoStruct struct = new VlanHandle.VlanInfoStruct( p );
			res.add( new VlanInfo( struct.link, struct.name, struct.tag ) );
		
		}

		handle.free_vlan_infos( infos );

		return res;

	}


	public void setHandle( VlanHandle handle ) {
		this.handle = handle;
	}


	private VlanHandle handle;

	public static final String LIB_NAME = "libjims-crossbow-native-lib-vlan-3.0.0.so";
	private static final Logger logger = Logger.getLogger( JNAVlanHelper.class );

}
