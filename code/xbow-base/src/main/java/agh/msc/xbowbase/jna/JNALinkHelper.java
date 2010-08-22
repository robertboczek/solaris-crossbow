package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.InvalidLinkNameException;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.link.NicInfo;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @brief
 * Link helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNALinkHelper implements NicHelper {

    /**
     * Creates the helper object and initializes underlying handler.
     */
    public JNALinkHelper() {

        handle = (LinkHandle) Native.loadLibrary(LIB_NAME, LinkHandle.class);
        handle.init();

    }

    /**
     * Creates the helper object using user-provided JNA handle.
     *
     * @param  handle  JNA handle
     */
    public JNALinkHelper(LinkHandle handle) {
        this.handle = handle;
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

    /**
     * @see  NicHelper#deleteVNic(java.lang.String, boolean)
     */
    @Override
    public void deleteVNic(String name, boolean temporary) throws LinkException {

        int persitent_type = checkPersistenceType(temporary);
        logger.info("Trying to remove vnic : " + name + ", temporary: " + temporary);
        int rc = handle.delete_vnic(name, persitent_type);

        if(rc == LinkReturn.INVALID_LINK_NAME.ordinal()){
            throw new InvalidLinkNameException("VNic name: " + name + " was incorrect");
        }
        else if(rc == LinkReturn.TOO_LONG_LINK_NAME.ordinal()){
            throw new InvalidLinkNameException("VNic name: " + name + " was too long");
        }
        else if (rc != LinkReturn.RESULT_OK.ordinal()) {
            throw new LinkException("VNic deletion failed.");
        }
    }

    /**
     * @see  NicHelper#createVNic(java.lang.String, boolean, java.lang.String)
     */
    @Override
    public void createVNic(String name, boolean temporary, String parent) throws LinkException {

        int persitent_type = checkPersistenceType(temporary);
        logger.info("Trying to create vnic : " + name + ", temporary: " + temporary);
        int rc = handle.create_vnic(name, persitent_type, parent);

        if (rc == LinkReturn.TOO_LONG_PARENT_LINK_NAME.ordinal()) {
            throw new InvalidLinkNameException("VNic parent link name too long");
        }
        else if (rc == LinkReturn.INVALID_PARENT_LINK_NAME.ordinal()) {
            throw new InvalidLinkNameException("VNic parent link was incorrect");
        }
        else if (rc == LinkReturn.TOO_LONG_LINK_NAME.ordinal()) {
            throw new InvalidLinkNameException("VNic name was too long");
        }
        else if (rc == LinkReturn.INVALID_LINK_NAME.ordinal()) {
            throw new InvalidLinkNameException("VNic name was incorrect");
        }
        else if (rc != LinkReturn.RESULT_OK.ordinal()) {
            throw new LinkException("VNic creation failed.");
        }
        
        logger.info("VNic : " + name + " sucessfully created");
    }

    /**
     * @see  NicHelper#getLinkNames(boolean)
     */
    @Override
    public String[] getLinkNames(boolean isVNic) throws LinkException {

        logger.info("Trying to read names of links");
        int typeOfLink = isVNic ? 0 : 1;
        Pointer pointer = handle.get_link_names(typeOfLink);

        if(pointer != null){

            String []array = pointer.getStringArray(0);
            handle.free_char_array(pointer);
            return array;
        }else{
            return new String[]{};
        }

    }

    /**
     * @see  NicHelper#getLinkParameter(java.lang.String, agh.msc.xbowbase.enums.LinkParameters)
     */
    @Override
    public String getLinkParameter(String name, LinkParameters parameter) throws LinkException {

        logger.info("Trying to read links's : " + name + ", parameter : " + parameter);

        Pointer p = handle.get_link_parameter(name, parameter.toString());
        return getStringFromPointer(p);

    }

    /**
     * @see  NicHelper#getLinkStatistic(java.lang.String, agh.msc.xbowbase.enums.LinkStatistics)
     */
    @Override
    public String getLinkStatistic(String name, LinkStatistics statistic) throws LinkException {

        logger.info("Trying to read links's : " + name + ", statistic : " + statistic);

        Pointer p = handle.get_link_statistic(name, statistic.toString());
        return getStringFromPointer(p);

    }

    /**
     * @see  NicHelper#setLinkProperty(java.lang.String, agh.msc.xbowbase.enums.LinkProperties, java.lang.String)
     */
    @Override
    public void setLinkProperty(String name, LinkProperties property, String value) throws LinkException {

        logger.info("Trying to set links's : " + name + ", property : " + property + " value : " + value);

        int returnValue = handle.set_link_property( name, property.toString(), value);


        String errorMessage;
        
        if (returnValue == LinkReturn.RESULT_OK.ordinal()) {
            errorMessage = null;
        } else if (returnValue == LinkReturn.INVALID_LINK_NAME.ordinal()) {
            errorMessage = "Invalid link name while setting property: " + property;
        } else if (returnValue == LinkReturn.TOO_LONG_LINK_NAME.ordinal()) {
            errorMessage = "Link name too long error while setting property: " + property;
        } else if (returnValue == LinkReturn.INVALID_PARENT_LINK_NAME.ordinal()) {
            errorMessage = "Invalid parent link name error while setting property: " + property;
        } else if (returnValue == LinkReturn.TOO_LONG_PARENT_LINK_NAME.ordinal()) {
            errorMessage = "Parent link name too long error while setting property: " + property;
        } else{
            errorMessage = "Unknown error while setting property: " + property;
        }

        if(errorMessage != null){
            throw new LinkException(errorMessage);
        }
        
    }

    /**
     * @see  NicHelper#getLinkProperty(java.lang.String, agh.msc.xbowbase.enums.LinkProperties)
     */
    @Override
    public String getLinkProperty(String name, LinkProperties property) throws LinkException {

        logger.info("Trying to read link's : " + name + ", property : " + property);

        Pointer p = handle.get_link_property( name, property.toString());
        return getStringFromPointer(p);

    }

    /**
     * Coverts type of persistence to 'c' like value
     * @param temporary Type of requested persistence
     * @return Flag specifies requested persistence type accustomed to 'c' library
     */
    private int checkPersistenceType(boolean temporary) {
        return (temporary) ? 1 : 0;
    }


    /**
     * Method return string on which Pointer p points and frees the memory allocated by the library
     *
     * @param p Pointer from the JNA library
     * @return String on which pointer points
     */
    private String getStringFromPointer(Pointer p) {
        
        String value = (p != null) ? p.getString(0) : null;
        handle.free_char_string(p);

        return value;
    }
    
    private static final String LIB_NAME = "link_wrapper";
    LinkHandle handle = null;
    private static final Logger logger = Logger.getLogger(JNALinkHelper.class);
}
