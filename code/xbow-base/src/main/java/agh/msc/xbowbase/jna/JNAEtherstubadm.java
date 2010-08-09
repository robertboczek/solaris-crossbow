package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.etherstub.enums.EtherstubParameters;
import agh.msc.xbowbase.etherstub.enums.EtherstubProperties;
import agh.msc.xbowbase.etherstub.enums.EtherstubStatistics;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.lib.Etherstubadm;
import com.sun.jna.ptr.IntByReference;
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

    public JNAEtherstubadm(){
        handle = ( IEtherstubadm ) Native.loadLibrary( LIBNAME, IEtherstubadm.class );
	handle.init();
    }

    @Override
    public void deleteEtherstub(String name, boolean temporary) throws XbowException{

        int persitent_type = checkPersistenceType(temporary);
        int rc = handle.delete_etherstub( name, persitent_type );

	if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
            throw new XbowException( "Etherstub deletion failed." );
	}
    }

    @Override
    public void createEtherstub(String name, boolean temporary) throws XbowException {
        
        int persitent_type = checkPersistenceType(temporary);
        int rc = handle.create_etherstub( name, persitent_type );

	if ( rc != XbowStatus.XBOW_STATUS_OK.ordinal() ) {
            throw new XbowException( "Etherstub deletion failed." );
	}
    }

    @Override
    public String[] getEtherstubNames() throws XbowException {

        Pointer pointer = handle.get_etherstub_names();
        return (pointer != null) ? pointer.getStringArray(0) : null;
    }

    @Override
    public void getEtherstubParameter(String name, EtherstubParameters parameter, String value) throws XbowException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getEtherstubStatistic(String name, EtherstubStatistics property, String value) throws XbowException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEtherstubProperty(String name, EtherstubProperties property, String value) throws XbowException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getEtherstubProperty(String name, EtherstubProperties property, String value) throws XbowException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int checkPersistenceType(boolean temporary) {
        return (temporary) ? 2 : 1;
    }

    private interface IEtherstubadm extends Library{

        public int init();

        public int delete_etherstub( String name, int persistenceType);

        public int create_etherstub(String name, int persitenceType);

        public Pointer get_etherstub_names();
    }

    
}
