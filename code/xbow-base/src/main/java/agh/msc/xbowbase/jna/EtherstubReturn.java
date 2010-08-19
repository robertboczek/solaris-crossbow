package agh.msc.xbowbase.jna;

/**
 * Possible values returned by the JNAEtherstub.IEtherstubadmin
 *
 * @author robert boczek
 */
public enum EtherstubReturn {

    RESULT_OK,
    DELETE_FAILURE,
    INVALID_ETHERSTUB_NAME,
    TOO_LONG_ETHERSTUB_NAME,
    CREATE_FAILURE,
    LIST_ETHERSTUB_NAMES_ERROR,
    ETHERSTUB_PROPERTY_FAILURE,
    ETHERSTUB_STATS_FAILURE
}
