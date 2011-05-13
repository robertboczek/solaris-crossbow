package org.jims.modules.crossbow.manager;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.manager.exception.EntityNotFoundException;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;


/**
 * Base abstract class for crossbow managers
 *
 * @author robert boczek
 */
public abstract class BaseManager< T > implements GenericManager< T > {

	@Override
	public T getProxyByName( String name ) throws EntityNotFoundException {

		T res = null;

		if ( null != publisher ) {

			try {
				res = publisher.getProxy( name );
			} catch ( NotPublishedException ex ) {
			}

		}

		if ( null == res ) {
			throw new EntityNotFoundException( name + " not found." );
		}

		return res;

	}


	/**
	 * @brief  Publisher setter method.
	 *
	 * @param  publisher  object responsible for registering discovered and created flows
	 */
	public void setPublisher( Publisher< T > publisher ) {
		this.publisher = publisher;
	}


	protected Publisher< T > publisher;

	private static final Logger logger = Logger.getLogger( BaseManager.class );

}
