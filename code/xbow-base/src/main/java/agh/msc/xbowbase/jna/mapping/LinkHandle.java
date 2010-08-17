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

	public class LinkInfosStruct extends Structure {
		public Pointer linkInfos;
		public int linkInfosLen;
	}

	public class LinkInfoStruct extends Structure {

		public LinkInfoStruct( Pointer p ) {
			super( p );
			read();
		}

		public String name;
	}


	/*
	 * Functions
	 */

	public void init();
	public LinkInfosStruct get_nic_infos();

}
