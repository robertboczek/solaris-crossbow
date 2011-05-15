package org.jims.modules.crossbow.gui.chart;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGathererMBean;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

/**
 * Displays graph with interface nad/or flow statistics
 * 
 * @author robert
 * 
 */
public class ChartDisplayer {

	private static int chartNumber = 0;
	private static ChartWindow chartWindow;

	private StatisticsGathererMBean statisticsGatherer;
	private Assignments assignments;

	private static final Logger logger = Logger.getLogger(ChartDisplayer.class);

	private void createWindow() {
		chartWindow = new ChartWindow();
		chartWindow.setAlwaysOnTop(true);
		chartWindow.setVisible(false);
		chartWindow.setSize(800, 600);
	}

	/**
	 * Constructor
	 * 
	 * @param linkStatisticTimePeriod
	 *            Time axis type (minutely, hourly etc.)
	 * @param policies
	 *            Array of flows to display on the chart
	 */
	public ChartDisplayer(LinkStatisticTimePeriod linkStatisticTimePeriod,
			Policy[] policies, StatisticsGathererMBean statisticsGatherer,
			Assignments assignments) {

		try {

			this.statisticsGatherer = statisticsGatherer;
			this.assignments = assignments;

			List<List<Map<LinkStatistics, Long>>> list = new LinkedList<List<Map<LinkStatistics, Long>>>();
			String names[] = new String[policies.length];
			int i = 0;
			for (Policy policy : policies) {
				List<Map<LinkStatistics, Long>> data = getData(policy, linkStatisticTimePeriod);
				if(data != null) {
					names[i++] = policy.getName();
					list.add(data);
				}
			}

			displayChart(list, names, linkStatisticTimePeriod, ChartType.flows);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor
	 * 
	 * @param linkStatisticTimePeriod
	 *            Time axis type (minutely, hourly etc.)
	 * @param policies
	 *            Array of flows to display on the chart
	 */
	public ChartDisplayer(LinkStatisticTimePeriod linkStatisticTimePeriod,
			Interface[] interfaces, StatisticsGathererMBean statisticsGatherer,
			Assignments assignments) {

		try {

			this.statisticsGatherer = statisticsGatherer;
			this.assignments = assignments;

			List<List<Map<LinkStatistics, Long>>> list = new LinkedList<List<Map<LinkStatistics, Long>>>();
			String names[] = new String[interfaces.length];
			int i = 0;
			for (Interface interfac : interfaces) {
				names[i++] = interfac.getIpAddress().getAddress();
				list.add(getData(interfac, linkStatisticTimePeriod));
			}

			displayChart(list, names, linkStatisticTimePeriod,
					ChartType.interfaces);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Map<LinkStatistics, Long>> getData(Policy policy,
			LinkStatisticTimePeriod linkStatisticTimePeriod) {

		try {
			logger.trace("Asking for flow statistics for " + policy.getName()
					+ " from the last " + linkStatisticTimePeriod
					+ " and assigmnets " + assignments);
			List<Map<LinkStatistics, Long>> list = statisticsGatherer
					.getPolicyPeriodStatistics(policy, linkStatisticTimePeriod,
							assignments);

			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<Map<LinkStatistics, Long>> getData(Interface interfac,
			LinkStatisticTimePeriod linkStatisticTimePeriod) {

		try {
			logger.trace("Asking for interface statistics for "
					+ interfac.getIpAddress().getAddress() + " from the last "
					+ linkStatisticTimePeriod);
			List<Map<LinkStatistics, Long>> list = statisticsGatherer
					.getInterfacePeriodStatistics(interfac,
							linkStatisticTimePeriod, assignments);

			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void displayChart(List<List<Map<LinkStatistics, Long>>> list,
			String[] titles, LinkStatisticTimePeriod linkStatisticTimePeriod,
			ChartType chartType) throws Exception {

		if (chartWindow == null) {
			createWindow();
		}

		if (!chartWindow.isVisible()) {
			chartWindow.setVisible(true);
		}

		chartWindow.addChart("Chart number " + ++chartNumber + " ", list,
				titles, linkStatisticTimePeriod, chartType);
	}

	public static void close() {
		if (chartWindow != null) {
			chartWindow.dispose();
		}
	}
}
