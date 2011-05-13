package org.jims.modules.crossbow.manager;

import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;


/**
 *
 * @author cieplik
 */
public interface GenericManager< T > {

	T getProxyByName( String name ) throws EntityNotFoundException;

}
