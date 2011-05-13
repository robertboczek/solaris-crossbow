package org.jims.modules.crossbow.manager;

import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;


/**
 *
 * @author cieplik
 */
public interface GenericManager< T > {

	ProxyFactory< T > getProxyFactory( String name, Class< T > klass ) throws EntityNotFoundException;

}
