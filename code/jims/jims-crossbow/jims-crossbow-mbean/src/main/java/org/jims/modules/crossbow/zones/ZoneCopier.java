package org.jims.modules.crossbow.zones;

import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 * Implementation of @see org.jims.modules.crossbow.zones.ZoneCopierMBean
 *
 * @author robert boczek
 */
public class ZoneCopier implements ZoneCopierMBean {

    private static final Logger logger = Logger.getLogger(ZoneCopier.class);


    @Override
    public boolean copyZone(String fromLocation, String toLocation) {

        Process proc = null;
        boolean result = false;
        Runtime rt = Runtime.getRuntime();
        try {
            String cmd[] = {
                "bash",
                "-c",
                "cp " + fromLocation + " " + toLocation};
            proc = rt.exec(cmd);

            Scanner outputScanner = new Scanner(proc.getInputStream());
            result = (proc.exitValue() == 0);
            outputScanner.close();
        } catch (Exception ex) {
            logger.error("Exception while copying zone has occured: " + ex.getMessage());
        }

        return result;
    }
}
