package agh.msc.xbowbase;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowManager;
import agh.msc.xbowbase.jna.JNAFlowadm;
import agh.msc.xbowbase.lib.Flowadm;
import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.timer.Timer;


/**
 * Hello world!
 *
 * libflowadm, JNA snippet
 *
 */
public class App {

	public static void main( String args[] ) throws Exception {

		Flowadm flowadm = new JNAFlowadm();

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		Timer timer = new Timer();
		timer.addNotification( "discovery", "discover", null, new Date(), 5000 );
		timer.addNotificationListener( flowManager, null, null );

		mbs.registerMBean( flowManager, new ObjectName( "agh.msc.xbowbase:type=Hello" ) );
		mbs.registerMBean( timer, new ObjectName( "agh.msc.xbowbase:type=Timer" ) );

		for ( String flow : flowManager.getFlows() ) {
			System.out.println( flow );
		}

		Flow flow = new Flow();

		flow.setFlowadm( flowadm );
		flow.setName( "flow" );


		Map< String, String > properties = new HashMap< String, String >();
		properties.put( "maxbw", "10M" );

		flow.setProperties( properties, true );

		for ( Map.Entry< String, String > entry : flow.getProperties().entrySet() ) {
			System.out.println( entry.toString() );
		}

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
