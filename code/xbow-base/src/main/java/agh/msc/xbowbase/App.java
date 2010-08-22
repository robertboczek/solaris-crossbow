package agh.msc.xbowbase;

import agh.msc.xbowbase.etherstub.EtherstubManager;
import agh.msc.xbowbase.flow.FlowAccounting;
import agh.msc.xbowbase.publisher.FlowMBeanPublisher;
import agh.msc.xbowbase.flow.FlowManager;
import agh.msc.xbowbase.jna.JNAEtherstubHelper;
import agh.msc.xbowbase.jna.JNAFlowHelper;
import agh.msc.xbowbase.jna.JNALinkHelper;
import agh.msc.xbowbase.lib.EtherstubHelper;
import agh.msc.xbowbase.lib.FlowHelper;
import agh.msc.xbowbase.lib.NicHelper;
import agh.msc.xbowbase.link.NicManager;
import agh.msc.xbowbase.link.VNicManager;
import agh.msc.xbowbase.publisher.EtherstubMBeanPublisher;
import agh.msc.xbowbase.publisher.NicMBeanPublisher;
import agh.msc.xbowbase.publisher.VNicMBeanPublisher;
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

		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		// Initialize flowadm wrapper.

		//FlowHelper flowadm = new JNAFlowHelper();
		NicHelper nicHelper = new JNALinkHelper();
		EtherstubHelper etherstubHelper = new JNAEtherstubHelper();

		// Create FlowManager.

		//FlowManager flowManager = new FlowManager();
		//flowManager.setFlowadm( flowadm );
		//flowManager.setPublisher( new FlowMBeanPublisher( mbs ) );

		//NicManager nicManager = new NicManager();
		//nicManager.setNicHelper( nicHelper );
		//nicManager.setPublisher( new NicMBeanPublisher( mbs ) );

		VNicManager vNicManager = new VNicManager();
		vNicManager.setLinkHelper( nicHelper );
		vNicManager.setPublisher( new VNicMBeanPublisher( mbs ) );

		EtherstubManager etherstubManager = new EtherstubManager();
		etherstubManager.setEtherstHelper( etherstubHelper );
		etherstubManager.setPublisher( new EtherstubMBeanPublisher( mbs ) );

		// Create FlowAccounting.

		//FlowAccounting flowAccounting = new FlowAccounting();

		// Create Timer.

		Timer timer = new Timer();
		timer.addNotification( "discovery", "discover", null, new Date(), 5000 );
		//timer.addNotificationListener( flowManager, null, null );
		//timer.addNotificationListener( nicManager, null, null );
		timer.addNotificationListener( vNicManager, null, null );
		timer.addNotificationListener( etherstubManager, null, null );
		timer.start();

		// Register MBeans.

		//mbs.registerMBean( flowManager, new ObjectName( "agh.msc.xbowbase:type=FlowManager" ) );
		//mbs.registerMBean( flowAccounting, new ObjectName( "agh.msc.xbowbase:type=FlowAccounting" ) );

		//mbs.registerMBean( nicManager, new ObjectName( "agh.msc.xbowbase:type=NicManager" ) );

		mbs.registerMBean( vNicManager, new ObjectName( "agh.msc.xbowbase:type=VNicManager" ) );

		mbs.registerMBean( etherstubManager, new ObjectName( "agh.msc.xbowbase:type=EtherstubManager" ) );

		mbs.registerMBean( timer, new ObjectName( "agh.msc.xbowbase:type=Timer" ) );


		/*     FLOW CREATION TEST     */

		/*
		Map< String, String > newAttrs = new HashMap< String, String >();
		newAttrs.put( "transport", "tcp" );
		newAttrs.put( "local_port", "1234" );

		Map< String, String > newProps = new HashMap< String, String >();
		newProps.put( "priority", "MEDIUM" );

		Flow newFlow = new Flow();
		newFlow.setName( "wyjatkowy" );
		newFlow.setLink( "e1000g1" );
		newFlow.setAttrs( newAttrs );
		newFlow.setProps( newProps );
		newFlow.setTemporary( false );

		newFlow.setFlowadm( flowadm );

		try {

			flowManager.create( newFlow );

		} catch ( XbowException e ) {

			System.out.println( "Caught XBowException while creating " + newFlow.getName() + "." );
			e.printStackTrace();

		}
		 */

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
