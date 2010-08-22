package agh.msc.xbowbase.exception;

/**
 * @brief Exception thrown in case of problems with Etherstubs
 * Subclass of XbowException thrown in case of errors
 * 
 * @author robert boczek
 */
public class EtherstubException extends XbowException{

    public EtherstubException(String message){
        super(message);
    }
}
