package agh.msc.xbowbase.etherstub;

import agh.msc.xbowbase.exception.EtherstubException;
import java.util.List;

/**
 * @brief  Defines list of possible operations
 * Interface EtherstubManagerMBean defines possible operation to be used in terms of Etherstub's management
 *
 * @author robert boczek
 */
public interface EtherstubManagerMBean {

    /**
     * @brief Creates new etherstub
     * Creates new etherstub
     * 
     * @param etherstubMBean New etherstub to be created
     * @throws EtherstubException Exeption thrown in case of errors
     */
    void create(EtherstubMBean etherstubMBean) throws EtherstubException;

    /**
     * @brief Removes existing etherstub
     * Deletes existing etherstub
     *
     * @param name Name of the etherstub
     * @param temporary Specifies type of removal, whether this etherstub should exist after reboot or not
     * @throws EtherstubException Exeption thrown in case of errors
     */
    void delete(String name, boolean temporary) throws EtherstubException;

    /**
     * @brief Returns list of etherstubs' names
     * Gets all names of existing etherstubs
     * 
     * @return Returns all names of existing etherstubs or empty list if there is no etherstubs
     * @throws EtherstubException Exeption thrown in case of errors
     */
    List<String> getEtherstubsNames() throws EtherstubException;

    /**
     * @brief Inovked when state of etherstubs could change
     * Removes unexisting beans and registers new ones
     * 
     * @throws EtherstubException Exception thrown in case of error
     */
    void discover() throws EtherstubException;


		/*
		 * jconsole only
		 */

		public void _create( String name, boolean temporary ) throws EtherstubException;
 
}
