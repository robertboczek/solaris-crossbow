package agh.msc.xbowbase.exception;

/**
 * @brief Exception thrown when names of etherstubs where invalid
 * Exception thrown when provided name of etherstub wasn't correct
 *
 * @author robert boczek
 */
public class InvalidEtherstubNameException extends EtherstubException{

    public InvalidEtherstubNameException(String message){
        super(message);
    }
}