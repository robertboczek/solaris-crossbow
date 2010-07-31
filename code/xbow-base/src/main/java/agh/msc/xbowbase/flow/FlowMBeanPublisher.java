package agh.msc.xbowbase.flow;

import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 *
 * @author cieplik
 */
public class FlowMBeanPublisher implements Publisher {

	public FlowMBeanPublisher( MBeanServer mBeanServer ) {

		this.mBeanServer = mBeanServer;

	}


	@Override
	public void publish( Object object ) {

		FlowMBean flowMBean = ( FlowMBean ) object;

		try {

			mBeanServer.registerMBean( flowMBean, new ObjectName( String.format(
				"agh.msc.xbowbase:type=Flow,link=%s,name=%s",
				flowMBean.getLink(), flowMBean.getName()
			) ) );

		} catch ( Exception e ) {

			e.printStackTrace();

		}

	}


	private MBeanServer mBeanServer = null;

}
