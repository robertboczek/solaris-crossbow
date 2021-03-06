package org.jims.modules.crossbow.gui.chart;

import java.util.List;
import java.util.Map;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.DataUtil;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

import static com.googlecode.charts4j.Color.*;

public class ChartPreparer {

	/**
	 * Return url to chart based on the data and time to be presented
	 * 
	 * @param list
	 *            List of ChartData elements (max 3)
	 * @param linkStatisticTimePeriod
	 *            Determines X_AXIS legend
	 * @param chartType
	 * 
	 * @return Returns String url
	 */
	public String prepareChart(List<List<Map<LinkStatistics, Long>>> list,
			String[] chartNames, String chartTitle,
			LinkStatisticTimePeriod linkStatisticTimePeriod,
			ChartType chartType, LinkStatistics linkStatistics) {

		Line line1 = null, line2 = null, line3 = null;
		Color c1 = Color.newColor("CA3D05"), c2 = SKYBLUE, c3 = GREEN;

		String legendSuffix = null;
		if (ChartType.flows.equals(chartType)) {
			legendSuffix = " flow";
		} else if (ChartType.interfaces.equals(chartType)) {
			legendSuffix = " interface";
		}

		double max = roundMaxiumValue(list, linkStatistics);
		double min = roundMinimumValue(list, max, linkStatistics);

		System.err.println(list);
		if (list != null) {
			for (List<Map<LinkStatistics, Long>> l : list) {
				for (Map<LinkStatistics, Long> map : l) {
					for (Map.Entry<LinkStatistics, Long> entry : map.entrySet()) {
						System.err.println(entry.getKey() + " "
								+ entry.getValue());
					}
				}
			}
		}

		if (list.size() > 0) {
			double data[] = getData(list.get(0), linkStatistics);
			line1 = Plots.newLine(DataUtil.scaleWithinRange(min, max, data),
					c1, chartNames[0] + legendSuffix);
			line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line1.addShapeMarkers(Shape.DIAMOND, c1, 12);
			line1.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}

		if (list.size() > 1) {
			double data[] = getData(list.get(1), linkStatistics);
			line2 = Plots.newLine(DataUtil.scaleWithinRange(min, max, data),
					c2, chartNames[1] + legendSuffix);
			line2.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line2.addShapeMarkers(Shape.DIAMOND, c2, 12);
			line2.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}

		if (list.size() > 2) {
			double data[] = getData(list.get(2), linkStatistics);
			line3 = Plots.newLine(DataUtil.scaleWithinRange(min, max, data),
					c3, chartNames[2] + legendSuffix);
			line3.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line3.addShapeMarkers(Shape.DIAMOND, c3, 12);
			line3.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}
		LineChart chart = null;
		if (line3 != null) {
			chart = GCharts.newLineChart(line1, line2, line3);
		} else if (line2 != null) {
			chart = GCharts.newLineChart(line1, line2);
		} else {
			chart = GCharts.newLineChart(line1);
		}

		chart.setSize(600, 450);
		chart.setTitle(chartTitle + "for " + linkStatistics, WHITE, 14);
		// chart.addHorizontalRangeMarker(40, 60, Color.newColor(RED, 30));
		// chart.addVerticalRangeMarker(70, 90, Color.newColor(GREEN, 30));
		chart.setGrid(25, 25, 3, 2);

		// Defining axis info and styles
		AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12,
				AxisTextAlignment.CENTER);
		AxisLabels xAxis = null;

		if (linkStatisticTimePeriod.equals(LinkStatisticTimePeriod.MINUTELY)) {
			xAxis = AxisLabelsFactory
					.newAxisLabels(ChartDescription.MINUTE_CHART_X_AXIS);
		} else if (linkStatisticTimePeriod
				.equals(LinkStatisticTimePeriod.FIVE_MINUTELY)) {
			xAxis = AxisLabelsFactory
					.newAxisLabels(ChartDescription.FIVE_MINUTES_CHART_X_AXIS);
		} else if (linkStatisticTimePeriod
				.equals(LinkStatisticTimePeriod.HOURLY)) {
			xAxis = AxisLabelsFactory
					.newAxisLabels(ChartDescription.HOUR_CHART_X_AXIS);
		} else if (linkStatisticTimePeriod
				.equals(LinkStatisticTimePeriod.DAILY)) {
			xAxis = AxisLabelsFactory
					.newAxisLabels(ChartDescription.DAY_CHART_X_AXIS);
		}

		xAxis.setAxisStyle(axisStyle);

		AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels(
				String.valueOf(min), String.valueOf(min + ((max - min) / 4)),
				String.valueOf(min + ((max - min) / 4)), String.valueOf(min + 3
						* ((max - min) / 4)), String.valueOf(max));
		yAxis2.setAxisStyle(axisStyle);
		/*
		 * AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Month", 50.0);
		 * xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14,
		 * AxisTextAlignment.CENTER)); //yAxis.setAxisStyle(axisStyle);
		 * AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Hits", 50.0);
		 * yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14,
		 * AxisTextAlignment.CENTER)); yAxis2.setAxisStyle(axisStyle);
		 */

		// Adding axis info to chart.
		chart.addXAxisLabels(xAxis);
		// chart.addXAxisLabels(xAxis2);
		// chart.addXAxisLabels(xAxis3);
		// chart.addYAxisLabels(yAxis);
		chart.addYAxisLabels(yAxis2);

		// Defining background and chart fills.
		chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
		LinearGradientFill fill = Fills.newLinearGradientFill(0, Color
				.newColor("363433"), 100);
		fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
		chart.setAreaFill(fill);

		return chart.toURLString();
	}

	private double roundMinimumValue(
			List<List<Map<LinkStatistics, Long>>> list, double max, 
			LinkStatistics linkStatistics) {

		double min = max;
		for (List<Map<LinkStatistics, Long>> l : list) {
			for (Map<LinkStatistics, Long> map : l) {
				if (!(linkStatistics.equals(LinkStatistics.IERRORS)
						|| linkStatistics.equals(LinkStatistics.OERRORS) || map.get(linkStatistics) > 0)) {
					min = Math.min(min, map.get(linkStatistics));
				}
			}
		}

		if (min > 0.0) {
			min = 0.5 * min;
		}

		System.err.println("Minimum value " + min);

		return min;
	}

	private double[] getData(List<Map<LinkStatistics, Long>> list,
			LinkStatistics linkStatistics) {
		double[] data = new double[list.size()];
		int i = 0;
		for (Map<LinkStatistics, Long> map : list) {
			data[i++] = map.get(linkStatistics);
		}
		return data;
	}

	private double roundMaxiumValue(List<List<Map<LinkStatistics, Long>>> list,
			LinkStatistics linkStatistics) {

		double max = Double.MIN_VALUE;
		for (List<Map<LinkStatistics, Long>> l : list) {
			for (Map<LinkStatistics, Long> map : l) {
				max = Math.max(max, map.get(linkStatistics));
			}
		}

		if (max > 100) {
			long m = ((long) max) / 100;
			return ((m + 1) * 100);
		} else {
			return max;
		}
	}

}
