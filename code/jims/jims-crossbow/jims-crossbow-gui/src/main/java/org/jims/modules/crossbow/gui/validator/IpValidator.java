package org.jims.modules.crossbow.gui.validator;

/**
 * Validates ip address correctness
 * 
 * @author robert
 *
 */
public class IpValidator {
	
	/**
	 * Checks ip address format
	 * 
	 * @param address Ip address to be checked
	 * @return True if ip is correct
	 */
	public static boolean isIpv4(String address) {
		String[] tab = address.split("\\.");
		if (tab.length != 4) {
			return false;
		}
		for (String part : tab) {
			Integer i = Integer.parseInt(part);
			if (i > 255 || i < 0) {
				return false;
			}
		}
		return true;
	}

}
