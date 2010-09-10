package agh.msc.xbowbase.exception;

/**
 * Exception thrown when provided etherstub name was too long
 *
 * @author robert boczek
 */
public class TooLongLinkNameException extends InvalidLinkNameException{

    /**
     * Constructor for TooLongLinkNameException
     *
     * @param message Message thrown within the exception
     */
    public TooLongLinkNameException(String message){
        super(message);
    }

}
