package agh.msc.xbowbase.exception;

/**
 * Exception thrown when provided etherstub name was too long
 *
 * @author robert boczek
 */
public class TooLongEtherstubNameException extends InvalidEtherstubNameException {

    /**
     * Constructor for TooLongEtherstubNameException
     *
     * @param message Message thrown within the exception
     */
    public TooLongEtherstubNameException(String message){
        super(message);
    }
}
