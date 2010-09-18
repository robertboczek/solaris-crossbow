package agh.msc.xbowbase.flow.enums;

import agh.msc.xbowbase.exception.NoSuchEnumException;
import java.io.Serializable;


/**
 * @brief  Supported flow attributes.
 *
 * @author cieplik
 */
public enum FlowAttribute implements Serializable {

	REMOTE_PORT,
	LOCAL_PORT,
	TRANSPORT,
	DSFIELD,
	REMOTE_IP,
	LOCAL_IP;


	@Override
	public String toString() {

		switch ( this ) {

			case REMOTE_PORT: return "remote_port";
			case LOCAL_PORT:  return "local_port";
			case TRANSPORT:   return "transport";
			case DSFIELD:     return "dsfield";
			case REMOTE_IP:   return "remote_ip";
			case LOCAL_IP:    return "local_ip";
			default:          return "";

		}

	}


	public static FlowAttribute fromString( String key ) throws NoSuchEnumException {

		if ( key.equals( "remote_port" ) ) {
			return REMOTE_PORT;
		} else if ( key.equals( "local_port" ) ) {
			return LOCAL_PORT;
		} else if ( key.equals( "transport" ) ) {
			return TRANSPORT;
		} else if ( key.equals( "dsfield" ) ) {
			return DSFIELD;
		} else if ( key.equals( "remote_ip" ) ) {
			return REMOTE_IP;
		} else if ( key.equals( "local_ip" ) ) {
			return LOCAL_IP;
		}

		throw new NoSuchEnumException( key );

	}

}
