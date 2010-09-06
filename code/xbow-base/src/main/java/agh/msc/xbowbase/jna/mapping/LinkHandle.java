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

	public int plumb( String link );
	public int set_netmask( String link, String mask );
	public String get_netmask( String link );

	public int delete_vnic(String name, int temporary);
	public int create_vnic(String name, int temporary, String parent);
	public Pointer get_link_names(int link_type);
	public Pointer get_link_parameter(String name, String parameter);
	public Pointer get_link_statistic(String name, String statistic);
	public Pointer get_link_property( String name, String property);
	public int set_link_property( String name, String property, String value );

	public void free_char_array( Pointer pointer );
	public void free_char_string( Pointer pointer );


	public void free_nic_info( NicInfoStruct nicInfoStruct );
	public void free_nic_infos( NicInfosStruct linkInfosStruct );

}
