package org.jims.modules.crossbow.link.validators;

/**
 * Interface with methods used to validate link properties
 *
 * @author robert boczek
 */
public interface LinkValidator {

    /**
     * Validates correct format of ipv4
     *
     * @param ipAddress Ip address to check
     * @return True if ip format is correct, false otherwise
     */
    public boolean isIpAddressValid(String ipAddress);

}
