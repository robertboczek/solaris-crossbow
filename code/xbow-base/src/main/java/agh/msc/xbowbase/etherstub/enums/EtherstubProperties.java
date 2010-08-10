package agh.msc.xbowbase.etherstub.enums;

/**
 *
 * @author robert boczek
 */
public enum EtherstubProperties {
    	MAXBW(1),
	LEARN_LIMIT(2),
	CPUS(3),
	PRIORITY(4);

    private EtherstubProperties(int value) {
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    private int value;
}
