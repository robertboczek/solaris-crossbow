package org.jims.modules.crossbow.etherstub;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.exception.EtherstubException;
import org.jims.modules.crossbow.lib.EtherstubHelper;

/**
 * Thread gathering statistics 
 *
 * @author robert boczek
 */
public class EtherstubStatisticsGatherer {

    private EtherstubHelper etherstubHelper;
    private Timer timer = new Timer();
    private final String etherstubName;
    private static final Logger logger = Logger.getLogger(EtherstubStatisticsGatherer.class);

    public EtherstubStatisticsGatherer(String etherstubName) {
        this.etherstubName = etherstubName;
    }

    void setEtherstubHelper(EtherstubHelper etherstubHelper) {
        this.etherstubHelper = etherstubHelper;
    }
    private LinkedList<Map<LinkStatistics, Long>> minuteValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private LinkedList<Map<LinkStatistics, Long>> fiveMinutesValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private LinkedList<Map<LinkStatistics, Long>> hourValueList = new LinkedList<Map<LinkStatistics, Long>>();
    private LinkedList<Map<LinkStatistics, Long>> dayValueList = new LinkedList<Map<LinkStatistics, Long>>();

    private Map<LinkStatistics, Long> getEmtpyMap() {
        Map<LinkStatistics, Long> map = new HashMap<LinkStatistics, Long>();
        for (LinkStatistics linkStatistics : LinkStatistics.values()) {
            map.put(linkStatistics, 0L);
        }
        return map;
    }

    private void initContent() {

        Map<LinkStatistics, Long> map = getEmtpyMap();
        for (LinkStatistics linkStatistics : LinkStatistics.values()) {
            map.put(linkStatistics, 0L);
        }

        for (int i = 0; i < 10; i++) {
            minuteValueList.add(map);
            fiveMinutesValueList.add(map);
            hourValueList.add(map);
            dayValueList.add(map);
        }


        timer.schedule(new TimerTask() {

            @Override
            public void run() {

                updateStatistics(minuteValueList);
                logger.trace("Minute statistics for etherstub " + etherstubName + " updated");

            }
        }, 0, 6000);//zawiera 10 wartosci

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics(fiveMinutesValueList);
                logger.trace("Five-minute statistics for etherstub " + etherstubName + " updated");
            }
        }, 0, 3000);//zawiera 10 wartosci

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics(hourValueList);
                logger.trace("Hourly statistics for etherstub " + etherstubName + " updated");
            }
        }, 0, 36000);//zawiera 10 wartosci

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateStatistics(dayValueList);
                logger.trace("Daily statistics for etherstub " + etherstubName + " updated");
            }
        }, 0, 864000);//zawiera 10 wartosci


    }

    private void updateStatistics(LinkedList<Map<LinkStatistics, Long>> valueList) {

        Map<LinkStatistics, Long> map = null;
        if (etherstubHelper != null) {

            map = new HashMap<LinkStatistics, Long>();
            for (LinkStatistics linkStatistics : LinkStatistics.values()) {
                try {
                    map.put(linkStatistics, Long.valueOf(etherstubHelper.getEtherstubStatistic(etherstubName, linkStatistics)));

                } catch (EtherstubException ex) {
                    logger.error("Couldn't read etherstubs statistic", ex);
                    break;
                } catch (Exception ex2) {
                    logger.error("Link Statistic Exception", ex2);
                }


            }
        } else {
            map = getEmtpyMap();
        }
        valueList.removeFirst();
        valueList.addLast(map);

    }

    public void start() {
        //initContent();
    }

    public void stop() {
        //timer.cancel();
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
