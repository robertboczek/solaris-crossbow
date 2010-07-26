package agh.msc.xbowbase.lib;

import com.sun.jna.Library;


/**
 *
 * @author cieplik
 */
public interface Flowadm extends Library {

	public int init();

	public int remove( String flow );

}
