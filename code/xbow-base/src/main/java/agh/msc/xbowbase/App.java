package agh.msc.xbowbase;

import agh.msc.xbowbase.flow.Flow;
import agh.msc.xbowbase.flow.FlowMBeanPublisher;
import agh.msc.xbowbase.flow.FlowManager;
import agh.msc.xbowbase.jna.JNAFlowadm;
import agh.msc.xbowbase.lib.Flowadm;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
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

		Flowadm flowadm = new JNAFlowadm();

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );
		flowManager.setPublisher( new FlowMBeanPublisher( mbs ) );

		Timer timer = new Timer();
		timer.addNotification( "discovery", "discover", null, new Date(), 5000 );
		timer.addNotificationListener( flowManager, null, null );

		mbs.registerMBean( flowManager, new ObjectName( "agh.msc.xbowbase:type=Hello" ) );
		mbs.registerMBean( timer, new ObjectName( "agh.msc.xbowbase:type=Timer" ) );

		for ( String flow : flowManager.getFlows() ) {
			System.out.println( flow );
		}

		/*
		Flow flow = new Flow();

		flow.setFlowadm( flowadm );
		flow.setName( "flow" );


		Map< String, String > properties = new HashMap< String, String >();
		properties.put( "maxbw", "10M" );

		flow.setProps( properties );

		for ( Map.Entry< String, String > entry : flow.getProperties().entrySet() ) {
			System.out.println( entry.toString() );
		}
		 *
		 */


		Map< String, String > newAttrs = new HashMap< String, String >();
		newAttrs.put( "transport", "tcp" );

		Map< String, String > newProps = new HashMap< String, String >();
		newProps.put( "priority", "MEDIUM" );

		/*
		Flow newFlow = new Flow();
		newFlow.setName( "yyynowyyy" );
		newFlow.setLink( "e1000g0" );
		newFlow.setAttrs( newAttrs );
		newFlow.setProps( newProps );
		newFlow.setTemporary( false );

		newFlow.setFlowadm( flowadm );

		flowManager.create( newFlow );

		newFlow.resetProperties( Arrays.asList( "priority" ), true );
		 * *
		 */

		flowManager.discover();

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
