package agh.msc.xbowbase.etherstub.enums;

/**
 *
 * @author robert boczek
 */
public enum EtherstubStatistics {

    IPACKETS(1),
    RBYTES(2),
    IERRORS(3),
    OPACKETS(4),
    OBYTES(5),
    OERRORS(6);

    private EtherstubStatistics(int libValue) {
        this.libValue = libValue;
    }
    private int libValue;

    public int getValue() {
        return libValue;
    }
}
