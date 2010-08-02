package agh.msc.xbowbase.flow;

import java.util.LinkedList;
import java.util.List;
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

		if ( published.contains( object ) ) {

			// TODO-DAWID: already published. do nothing. log?

		} else {

			FlowMBean flowMBean = ( FlowMBean ) object;

			try {

				mBeanServer.registerMBean( flowMBean, new ObjectName( String.format(
					"agh.msc.xbowbase:type=Flow,link=%s,name=%s",
					flowMBean.getLink(), flowMBean.getName()
				) ) );

				published.add( object );

			} catch ( Exception e ) {

				e.printStackTrace();

			}

		}

	}


	private MBeanServer mBeanServer = null;
	private List published = new LinkedList();

}
