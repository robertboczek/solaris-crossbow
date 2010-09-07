package agh.msc.xbowbase.link.util;

import agh.msc.xbowbase.link.Nic;
import agh.msc.xbowbase.link.NicInfo;


/**
 *
 * @author cieplik
 */
public class NicToNicInfoTranslator {

	public static Nic toNic( NicInfo nicInfo ) {

		Nic nic = new Nic(nicInfo.getName());

		return nic;

	}

}
