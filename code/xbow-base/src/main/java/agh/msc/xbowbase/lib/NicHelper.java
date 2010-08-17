package agh.msc.xbowbase.lib;

import agh.msc.xbowbase.link.NicInfo;
import java.util.List;


/**
 * Link helper interface.
 *
 * Used to manage and query links.
 *
 * @author cieplik
 */
public interface NicHelper {

	/**
	 * Retrieves NicInfo objects for all links in the system.
	 *
	 * @return  list of NicInfo objects
	 */
	public List< NicInfo > getNicsInfo();

}
