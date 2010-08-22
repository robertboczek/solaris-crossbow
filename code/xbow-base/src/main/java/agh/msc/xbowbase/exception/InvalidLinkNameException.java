package agh.msc.xbowbase.exception;

/**
 * @brief Exception thrown when names of links where invalid
 * Exception thrown when provided name of etherstub wasn't correct
 *
 * @author robert boczek
 */
public class InvalidLinkNameException extends LinkException{

    public InvalidLinkNameException(String message){
        super(message);
    }
}
