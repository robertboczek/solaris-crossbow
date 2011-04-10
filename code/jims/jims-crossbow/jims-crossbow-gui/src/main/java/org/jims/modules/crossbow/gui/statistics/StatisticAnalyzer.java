package org.jims.modules.crossbow.gui.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.infrastructure.gatherer.StatisticsGathererMBean;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

public class StatisticAnalyzer {

	public static final int REFRESH_TIME = 60000;// 1min

	private List<GraphConnectionData> graphConnectionDataList;
	private Map<Interface, EndpointStatistic> interfacesMap = new HashMap<Interface, EndpointStatistic>();

	private ComponentProxyFactory componentProxyFactory;

	public StatisticAnalyzer(List<GraphConnectionData> graphConnectionDataList,
			ComponentProxyFactory componentProxyFactory) {

		this.graphConnectionDataList = graphConnectionDataList;
		this.componentProxyFactory = componentProxyFactory;

		prepareInterfaceMap();

	}

	private void prepareInterfaceMap() {

		for (GraphConnectionData g : graphConnectionDataList) {

			System.out.println(g.getEndp1() + " " + g.getEndp2());
			if (g.getEndp1() != null) {

				if (g.getEndp1() instanceof Interface) {

					Interface interf = (Interface) (g.getEndp1());
					EndpointStatistic endpointStatistic = new EndpointStatistic(
							interf);
					g.setEndp1Statistic(endpointStatistic);
					interfacesMap.put(interf, endpointStatistic);
				}

			}

			if (g.getEndp2() != null) {
				if (g.getEndp2() instanceof Interface) {
					Interface interf = (Interface) g.getEndp2();
					EndpointStatistic endpointStatistic = new EndpointStatistic(
							interf);
					g.setEndp2Statistic(endpointStatistic);
					interfacesMap.put(interf, endpointStatistic);
				}
			}
		}

	}

	/**
	 * Uruchamia wszystkie watki zbierajace statystyki
	 */
	public void startGatheringStatistics() {

		System.out.println(interfacesMap.size());
		for (Map.Entry<Interface, EndpointStatistic> entry : interfacesMap
				.entrySet()) {
			new Thread(entry.getValue()).start();
		}

	}

	/**
	 * Zatrzymuej wszystkie watki zbierajace statystki
	 */
	public void stopGatheringStatistics() {

		for (Map.Entry<Interface, EndpointStatistic> entry : interfacesMap
				.entrySet()) {
			entry.getValue().stop();
		}

	}

	public class EndpointStatistic implements Runnable {

		private Interface interfac;
		private Map<LinkStatistics, Double> averageStatistics = new HashMap<LinkStatistics, Double>();
		private Map<LinkStatistics, Long> totalStatistics = new HashMap<LinkStatistics, Long>();

		private boolean stop = false;

		public EndpointStatistic(Interface interfac) {
			this.interfac = interfac;

			totalStatistics.put(LinkStatistics.IERRORS, 0L);
			totalStatistics.put(LinkStatistics.IPACKETS, 0L);
			totalStatistics.put(LinkStatistics.OBYTES, 0L);
			totalStatistics.put(LinkStatistics.OPACKETS, 0L);
			totalStatistics.put(LinkStatistics.OERRORS, 0L);
			totalStatistics.put(LinkStatistics.RBYTES, 0L);

			averageStatistics.put(LinkStatistics.IERRORS, 0.0);
			averageStatistics.put(LinkStatistics.IPACKETS, 0.0);
			averageStatistics.put(LinkStatistics.OBYTES, 0.0);
			averageStatistics.put(LinkStatistics.OPACKETS, 0.0);
			averageStatistics.put(LinkStatistics.OERRORS, 0.0);
			averageStatistics.put(LinkStatistics.RBYTES, 0.0);

		}

		public void stop() {
			stop = true;
		}

		@Override
		public void run() {

			System.out.println("Start gathering statistics for interface "
					+ interfac);
			try {
				while (!stop) {

					StatisticsGathererMBean statisticGatherer = null;

					try {
						statisticGatherer = componentProxyFactory
								.createStatisticAnalyzer();
					} catch (Exception e) {

						e.printStackTrace();
						Thread.sleep(REFRESH_TIME);
						continue;
					}

					if (statisticGatherer != null) {
						Map<LinkStatistics, Long> statistics = statisticGatherer
								.getInterfaceStatistics(interfac);
						updateStatistics(statistics);
					}
					Thread.sleep(REFRESH_TIME);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void updateStatistics(Map<LinkStatistics, Long> statistics) {

			for (Map.Entry<LinkStatistics, Long> linkStatistics : statistics
					.entrySet()) {
				averageStatistics.put(linkStatistics.getKey(), (linkStatistics
						.getValue() - totalStatistics.get(linkStatistics
						.getKey())) / 60.0);
				totalStatistics.put(linkStatistics.getKey(), linkStatistics
						.getValue());

			}
		}

		public Map<LinkStatistics, Double> getAverageStatistics() {
			return averageStatistics;
		}

		public Map<LinkStatistics, Long> getTotalStatistics() {
			return totalStatistics;
		}

		public Long getReceivedBytes() {
			return totalStatistics.get(LinkStatistics.RBYTES);
		}

		public Long getSentBytes() {
			return totalStatistics.get(LinkStatistics.OBYTES);
		}

		public Long getReceivedPackets() {
			return totalStatistics.get(LinkStatistics.IPACKETS);
		}

		public Long getSentPackets() {
			return totalStatistics.get(LinkStatistics.OPACKETS);
		}

	}

}
