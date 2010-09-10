package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.InvalidLinkNameException;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.TooLongLinkNameException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.lib.VNicHelper;
import agh.msc.xbowbase.link.validators.LinkValidator;
import org.apache.log4j.Logger;

/**
 * @brief
 * VNic helper implementation based on Java Native Access.
 *
 * @author robert boczek
 */
public class JNAVNicHelper extends JNALinkHelper implements VNicHelper{

    private static final Logger logger = Logger.getLogger(JNAVNicHelper.class);

    /**
     * Constructor of JNAVNicHelper object injecting LinkHandle object instance
     *
     * @param linkHandle Reference to LinkHandle object
     */
    public JNAVNicHelper(LinkHandle linkHandle){
        super(linkHandle);
    }
    
    /**
     * Constructor of JNAVNicHelper object injecting LinkValidator object instance
     *
     * @param linkHandle Reference to LinkHandle object
     */
    public JNAVNicHelper(LinkValidator linkValidator){
        super(linkValidator);
    }
    

    /**
     * Default constructor
     */
    public JNAVNicHelper(){

    }

    /**
     * @see  NicHelper#deleteVNic(java.lang.String, boolean)
     */
    @Override
    public void deleteVNic(String name, boolean temporary) throws LinkException {

        int persitent_type = checkPersistenceType(temporary);
        logger.info("Trying to remove vnic : " + name + ", temporary: " + temporary);
        int rc = handle.delete_vnic(name, persitent_type);

        if(rc == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()){
            throw new InvalidLinkNameException("VNic name: " + name + " was incorrect");
        }
        else if(rc == XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal()){
            throw new TooLongLinkNameException("VNic name: " + name + " was too long");
        }
        else if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {
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

        if (rc == XbowStatus.XBOW_STATUS_TOO_LONG_PARENT_NAME.ordinal()) {

            throw new TooLongLinkNameException("VNic parent link name too long");

        }
        else if (rc == XbowStatus.XBOW_STATUS_INVALID_PARENT_NAME.ordinal()) {

            throw new InvalidLinkNameException("VNic parent link was incorrect");

        }
        else if (rc == XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal()) {

            throw new TooLongLinkNameException("VNic name was too long");

        }
        else if (rc == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()) {

            throw new InvalidLinkNameException("VNic name was incorrect");

        }
        else if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {

            throw new LinkException("VNic creation failed.");

        }

        logger.info("VNic : " + name + " sucessfully created");
    }

    /**
     * Coverts type of persistence to 'c' like value
     * 
     * @param temporary Type of requested persistence
     * @return Flag specifies requested persistence type accustomed to 'c' library
     */
    private int checkPersistenceType(boolean temporary) {
        return (temporary) ? 1 : 0;
    }
}
