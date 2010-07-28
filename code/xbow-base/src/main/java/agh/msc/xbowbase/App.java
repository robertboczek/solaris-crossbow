package agh.msc.xbowbase;

import agh.msc.xbowbase.flow.FlowManager;
import agh.msc.xbowbase.lib.Flowadm;
import java.lang.management.ManagementFactory;
import java.util.Date;
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

		// Flowadm flowadm = new JNAFlowadm();
		Flowadm flowadm = null;

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		Timer timer = new Timer();
		timer.addNotification( "discovery", "discover", null, new Date(), 5000 );
		timer.addNotificationListener( flowManager, null, null );

		mbs.registerMBean( flowManager, new ObjectName( "agh.msc.xbowbase:type=Hello" ) );
		mbs.registerMBean( timer, new ObjectName( "agh.msc.xbowbase:type=Timer" ) );

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
