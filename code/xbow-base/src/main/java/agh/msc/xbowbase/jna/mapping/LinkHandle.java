package agh.msc.xbowbase.jna.mapping;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;


/**
 *
 * @author cieplik
 */
public interface LinkHandle extends Library {

	/*
	 * Types
	 */

	public class NicInfosStruct extends Structure {
		public Pointer nicInfos;
		public int nicInfosLen;
	}

	public class NicInfoStruct extends Structure {

		public NicInfoStruct() {}

		public NicInfoStruct( Pointer p ) {
			super( p );
			read();
		}

		public String name;
		public boolean up;
	}


	/*
	 * Functions
	 */

	public void init();
	public NicInfoStruct get_nic_info( String name );
	public NicInfosStruct get_nic_infos();

	public void free_nic_info( NicInfoStruct nicInfoStruct );
	public void free_nic_infos( NicInfosStruct linkInfosStruct );

}
