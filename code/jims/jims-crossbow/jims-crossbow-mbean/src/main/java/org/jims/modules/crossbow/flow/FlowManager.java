package org.jims.modules.crossbow.flow;

import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.exception.XbowException;
import org.jims.modules.crossbow.flow.enums.FlowAttribute;
import org.jims.modules.crossbow.flow.enums.FlowProperty;
import org.jims.modules.crossbow.flow.util.FlowToFlowInfoTranslator;
import org.jims.modules.crossbow.lib.FlowHelper;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.manager.BaseManager;


/**
 * The class implements FlowManagerMBean functionality.
 *
 * @author cieplik
 */
public class FlowManager extends BaseManager implements FlowManagerMBean, NotificationListener {

    /**
     * @see  FlowManagerMBean#getFlows()
     */
    @Override
    public List<String> getFlows() {

        List<String> names = new LinkedList<String>();
        List<FlowInfo> flowsInfo = flowadm.getFlowsInfo();

        for (FlowInfo flowInfo : flowsInfo) {
            names.add(flowInfo.getName());
        }

        logger.debug(names.size() + " flow(s) present.");

        return names;

    }

		/**
		 * @see  FlowManagerMBean#getByName(java.lang.String)
		 */
		@Override
		public FlowMBean getByName( String name ) {

			FlowMBean res = null;

			for ( Object o : publisher.getPublished() ) {

				if ( name.equals( ( ( FlowMBean ) o ).getName() ) ) {
					res = ( FlowMBean ) o;
					break;
				}

			}

			return res;

		}

    /**
     * Discovers flows present in the system and, if a publisher has been set,
     * publishes each discovered flow.
     *
     * @see FlowManagerMBean#discover()
     */
    @Override
    public void discover() {

        if (publisher != null) {

            synchronized (publisher) {

                List<FlowInfo> flowsInfo = flowadm.getFlowsInfo();

                logger.debug(flowsInfo.size() + " flow(s) discovered.");

                for (FlowInfo flowInfo : flowsInfo) {

                    // Create new Flow object, initialize and register it.

                    Flow flow = FlowToFlowInfoTranslator.toFlow(flowInfo);
                    flow.setFlowadm(flowadm);

                    publisher.publish(flow);

                }

                // Unpublish flows user deleted manually.

                Set<String> published = new HashSet<String>();
                for (Object flow : publisher.getPublished()) {
                    published.add(((Flow) flow).getName());
                }

                Set<String> discovered = new HashSet<String>();
                for (Object flowInfo : flowsInfo) {
                    discovered.add(((FlowInfo) flowInfo).getName());
                }

                published.removeAll(discovered);
                for (Object flowName : published) {

                    try {
                        publisher.unpublish((String) flowName);
                    } catch (NotPublishedException e) {
                        logger.fatal("Error while removing stale flows.", e);
                    }

                }

            }

        }

    }

    /**
     * @see  FlowManagerMBean#create(agh.msc.xbowbase.flow.FlowMBean)
     */
    @Override
    public void create(FlowMBean flow) throws XbowException {

        // Create the flow.

        flowadm.create(FlowToFlowInfoTranslator.toFlowInfo((Flow) flow));
        logger.info(flow.getName() + " flow created.");

        // Publish, if publisher set.

        // if (publisher != null) {

        //     synchronized (publisher) {
        //         publisher.publish(flow);
        //     }

        //     logger.info(flow.getName() + " flow published.");

        // }

    }

    /**
     * Removes flow from the system and unpublishes, if publisher exists.
     *
     * @see  FlowManagerMBean#remove(java.lang.String, boolean)
     */
    @Override
    public void remove(String flowName, boolean temporary) throws XbowException {

        // Remove from the system.

        flowadm.remove(flowName, temporary);
        logger.info(flowName + " flow removed.");

        // Unpublish, if publisher exists.

        if (publisher != null) {

            try {

                synchronized (publisher) {
                    publisher.unpublish(flowName);
                }

                logger.info(flowName + " flow unpublished.");

            } catch (NotPublishedException e) {

                logger.error("Error while unpublishing " + flowName + ".", e);

            }

        }

    }

    /**
     * Executes discover() in response to notification.
     *
     * @see  NotificationListener#handleNotification(javax.management.Notification, java.lang.Object)
     */
    @Override
    public void handleNotification(Notification ntfctn, Object o) {
        discover();
    }

    /**
     * @brief  Flow helper setter method.
     *
     * @param  flowadm  helper used to manipulate flows
     */
    public void setFlowadm(FlowHelper flowadm) {
        this.flowadm = flowadm;
    }

    /**
     * @brief  Publisher setter method.
     *
     * @param  publisher  object responsible for registering discovered and created flows
     */
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }


    /*
     * jconsole only
     */
    @Override
    public void _create(String flowName, String link, String attributes) throws XbowException {

        Map<FlowAttribute, String> attrs = new HashMap<FlowAttribute, String>();

        for (String entry : attributes.split(",")) {
            attrs.put(FlowAttribute.fromString(entry.split("=")[ 0]), entry.split("=")[ 1]);
        }

        Map<FlowProperty, String> props = new HashMap<FlowProperty, String>();
        props.put(FlowProperty.PRIORITY, "medium");

        Flow flow = FlowToFlowInfoTranslator.toFlow(new FlowInfo(
                flowName, link, attrs, props, false));
        flow.setFlowadm(flowadm);

        create(flow);

    }
    private FlowHelper flowadm = null;
    private Publisher publisher = null;
    private static final Logger logger = Logger.getLogger(FlowManager.class);
}
