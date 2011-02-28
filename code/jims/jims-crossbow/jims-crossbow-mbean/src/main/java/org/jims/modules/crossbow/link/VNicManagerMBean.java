package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.exception.LinkException;
import java.util.List;

/**
 * Interface VNicManagerMBean defines possible operation to be used in terms of VNic's management
 *
 * @author robert boczek
 */
public interface VNicManagerMBean {

    /**
     * Creates new vnic
     * @param vNicMBean New vnic to be created
     * @throws LinkException Exeption thrown in case of errors
     */
    void create(VNicMBean vNicMBean) throws LinkException;

    /**
     *
     * Deletes existing vnic
     * @param name Name of the vnic
     * @param temporary Specifies type of removal, whether this vnic should exist after reboot or not
     * @throws LinkException Exeption thrown in case of errors
     */
    void delete(String name, boolean temporary) throws LinkException;

    /**
     * Gets all names of existing vnic's
     * @return Returns all names of existing vnics or empty list if there is no vnics
     * @throws LinkException Exeption thrown in case of errors
     */
    List<String> getVNicsNames() throws LinkException;

		/**
		 * Returns MBean object representing VNIC with name equal to `name'.
		 *
		 * @return  MBean object
		 *
		 * @throws  LinkException if no VNIC found
		 */
		VNicMBean getByName( String name ) throws LinkException;

    /**
     * Removes unexisting beans and registers new ones
     * @throws LinkException Exeption thrown in case of errors
     */
    void discover() throws LinkException;


		/*
		 * jconsole only
		 */

    void _create( String name, boolean temporary, String parent ) throws LinkException;

}
