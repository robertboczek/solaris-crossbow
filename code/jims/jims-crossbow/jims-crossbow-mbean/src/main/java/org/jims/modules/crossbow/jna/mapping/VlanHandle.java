package org.jims.modules.crossbow.jna.mapping;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.jims.modules.crossbow.vlan.VlanInfo;


/**
 * C <-> Java mappings.
 *
 * @author cieplik
 */
public interface VlanHandle extends Library {

	/*
	 * Types
	 */


	public class VlanInfosStruct extends Structure {
		public Pointer infos;
		public int infosLen;
	}

	public class VlanInfoStruct extends Structure {

		public VlanInfoStruct() {}

		public VlanInfoStruct( Pointer p ) {
			super( p );
			read();
		}

		public VlanInfoStruct( VlanInfo info ) {

			this.name = info.getName();
			this.link = info.getLink();
			this.tag = info.getTag();

		}

		public String name, link;
		public int tag;

	}


	/*
	 * Functions
	 */

	public void init();
	public int vlan_create( VlanInfoStruct info );
	public int vlan_remove( String name );
	public VlanInfosStruct get_vlan_infos();

	public void free_vlan_infos( VlanInfosStruct vis );

}
