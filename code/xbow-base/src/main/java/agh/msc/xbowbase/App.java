package agh.msc.xbowbase;

import agh.msc.xbowbase.exception.XbowException;
import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowAccounting;
import agh.msc.xbowbase.flow.FlowMBeanPublisher;
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

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		// Initialize flowadm wrapper.

		Flowadm flowadm = new JNAFlowadm();

		// Create FlowManager.

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );
		flowManager.setPublisher( new FlowMBeanPublisher( mbs ) );

		// Create FlowAccounting.

		FlowAccounting flowAccounting = new FlowAccounting();

		// Create Timer.

		Timer timer = new Timer();
		timer.addNotification( "discovery", "discover", null, new Date(), 5000 );
		timer.addNotificationListener( flowManager, null, null );
		timer.start();

		// Register MBeans.

		mbs.registerMBean( flowManager, new ObjectName( "agh.msc.xbowbase:type=FlowManager" ) );
		mbs.registerMBean( flowAccounting, new ObjectName( "agh.msc.xbowbase:type=FlowAccounting" ) );
		mbs.registerMBean( timer, new ObjectName( "agh.msc.xbowbase:type=Timer" ) );


		/*     FLOW DISCOVERY TEST     */

		for ( String flow : flowManager.getFlows() ) {
			System.out.println( flow );
		}


		/*     FLOW CREATION TEST     */

		Map< String, String > newAttrs = new HashMap< String, String >();
		newAttrs.put( "local_ip", "1.1.1.3" );

		Map< String, String > newProps = new HashMap< String, String >();
		newProps.put( "priority", "MEDIUM" );

		Flow newFlow = new Flow();
		newFlow.setName( "wyjatkowy" );
		newFlow.setLink( "e1000g0" );
		newFlow.setAttrs( newAttrs );
		newFlow.setProps( newProps );
		newFlow.setTemporary( false );

		newFlow.setFlowadm( flowadm );

		try {

			flowManager.create( newFlow );

		} catch ( XbowException e ) {

			System.out.println( "Caught XBowException while creating " + newFlow.getName() + "." );

		}

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
