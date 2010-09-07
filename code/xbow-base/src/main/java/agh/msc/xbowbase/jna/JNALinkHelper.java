package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.enums.LinkParameters;
import agh.msc.xbowbase.enums.LinkProperties;
import agh.msc.xbowbase.enums.LinkStatistics;
import agh.msc.xbowbase.exception.InvalidLinkNameException;
import agh.msc.xbowbase.exception.LinkException;
import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.jna.mapping.LinkHandle;
import agh.msc.xbowbase.lib.LinkHelper;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.apache.log4j.Logger;

/**
 * @brief
 * Link helper implementation based on Java Native Access.
 *
 * @author cieplik
 */
public class JNALinkHelper implements LinkHelper {

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
            return (array == null) ? new String[]{} : array;
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


        if(returnValue == XbowStatus.XBOW_STATUS_OK.ordinal()){
            
            return;

        } else if (returnValue == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()) {

            throw new InvalidLinkNameException("Invalid link name: " + name);

        }  else if (returnValue == XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal()) {

            throw new LinkException("Unable to set property " + property);

        } else {

            throw new LinkException("Unknown error while setting property " + property);

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


		@Override
		public void plumb( String link ) {

			logger.debug( "plumb: entry" );
			handle.plumb( link );

		}


		/**
		 * @see  LinkHelper#setNetmask(java.lang.String, java.lang.String)
		 */
		@Override
		public void setNetmask( String name, String mask ) throws ValidationException,
		                                                          LinkException {

			logger.info( "Setting " + name + " mask to " + mask );

			int rc = handle.set_netmask( name, mask );

			if ( XbowStatus.XBOW_STATUS_OK.ordinal() != rc ) {

				if ( XbowStatus.XBOW_STATUS_INVALID_VALUE.ordinal() == rc ) {
					throw new ValidationException( mask );
				} else {
					throw new LinkException( "set_netmask returned with rc == " + String.valueOf( rc ) );
				}

			}
		
		}


		@Override
		public String getNetmask( String link ) {

			String netmask = handle.get_netmask( link );
			String res = new String( netmask );

			handle.free( netmask );

			return res;

		}


    /**
     * Method return string on which Pointer p points and frees the memory allocated by the library
     *
     * @param p Pointer from the JNA library
     * @return String on which pointer points
     */
    protected String getStringFromPointer(Pointer p) {
        
        String value = (p != null) ? p.getString(0) : null;
        handle.free_char_string(p);

        return value;
    }    
    
    static final String LIB_NAME = "link_wrapper";
    protected LinkHandle handle = null;
    private static final Logger logger = Logger.getLogger(JNALinkHelper.class);

    /**
     * @see LinkHelper#getIpAddress(java.lang.String)
     */
    @Override
    public String getIpAddress(String link) throws LinkException {
        return handle.get_ip_address(link);
    }

    /**
     * @see LinkHelper#setIpAddress(java.lang.String, java.lang.String)
     */
    @Override
    public void setIpAddress(String link, String ipAddress) throws LinkException {

        int returnValue = handle.set_ip_address(link, ipAddress);

        //@todo obsluga bledow

    }
}
