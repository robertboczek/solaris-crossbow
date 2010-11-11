package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.exception.InvalidEtherstubNameException;
import org.jims.modules.crossbow.exception.TooLongEtherstubNameException;
import org.jims.modules.crossbow.jna.mapping.EtherstubHandle;
import org.jims.modules.crossbow.lib.EtherstubHelper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import org.apache.log4j.Logger;

/**
 * @brief Helper class implementation based on JNA
 * Etherstub helper implementation based on Java Native Access.
 * 
 * @author robeert boczek
 */
public class JNAEtherstubHelper implements EtherstubHelper {

    /** Logger */
    private static final Logger logger = Logger.getLogger(JNAEtherstubHelper.class);

    private final String LIBNAME = "etherstub-1.0.0";
    private EtherstubHandle handle;

    /**
     * @brief
     * Construtor of JNAEtherstubHelper - load etherstubadm native library
     */
    public JNAEtherstubHelper() {
        handle = (EtherstubHandle) Native.loadLibrary(LIBNAME, EtherstubHandle.class);
        handle.init();
    }

    public JNAEtherstubHelper(EtherstubHandle handle){
        this.handle = handle;
    }

    /**
     * @see EtherstubHelper#deleteEtherstub(java.lang.String, boolean)
     */
    @Override
    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);

        logger.info("Trying to remove etherstub : " + name + ", temporary: " + temporary);

        int rc = handle.delete_etherstub(name, persitent_type);

        if (rc == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()) {

            throw new InvalidEtherstubNameException("Etherstub couldn't be removed as the name was incorrect");

        }else if(rc != XbowStatus.XBOW_STATUS_OK.ordinal()){
            
            throw new EtherstubException("Etherstub deletion failed.");
        }
    }

    /**
     * @see EtherstubHelper#createEtherstub(java.lang.String, boolean)
    **/
    @Override
    public void createEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);

        logger.info("Trying to create etherstub : " + name + ", temporary: " + temporary);

        int rc = handle.create_etherstub(name, persitent_type);

        if(rc == XbowStatus.XBOW_STATUS_TOO_LONG_NAME.ordinal()){

            throw new  TooLongEtherstubNameException("Etherstub couldn't be created as the name was too long");

        }
        else if(rc == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()){

            throw new  InvalidEtherstubNameException("Etherstub couldn't be created as the name was incorrect");

        }
        else if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {

            throw new EtherstubException("Etherstub creation failed.");

        }
        logger.debug("Etherstub : " + name + " sucessfully created");
    }

    /**
     * @see EtherstubHelper#getEtherstubNames()
    **/
    @Override
    public String[] getEtherstubNames() throws EtherstubException {

        logger.debug("Trying to read names of existing etherstubs");

        Pointer pointer = handle.get_etherstub_names();

        if(pointer != null){
            
            String []array = pointer.getStringArray(0);
            handle.free_char_array(pointer);
            return (array != null)? array : new String[]{};
        }else{
            return new String[]{};
        }
    }

    /**
     * @see EtherstubHelper#getEtherstubParameter(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubParameters)
    **/
    @Override
    public String getEtherstubParameter(String name, LinkParameters parameter) throws EtherstubException {

        logger.debug("Trying to read etherstub's : " + name + ", parameter : " + parameter);

        Pointer p = handle.get_etherstub_parameter(name, parameter.toString());
        return getStringFromPointer(p);
    }

    /**
     * @see EtherstubHelper#getEtherstubStatistic(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubStatistics)
     */
    @Override
    public String getEtherstubStatistic(String name, LinkStatistics statistic) throws EtherstubException {

        logger.debug("Trying to read etherstub's : " + name + ", statistic : " + statistic);

        Pointer p = handle.get_etherstub_statistic(name, statistic.toString());
        return getStringFromPointer(p);
    }

    /**
     * @see EtherstubHelper#getEtherstubProperty(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubProperties)
     */
    @Override
    public void setEtherstubProperty(String name, LinkProperties property, String value) throws EtherstubException {

        logger.debug("Trying to set etherstub's : " + name + ", property : " + property + " value : " + value);

        int returnValue = handle.set_etherstub_property( name, property.toString(), value);


        if(returnValue == XbowStatus.XBOW_STATUS_OK.ordinal()){

            return;

        }
        else if (returnValue == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()) {

            throw new InvalidEtherstubNameException("Invalid etherstub name: " + name);

        } else if (returnValue == XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal()) {

            throw new EtherstubException("Unable to set property " + property);

        } else {

            throw new EtherstubException("Unknown error while setting property " + property);

        }

    }

    /**
     * @see EtherstubHelper#getEtherstubProperty(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubProperties)
     */
    @Override
    public String getEtherstubProperty(String name, LinkProperties property) throws EtherstubException {

        logger.debug("Trying to read etherstub's : " + name + ", property : " + property);

        Pointer p = handle.get_etherstub_property( name, property.toString());
        return getStringFromPointer(p);
    }

    /**
     * @brief Converts Java boolean to 'c' like proper integer value
     * Coverts type of persistence to 'c' like value
     *
     * @param temporary Type of requested persistence
     * @return Flag specifies requested persistence type accustomed to 'c' library
     */
    private int checkPersistenceType(boolean temporary) {
        return (temporary) ? 1 : 0;
    }

    /**
     * @brief Gets String value from Pointer(JNA) variable
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
}
