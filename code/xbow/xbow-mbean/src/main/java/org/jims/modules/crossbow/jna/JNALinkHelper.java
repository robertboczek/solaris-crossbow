package org.jims.modules.crossbow.jna;

import org.jims.modules.crossbow.enums.LinkParameters;
import org.jims.modules.crossbow.enums.LinkProperties;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.InvalidLinkNameException;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.exception.ValidationException;
import org.jims.modules.crossbow.jna.mapping.LinkHandle;
import org.jims.modules.crossbow.lib.LinkHelper;
import org.jims.modules.crossbow.link.validators.LinkValidator;
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

    private LinkValidator linkValidator;
    static final String LIB_NAME = "link-1.0.0";
    protected LinkHandle handle = null;
    private static final Logger logger = Logger.getLogger(JNALinkHelper.class);

    /**
     * Creates the helper object and initializes underlying handler.
     */
    public JNALinkHelper() {

        handle = (LinkHandle) Native.loadLibrary(LIB_NAME, LinkHandle.class);
        handle.init();

    }

    /**
     * Creates the helper object and initializes underlying handler.
     *
     * @param linkValidator LinkValidator implementation
     */
    public JNALinkHelper(LinkValidator linkValidator) {

        this();
        this.linkValidator = linkValidator;

    }

    /**
     * Creates the helper object using user-provided JNA handle and user-provided validator
     *
     * @param  handle  JNA handle
     * @param linkValidator LinkValidator implementation
     */
    public JNALinkHelper(LinkHandle handle, LinkValidator linkValidator) {
        this(handle);
        this.linkValidator = linkValidator;
    }

    /**
     * Creates the helper object using user-provided JNA handle and user-provided validator
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

        if (pointer != null) {

            String[] array = pointer.getStringArray(0);
            handle.free_char_array(pointer);
            return (array == null) ? new String[]{} : array;
        } else {
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

        int returnValue = handle.set_link_property(name, property.toString(), value);


        if (returnValue == XbowStatus.XBOW_STATUS_OK.ordinal()) {

            return;

        } else if (returnValue == XbowStatus.XBOW_STATUS_INVALID_NAME.ordinal()) {

            throw new InvalidLinkNameException("Invalid link name: " + name);

        } else if (returnValue == XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal()) {

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

        Pointer p = handle.get_link_property(name, property.toString());
        return getStringFromPointer(p);

    }

    @Override
    public void plumb( String link ) throws LinkException {

			logger.info( "Plumbing " + link );

			int rc = handle.plumb( link );

			if ( XbowStatus.XBOW_STATUS_OK.ordinal() != rc ) {
				throw new LinkException( "plumb returned with rc == " + String.valueOf( rc ) );
			}

    }

    @Override
    public boolean isPlumbed( String link ) {

        return handle.is_plumbed( link );

    }

    /**
     * @see  LinkHelper#setNetmask(java.lang.String, java.lang.String)
     */
    @Override
    public void setNetmask(String name, String mask) throws ValidationException,
            LinkException {

        logger.info("Setting " + name + " mask to " + mask);

        int rc = handle.set_netmask(name, mask);

        if (XbowStatus.XBOW_STATUS_OK.ordinal() != rc) {

            if (XbowStatus.XBOW_STATUS_INVALID_VALUE.ordinal() == rc) {
                throw new ValidationException(mask);
            } else {
                throw new LinkException("set_netmask returned with rc == " + String.valueOf(rc));
            }

        }

    }

    @Override
    public String getNetmask( String link ) throws LinkException {

			logger.debug( "Getting netmask for " + link );

			LinkHandle.BufferStruct buffer = new LinkHandle.BufferStruct( 256 );

			int rc = handle.get_netmask( link, buffer );

			if ( XbowStatus.XBOW_STATUS_OK.ordinal() != rc ) {
				throw new LinkException( "get_netmask returned with rc == " + String.valueOf( rc ) );
			}

			return buffer.buffer;

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
    public void setIpAddress(String link, String ipAddress) throws LinkException, ValidationException {

        logger.debug("Trying to set ip address: " + ipAddress + " to link: " + link);

        if (linkValidator != null &&
                linkValidator.isIpAddressValid(ipAddress) == false) {

            throw new ValidationException("Provided ipAddress: " + ipAddress + " is incorrect. Couldn't change ip address to: " + link);

        }

        int returnValue = handle.set_ip_address(link, ipAddress);

        if (returnValue == XbowStatus.XBOW_STATUS_OK.ordinal()) {

            return;

        } else if (returnValue == XbowStatus.XBOW_STATUS_OPERATION_FAILURE.ordinal()) {

            throw new LinkException("Couldn't set ip address - operation failed");

        } else {

            throw new LinkException("Unknown error occured while setting ip address");

        }

    }

    /**
     * Setter of linkValidator variable
     *
     * @param linkValidator LinkValidator implementation instance
     */
    public void setLinkValidator(LinkValidator linkValidator) {

        this.linkValidator = linkValidator;

    }

    /**
     * @see LinkHelper#putUp(java.lang.String, boolean)
     */
    @Override
    public void putUp(String link, boolean up) throws LinkException {

        int upDownProp = (up) ? 1 : 0;
        int result = handle.ifconfig_up(link, upDownProp);
        System.out.println("fds:" + result + " " + XbowStatus.XBOW_STATUS_OK.ordinal());

        if (result != XbowStatus.XBOW_STATUS_OK.ordinal()) {
            String message = "Couldn't put link " + link + " ";
            message += (up) ? "up" : "down";
            throw new LinkException(message);

        }
    }

    /**
     * @see LinkHelper#isUp(java.lang.String) 
     */
    @Override
    public boolean isUp(String link) throws LinkException {
        int value = handle.ifconfig_is_up(link);
        if (value == 0 || value == 1) {
            return (value == 0) ? false : true;
        } else {
            throw new LinkException("Couldn't get the info about link: " + link + " up or down state");
        }
    }
}
