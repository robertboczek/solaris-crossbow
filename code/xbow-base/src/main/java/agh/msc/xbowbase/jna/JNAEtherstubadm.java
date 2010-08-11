package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.lib.Etherstubadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import org.apache.log4j.Logger;

/**
 * Etherstub helper implementation based on Java Native Access.
 * @author robeert boczek
 */
public class JNAEtherstubadm implements Etherstubadm {

    /** Logger */
    private static final Logger logger = Logger.getLogger(JNAEtherstubadm.class);

    private final String LIBNAME = "etherstub_wrapper";
    private IEtherstubadmin handle;

    /**
     * Construtor of JNAEtherstubadm - load etherstubadm native library 
     */
    public JNAEtherstubadm() {
        handle = (IEtherstubadmin) Native.loadLibrary(LIBNAME, IEtherstubadmin.class);
        handle.init();
    }

    public JNAEtherstubadm(IEtherstubadmin handle){
        this.handle = handle;
    }

    /**
     * @see Etherstubadm#deleteEtherstub(java.lang.String, boolean) 
     */
    @Override
    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);

        logger.info("Trying to remove etherstub : " + name + ", temporary: " + temporary);

        int rc = handle.delete_etherstub(name, persitent_type);

        if (rc != EtherstubReturn.RESULT_OK.ordinal()) {
            throw new EtherstubException("Etherstub deletion failed.");
        }
    }

    /**
     * @see Etherstubadm#createEtherstub(java.lang.String, boolean)
    **/
    @Override
    public void createEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);

        logger.info("Trying to create etherstub : " + name + ", temporary: " + temporary);

        int rc = handle.create_etherstub(name, persitent_type);

        if (rc != EtherstubReturn.RESULT_OK.ordinal()) {
            throw new EtherstubException("Etherstub deletion failed.");
        }
        logger.info("Etherstub : " + name + " sucessfully created");
    }

    /**
     * @see Etherstubadm#getEtherstubNames()
    **/
    @Override
    public String[] getEtherstubNames() throws EtherstubException {

        logger.info("Trying to read names of exisitng etherstubs");

        Pointer pointer = handle.get_etherstub_names();

        if(pointer != null){
            
            String []array = pointer.getStringArray(0);
            handle.free_char_array(pointer);
            return array;
        }else{
            return new String[]{};
        }
    }

    /**
     * @see Etherstubadm#getEtherstubParameter(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubParameters)
    **/
    @Override
    public String getEtherstubParameter(String name, EtherstubParameters parameter) throws EtherstubException {

        logger.info("Trying to read etherstub's : " + name + ", parameter : " + parameter);

        Pointer p = handle.get_etherstub_parameter(name, parameter.ordinal());
        return getStringFromPointer(p);
    }

    /**
     * @see Etherstubadm#getEtherstubStatistic(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubStatistics)
     */
    @Override
    public String getEtherstubStatistic(String name, EtherstubStatistics statistic) throws EtherstubException {

        logger.info("Trying to read etherstub's : " + name + ", statistic : " + statistic);

        Pointer p = handle.get_etherstub_statistic(name, statistic.ordinal());
        return getStringFromPointer(p);
    }

    /**
     * @see Etherstubadm#getEtherstubProperty(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubProperties)
     */
    @Override
    public void setEtherstubProperty(String name, EtherstubProperties property, String value) throws EtherstubException {

        logger.info("Trying to set etherstub's : " + name + ", property : " + property + " value : " + value);

        int returnValue = handle.set_etherstub_property( name, property.ordinal(), value);


        String errorMessage;
        if (returnValue == EtherstubReturn.RESULT_OK.ordinal()) {
            errorMessage = null;
        } else if (returnValue == EtherstubReturn.INVALID_ETHERSTUB_NAME.ordinal()) {
            errorMessage = "Invalid etherstub name " + property;
        } else if (returnValue == EtherstubReturn.ETHERSTUB_PROPERTY_FAILURE.ordinal()) {
            errorMessage = "Unable to set property " + property;
        } else {
            errorMessage = "Unknown error while setting property " + property;
        }

        if(errorMessage != null){
            throw new EtherstubException(errorMessage);
        }
    }

    /**
     * @see Etherstubadm#getEtherstubProperty(java.lang.String, agh.msc.xbowbase.etherstub.enums.EtherstubProperties)
     */
    @Override
    public String getEtherstubProperty(String name, EtherstubProperties property) throws EtherstubException {

        logger.info("Trying to read etherstub's : " + name + ", property : " + property);

        Pointer p = handle.get_etherstub_property( name, property.ordinal());
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
     * @param p Pointer from the JNA library
     * @return String on which pointer points
     */
    private String getStringFromPointer(Pointer p) {
        String value = (p != null) ? p.getString(0) : null;
        handle.free_char_string(p);

        return value;
    }

    /**
     * C <-> Java mappings.
     */
    public interface IEtherstubadmin extends Library {

        public int init();

        public int delete_etherstub(String name, int temporary);

        public int create_etherstub(String name, int temporary);

        public Pointer get_etherstub_names();

        public Pointer get_etherstub_parameter(String name, int parameter);

        public Pointer get_etherstub_statistic(String name, int parameter);

        public Pointer get_etherstub_property( String name, int property);

        public int set_etherstub_property( String name, int property, String value );

        public void free_char_array( Pointer pointer );

        public void free_char_string( Pointer pointer );
    }
}
