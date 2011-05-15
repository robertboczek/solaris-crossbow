package org.jims.modules.crossbow.publisher;

import java.io.Serializable;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.jims.modules.crossbow.manager.ProxyFactory;


/**
 *
 * @author cieplik
 */
public class MBeanProxyFactory< T > implements ProxyFactory< T >, Serializable {

	public MBeanProxyFactory( String url, ObjectName on, Class< T > klass ) {
		this.url = url;
		this.on = on;
		this.klass = klass;
	}


	@Override
	public T create() {

		T proxy = null;

		try {

			MBeanServerConnection mbsc = JMXConnectorFactory.connect(
				new JMXServiceURL( url )
			).getMBeanServerConnection();

			proxy = JMX.newMBeanProxy( mbsc, on, klass );

		} catch ( Exception ex ) {

			// TODO  what to do now?

		}

		return proxy;

	}


	private String url;
	private ObjectName on;
	private Class< T > klass;

}
