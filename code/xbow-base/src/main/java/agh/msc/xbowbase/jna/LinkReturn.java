package agh.msc.xbowbase.jna;


/**
 * Possible values returned by the JNAEtherstub.IEtherstubadmin
 *
 * @author robert boczek
 */
public enum LinkReturn {

    RESULT_OK,
    OPERATION_FAILURE,
    INVALID_LINK_NAME,
    TOO_LONG_LINK_NAME,
    INVALID_PARENT_LINK_NAME,
    TOO_LONG_PARENT_LINK_NAME
}
