package agh.msc.xbowbase.flow;

import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
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

				mBeanServer.registerMBean( flowMBean, createObjectName( flowMBean ) );

				published.add( object );

			} catch ( Exception e ) {

				e.printStackTrace();

			}

		}

	}


	@Override
	public void unpublish( Object object ) {

		if ( object instanceof String ) {

			String flowName = ( String ) object;

			for ( Object o : published ) {

				Flow flow = ( Flow ) o;

				if ( flow.getName().equals( flowName ) ) {

					try {
						mBeanServer.unregisterMBean( createObjectName( flow ) );
					} catch ( Exception e ) {
						e.printStackTrace();
					}

				}

			}

		}

	}


	private ObjectName createObjectName( FlowMBean flowMBean ) {

		ObjectName on = null;

		try {

			on = new ObjectName( String.format(
				"agh.msc.xbowbase:type=Flow,link=%s,name=%s",
				flowMBean.getLink(), flowMBean.getName()
			) );

		} catch ( MalformedObjectNameException e ) {

			e.printStackTrace();

		}

		return on;

	}


	private MBeanServer mBeanServer = null;
	private List published = new LinkedList();

}
