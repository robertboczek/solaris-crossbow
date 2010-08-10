package agh.msc.xbowbase.etherstub.enums;

/**
 *
 * @author robert boczek
 */
public enum EtherstubParameters {

    BRIDGE(1),
    OVER(2),
    STATE(3),
    MTU(4);

    private EtherstubParameters(int libValue) {
        this.libValue = libValue;
    }
    private int libValue;

    public int getValue() {
        return libValue;
    }
}
