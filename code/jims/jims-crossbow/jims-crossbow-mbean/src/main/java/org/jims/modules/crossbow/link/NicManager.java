package org.jims.modules.crossbow.link;

import org.jims.modules.crossbow.lib.NicHelper;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.management.Notification;
import javax.management.NotificationListener;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.manager.BaseManager;

/**
 * The class implements NicManagerMBean functionality.
 *
 * @author cieplik
 */
public class NicManager extends BaseManager implements NicManagerMBean, NotificationListener {

    /**
     * @see  NicManagerMBean#getNicsList()
     */
    @Override
    public List<String> getNicsList() {

        List<String> names = new LinkedList<String>();

        for (NicInfo nicInfo : nicHelper.getNicsInfo()) {
            names.add(nicInfo.getName());
        }

        return names;

    }

    /**
     * @see  NicManagerMBean#discover()
     */
    @Override
    public void discover() {

        if (publisher != null) {

            synchronized (publisher) {

                List<NicInfo> nicsInfo = nicHelper.getNicsInfo();

                logger.debug(nicsInfo.size() + " nic(s) discovered.");

                for (NicInfo nicInfo : nicsInfo) {

                    // Create new Nic object, initialize and register it.

                    Nic nic = new Nic(nicInfo.getName());
                    nic.setNicHelper(nicHelper);

                    publisher.publish(nic);

                }

                // Unpublish nics user deleted.

                Set<String> published = new HashSet<String>();
                for (Object nic : publisher.getPublished()) {
                    published.add(((Nic) nic).getName());
                }

                Set<String> discovered = new HashSet<String>();
                for (Object nicInfo : nicsInfo) {
                    discovered.add(((NicInfo) nicInfo).getName());
                }

                published.removeAll(discovered);
                for (Object nicName : published) {

                    try {
                        publisher.unpublish((String) nicName);
                    } catch (NotPublishedException e) {
                        logger.fatal("Error while removing stale NICs.", e);
                    }

                }

            }

        }

    }

    /**
     * Executes discover() in response to notification.
     *
     * @see  NotificationListener#handleNotification( javax.management.Notification, java.lang.Object )
     */
    @Override
    public void handleNotification(Notification ntfctn, Object o) {

        logger.debug("Received notification " + ntfctn);

        discover();

    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public void setNicHelper(NicHelper nicHelper) {
        this.nicHelper = nicHelper;
    }
    Publisher publisher;
    NicHelper nicHelper;
    private static final Logger logger = Logger.getLogger(NicManager.class);
}
