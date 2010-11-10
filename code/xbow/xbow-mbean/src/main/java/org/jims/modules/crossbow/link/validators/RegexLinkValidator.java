package org.jims.modules.crossbow.link.validators;

import java.util.regex.Pattern;

/**
 * Class valids the ip v.4 address format
 *
 * @author robert boczek
 */
public class RegexLinkValidator implements LinkValidator {

    private final Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    @Override
    public final boolean isIpAddressValid(String ipAddress) {

        boolean bool = ipPattern.matcher(ipAddress).matches();
        if (bool) {
            String[] ipAry = ipAddress.split("\\.");

            for (int i = 0; i < ipAry.length; i++) {
                int value = Integer.parseInt(ipAry[i]);
                if ((value < 0) || (value > 255)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
