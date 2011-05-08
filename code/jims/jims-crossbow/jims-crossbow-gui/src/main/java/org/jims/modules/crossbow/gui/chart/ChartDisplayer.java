package org.jims.modules.crossbow.gui.chart;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.swing.ImageIcon;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGatherer;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGathererMBean;
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
	private static final String OBJECT_NAME = "Crossbow:type=StatisticsGatherer";

	private static ChartWindow chartWindow;

	private StatisticsGathererMBean statisticsGatherer;

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
			Policy[] policies, StatisticsGathererMBean statisticsGatherer) {
		
		try {

			this.statisticsGatherer = statisticsGatherer;

			List<List<Map<LinkStatistics, Long>>> list = new LinkedList<List<Map<LinkStatistics, Long>>>();
			String names[] = new String[policies.length]; int i = 0;
			for(Policy policy : policies) {
				names[i] = policy.getName();
				list.add(getData(policy, linkStatisticTimePeriod));
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
			Interface[] interfaces, StatisticsGathererMBean statisticsGatherer) {
		
		try {

			this.statisticsGatherer = statisticsGatherer;

			List<List<Map<LinkStatistics, Long>>> list = new LinkedList<List<Map<LinkStatistics, Long>>>();
			String names[] = new String[interfaces.length]; int i = 0;
			for(Interface interfac : interfaces) {
				names[i] = interfac.getIpAddress().getAddress();
				list.add(getData(interfac, linkStatisticTimePeriod));
			}

			displayChart(list, names, linkStatisticTimePeriod, ChartType.interfaces);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<Map<LinkStatistics, Long>> getData(Policy policy,
			LinkStatisticTimePeriod linkStatisticTimePeriod) {

		try {
			List<Map<LinkStatistics, Long>> list = statisticsGatherer
					.getPolicyStatistics(policy, linkStatisticTimePeriod);
			
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private List<Map<LinkStatistics, Long>> getData(Interface interfac,
			LinkStatisticTimePeriod linkStatisticTimePeriod) {

		try {
			List<Map<LinkStatistics, Long>> list = statisticsGatherer
					.getInterfaceStatistics(interfac, linkStatisticTimePeriod);
			
			return list;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void displayChart(List<List<Map<LinkStatistics, Long>>> list, String []titles, 
			LinkStatisticTimePeriod linkStatisticTimePeriod, ChartType chartType) throws Exception {

		if (chartWindow == null) {
			createWindow();
		}

		if (!chartWindow.isVisible()) {
			chartWindow.setVisible(true);
		}

		chartWindow.addChart("Chart number " + ++chartNumber + " ",
				list, titles, linkStatisticTimePeriod, chartType);
	}	
	

	public static void close() {
		if (chartWindow != null) {
			chartWindow.dispose();
		}
	}
}
