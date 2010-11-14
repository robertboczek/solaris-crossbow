package org.jims.modules.crossbow;

import org.jims.modules.crossbow.etherstub.EtherstubManager;
import org.jims.modules.crossbow.publisher.FlowMBeanPublisher;
import org.jims.modules.crossbow.flow.FlowManager;
import org.jims.modules.crossbow.jna.JNAEtherstubHelper;
import org.jims.modules.crossbow.jna.JNAFlowHelper;
import org.jims.modules.crossbow.jna.JNANicHelper;
import org.jims.modules.crossbow.jna.JNAVNicHelper;
import org.jims.modules.crossbow.lib.EtherstubHelper;
import org.jims.modules.crossbow.lib.FlowHelper;
import org.jims.modules.crossbow.lib.NicHelper;
import org.jims.modules.crossbow.lib.VNicHelper;
import org.jims.modules.crossbow.link.NicManager;
import org.jims.modules.crossbow.link.VNicManager;
import org.jims.modules.crossbow.link.validators.LinkValidator;
import org.jims.modules.crossbow.link.validators.RegexLinkValidator;
import org.jims.modules.crossbow.publisher.EtherstubMBeanPublisher;
import org.jims.modules.crossbow.publisher.NicMBeanPublisher;
import org.jims.modules.crossbow.publisher.VNicMBeanPublisher;
import com.sun.jna.Native;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.timer.Timer;

/**
 * Hello world!
 *
 * libflowadm, JNA snippet
 *
 */
public class App {

    public static void main(String args[]) throws Exception {

        MBeanServer server = null;//ManagementFactory.getPlatformMBeanServer();

        // get the JIMS MBean server
        ArrayList servers = MBeanServerFactory.findMBeanServer(null);
        Iterator it = servers.iterator();

        System.out.println("Registered servers number: " + servers.size());
        while (it.hasNext()) {
            MBeanServer testedServer = (MBeanServer) it.next();
            try {
                if (testedServer.isRegistered(new ObjectName("Information:class=OSCommon"))) {
                    server = testedServer;
                    break;
                }
            } catch (Exception ex) {               
                ex.printStackTrace();
            }
        }
        if (server == null) {
            System.out.println("Jims mbean servered was not found");
            return;
        }

        // Validators

				LinkValidator linkValidator = new RegexLinkValidator();

        // Initialize flowadm wrapper.

				FlowHelper flowadm = new JNAFlowHelper();
				
				NicHelper nicHelper = new JNANicHelper(linkValidator);
				
				VNicHelper vnicHelper = new JNAVNicHelper(linkValidator);
				
				EtherstubHelper etherstubHelper = new JNAEtherstubHelper();


        // Create FlowManager.

        FlowManager flowManager = new FlowManager();

        NicManager nicManager = new NicManager();

        VNicManager vNicManager = new VNicManager();


        EtherstubManager etherstubManager = new EtherstubManager();

        // Create FlowAccounting.

        //FlowAccounting flowAccounting = new FlowAccounting();

        // Create Timer.

        Timer timer = new Timer();
        timer.addNotification("discovery", "discover", null, new Date(), 5000);
        timer.addNotificationListener(flowManager, null, null);
        timer.addNotificationListener(nicManager, null, null);
        timer.addNotificationListener(vNicManager, null, null);
        timer.addNotificationListener(etherstubManager, null, null);
        timer.start();

        // Register MBeans.

        server.registerMBean(flowManager, new ObjectName("agh.msc.xbowbase:type=FlowManager"));
        // mbs.registerMBean( flowAccounting, new ObjectName( "agh.msc.xbowbase:type=FlowAccounting" ) );

        server.registerMBean(nicManager, new ObjectName("agh.msc.xbowbase:type=NicManager"));

        server.registerMBean(vNicManager, new ObjectName("agh.msc.xbowbase:type=VNicManager"));

        server.registerMBean(etherstubManager, new ObjectName("agh.msc.xbowbase:type=EtherstubManager"));

        server.registerMBean(timer, new ObjectName("agh.msc.xbowbase:type=Timer"));


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
