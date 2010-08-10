package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.EtherstubException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.lib.Etherstubadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 *
 * @author robeert boczek
 */
public class JNAEtherstubadm implements Etherstubadm {

    private final String LIBNAME = "etherstub_wrapper";
    private IEtherstubadm handle;

    public JNAEtherstubadm() {
        handle = (IEtherstubadm) Native.loadLibrary(LIBNAME, IEtherstubadm.class);
        handle.init();
    }

    @Override
    public void deleteEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);
        int rc = handle.delete_etherstub(name, persitent_type);

        if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {
            throw new EtherstubException("Etherstub deletion failed.");
        }
    }

    @Override
    public void createEtherstub(String name, boolean temporary) throws EtherstubException {

        int persitent_type = checkPersistenceType(temporary);
        int rc = handle.create_etherstub(name, persitent_type);

        if (rc != XbowStatus.XBOW_STATUS_OK.ordinal()) {
            throw new EtherstubException("Etherstub deletion failed.");
        }
    }

    @Override
    public String[] getEtherstubNames() throws EtherstubException {

        Pointer pointer = handle.get_etherstub_names();
        return (pointer != null) ? pointer.getStringArray(0) : null;
    }

    @Override
    public String getEtherstubParameter(String name, EtherstubParameters parameter) throws EtherstubException {

        Pointer p = handle.get_etherstub_parameter(name, parameter.getValue());
        return (p != null) ? p.getString(0) : null;
    }

    @Override
    public String getEtherstubStatistic(String name, EtherstubStatistics property) throws EtherstubException {

        Pointer p = handle.get_etherstub_statistic(name, property.getValue());
        return (p != null) ? p.getString(0) : null;
    }

    @Override
    public void setEtherstubProperty(String name, EtherstubProperties property, String value) throws EtherstubException {
        int returnValue = handle.set_etherstub_property( name, property.getValue(), value);

        System.err.println(returnValue);
        String errorMessage;
        switch(returnValue){
            case 0:
                errorMessage = null;
                break;
            case 2:
                errorMessage = "Invalid etherstub name " + property;
                break;
            case 6:
                errorMessage = "Unable to set property " + property;
                break;
            default:
                errorMessage = "Unknown error while setting property " + property;
                break;
        }
        if(errorMessage != null){
            throw new EtherstubException(errorMessage);
        }
    }

    @Override
    public String getEtherstubProperty(String name, EtherstubProperties property) throws EtherstubException {

        Pointer p = handle.get_etherstub_property( name, property.getValue());
        return (p != null) ? p.getString(0) : null;
    }

    private int checkPersistenceType(boolean temporary) {
        return (temporary) ? 2 : 1;
    }

    private interface IEtherstubadm extends Library {

        public int init();

        public int delete_etherstub(String name, int persistenceType);

        public int create_etherstub(String name, int persitenceType);

        public Pointer get_etherstub_names();

        public Pointer get_etherstub_parameter(String name, int parameter);

        public Pointer get_etherstub_statistic(String name, int parameter);

        public Pointer get_etherstub_property( String name, int property);

        public int set_etherstub_property( String name, int property, String value );
    }
}
