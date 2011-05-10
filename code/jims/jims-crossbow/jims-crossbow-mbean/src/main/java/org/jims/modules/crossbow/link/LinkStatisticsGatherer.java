package org.jims.modules.crossbow.link;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.LinkException;
import org.jims.modules.crossbow.lib.LinkHelper;

/**
 * Thread gathering statistics
 *
 * @author robert boczek
 */
public class LinkStatisticsGatherer {

    private LinkHelper linkHelper;
    private Timer timer = new Timer();
    private final String linkName;

    private static final Logger logger = Logger.getLogger(LinkStatisticsGatherer.class);
    
    private Thread timerThread = new Thread() {

        public void run() {

            Map<LinkStatistics, Long> map = getEmtpyMap();

            final LinkHelper helper = linkHelper;

            for (int i = 0; i < 10; i++) {
                minuteValueList.add(map);
                fiveMinutesValueList.add(map);
                hourValueList.add(map);
                dayValueList.add(map);
            }

            timer.schedule(new TimerTask() {

                @Override
                public void run() {

                    updateStatistics(minuteValueList, helper);
                    logger.trace("Minute statistics for etherstub " + linkName + " updated");

                }
            }, 0, 6000);//zawiera 10 wartosci

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    updateStatistics(fiveMinutesValueList, helper);
                    logger.trace("Five-minute statistics for etherstub " + linkName + " updated");
                }
            }, 0, 3000);//zawiera 10 wartosci

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    updateStatistics(hourValueList, helper);
                    logger.trace("Hourly statistics for etherstub " + linkName + " updated");
                }
            }, 0, 36000);//zawiera 10 wartosci

            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    updateStatistics(dayValueList, helper);
                    logger.trace("Daily statistics for etherstub " + linkName + " updated");
                }
            }, 0, 864000);//zawiera 10 wartosci

            try {
                Thread.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException ex) {
            }

        }
    };

    public LinkStatisticsGatherer(String linkName) {
        this.linkName = linkName;
    }

    void setLinkHelper(LinkHelper linkHelper) {
        this.linkHelper = linkHelper;
    }
    private final LinkedList<Map<LinkStatistics, Long>> minuteValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private final LinkedList<Map<LinkStatistics, Long>> fiveMinutesValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private final LinkedList<Map<LinkStatistics, Long>> hourValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private final LinkedList<Map<LinkStatistics, Long>> dayValueList = new LinkedList<Map<LinkStatistics, Long>>();

    private Map<LinkStatistics, Long> getEmtpyMap() {
        Map<LinkStatistics, Long> map = new HashMap<LinkStatistics, Long>();
        for (LinkStatistics linkStatistics : LinkStatistics.values()) {
            map.put(linkStatistics, 0L);
        }
        return map;
    }

    private void updateStatistics(LinkedList<Map<LinkStatistics, Long>> valueList, final LinkHelper helper) {

        Map<LinkStatistics, Long> map = null;
        if (linkHelper != null) {
            map = new HashMap<LinkStatistics, Long>();
            for (LinkStatistics linkStatistics : LinkStatistics.values()) {
                try {
                    map.put(linkStatistics, Long.valueOf(helper.getLinkStatistic(linkName, linkStatistics)));

                } catch (LinkException ex) {
                    logger.error("Couldn't read etherstubs statistic", ex);
                    break;
                } catch (Exception ex2) {
                    logger.error("Link Statistic Exception", ex2);
                }

            }

        } else {
            logger.info("Couldn't read statistics as linkHelper was null");
            map = getEmtpyMap();
        }
        valueList.removeFirst();
        valueList.addLast(map);

    }

    public void start() {
        timerThread.start();
    }

    public void stop() {
        timer.cancel();
    }

    public List<Map<LinkStatistics, Long>> getStatistics(LinkStatisticTimePeriod period) {

        List<Map<LinkStatistics, Long>> map = null;
        if (LinkStatisticTimePeriod.DAILY.equals(period)) {
            map = dayValueList;
        } else if (LinkStatisticTimePeriod.HOURLY.equals(period)) {
            map = hourValueList;
        } else if (LinkStatisticTimePeriod.FIVE_MINUTELY.equals(period)) {
            map = fiveMinutesValueList;
        } else if (LinkStatisticTimePeriod.MINUTELY.equals(period)) {
            map = minuteValueList;
        }

        return map;
    }
}

