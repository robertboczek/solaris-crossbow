package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.jna.mapping.LinkHandle;
import org.jims.modules.crossbow.lib.NicHelper;
import org.jims.modules.crossbow.link.NicInfo;
import org.jims.modules.crossbow.link.validators.LinkValidator;
import com.sun.jna.Pointer;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @brief
 * Nic helper implementation based on Java Native Access.
 *
 * @author robert boczek
 */
public class JNANicHelper extends JNALinkHelper implements NicHelper {

    private static final Logger logger = Logger.getLogger(JNANicHelper.class);

    /**
     * Constructor of JNANicHelper object injecting LinkHandle object instance
     *
     * @param linkHandle Reference to LinkHandle object
     */
    public JNANicHelper(LinkHandle linkHandle){
        super(linkHandle);
    }

    /**
     * Constructor of JNANicHelper object injecting LinkValidator object instance
     *
     * @param linkHandle Reference to LinkHandle object
     */
    public JNANicHelper( String libraryPath, LinkValidator linkValidator){
        super( libraryPath, linkValidator );
    }


    /**
     * Default constructor
     */
    public JNANicHelper(){

    }

    /**
     * @see  NicHelper#getNicsInfo( java.util.List )
     */
    @Override
    public List<NicInfo> getNicsInfo() {

        List<NicInfo> res = new LinkedList<NicInfo>();

        // Call helper function.

        LinkHandle.NicInfosStruct nicInfosStruct = handle.get_nic_infos();

        logger.debug("get_nic_infos returned " + nicInfosStruct.nicInfosLen + " NicInfoStruct(s).");

        // Process returned structs.

        for (Pointer p : nicInfosStruct.nicInfos.getPointerArray(0, nicInfosStruct.nicInfosLen)) {

            LinkHandle.NicInfoStruct struct = new LinkHandle.NicInfoStruct(p);

            // Append to the resulting list.

            res.add(new NicInfo(
                    struct.name,
                    struct.up));

        }

        // Free the memory.

        handle.free_nic_infos(nicInfosStruct);

        return res;

    }

    /**
     * @see  NicHelper#isUp( java.lang.String )
     */
    @Override
    public boolean isUp(String name) {

        LinkHandle.NicInfoStruct nicInfoStruct = handle.get_nic_info(name);
        boolean up = nicInfoStruct.up;
        handle.free_nic_info(nicInfoStruct);

        return up;

    }


}
