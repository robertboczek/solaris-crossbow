package org.jims.modules.crossbow.jna.mapping;

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

	public class BufferStruct extends Structure {

		public BufferStruct() {}

		public BufferStruct( int len ) {
			buffer = new String( new char[ len ] );
			this.len = len;
		}

		public String buffer;
		public int len;

	}


	/*
	 * Functions
	 */

	public void init();
	public NicInfoStruct get_nic_info( String name );
	public NicInfosStruct get_nic_infos();

	public int plumb( String link );
	public boolean is_plumbed( String link );

	public int set_netmask( String link, String mask );
	public int get_netmask( String link, BufferStruct buffer );

	public String get_ip_address(String link);
	public int set_ip_address(String link, String address);

	public int ifconfig_up( String link, int up_down );
	public int ifconfig_is_up( String link );

	public int delete_vnic(String name, int temporary);
	public int create_vnic(String name, int temporary, String parent);
	public Pointer get_link_names(int link_type);
	public Pointer get_link_parameter(String name, String parameter);
	public Pointer get_link_statistic(String name, String statistic);
	public Pointer get_link_property( String name, String property);
	public int set_link_property( String name, String property, String value );

	public Pointer malloc_buffer( int len );

	public void free( String s );
	public void free_char_array( Pointer pointer );
	public void free_char_string( Pointer pointer );
	public void free_nic_info( NicInfoStruct nicInfoStruct );
	public void free_nic_infos( NicInfosStruct linkInfosStruct );
	public void free_buffer( Pointer buffer );

}
