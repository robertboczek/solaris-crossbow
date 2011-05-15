package org.jims.modules.crossbow.util.jmx;


/**
 *
 * @author cieplik
 */
public interface MBeanProxyHelperFactory {

	MBeanProxyHelper getComponentProxyHelper();
	MBeanProxyHelper getComponentProxyHelper( String url );

	MBeanProxyHelper getManagerProxyFactory();
	MBeanProxyHelper getManagerProxyFactory( String url );

}
