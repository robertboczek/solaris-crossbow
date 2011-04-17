package org.jims.modules.crossbow.gui.chart;

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
	 * Return url to chart based on the data
	 * and time to be presented
	 * 
	 * @param data Array of data to be presented on the chart (max 3)
	 * @param legend array of chart legends
	 * @param chartTimeType Determines X_AXIS legend
	 * 
	 * @return Returns String url
	 */
	public String prepareChart(double [][]data, String []legend, ChartTimeType chartTimeType) {
		
		Line line1 = null, line2 = null, line3 = null;
		Color c1 = Color.newColor("CA3D05"), c2 = SKYBLUE, c3 = GREEN;
		
		double max = roundMaxiumValue(data);
		
		if(data.length > 0) {
			line1 = Plots.newLine(DataUtil.scaleWithinRange(0.0, max, data[0]), c1, legend[0]);
			line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line1.addShapeMarkers(Shape.DIAMOND, c1, 12);
			line1.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}
		
		if(data.length > 1) {
			line2 = Plots.newLine(DataUtil.scaleWithinRange(0.0, max, data[1]), c2, legend[1]);
			line2.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line2.addShapeMarkers(Shape.DIAMOND, c2, 12);
			line2.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}
		
		if(data.length > 2) {
			line3 = Plots.newLine(DataUtil.scaleWithinRange(0.0, max, data[2]), c3, legend[2]);
			line3.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
			line3.addShapeMarkers(Shape.DIAMOND, c3, 12);
			line3.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		}
		LineChart chart = null;
		if(line3 != null) {
			chart = GCharts.newLineChart(line1, line2, line3);
		} else if(line2 != null) {
			chart = GCharts.newLineChart(line1, line2);
		} else {
			chart = GCharts.newLineChart(line1);
		}
		
        chart.setSize(600, 450);
        chart.setTitle("Average bandwidth on selected interfaces and flows", WHITE, 14);
        //chart.addHorizontalRangeMarker(40, 60, Color.newColor(RED, 30));
        //chart.addVerticalRangeMarker(70, 90, Color.newColor(GREEN, 30));
        chart.setGrid(25, 25, 3, 2);
        
        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = null;
        if(chartTimeType.equals(ChartTimeType.MINUTELY)) {
        	xAxis = AxisLabelsFactory.newAxisLabels(ChartDescription.MINUTE_CHART_X_AXIS);
        } else if(chartTimeType.equals(ChartTimeType.FIVE_MINUTELY)) {
        	xAxis = AxisLabelsFactory.newAxisLabels(ChartDescription.FIVE_MINUTES_CHART_X_AXIS);
        } else if(chartTimeType.equals(ChartTimeType.HOURLY)) {
        	xAxis = AxisLabelsFactory.newAxisLabels(ChartDescription.HOUR_CHART_X_AXIS);
        } else if(chartTimeType.equals(ChartTimeType.DAILY)) {
        	xAxis = AxisLabelsFactory.newAxisLabels(ChartDescription.DAY_CHART_X_AXIS);
        }
        
        xAxis.setAxisStyle(axisStyle);
        
        
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("0.0", String.valueOf(max/4), String.valueOf(max/2), String.valueOf(3*max/4), String.valueOf(max));
        yAxis2.setAxisStyle(axisStyle);
        /*AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Month", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        //yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Hits", 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis2.setAxisStyle(axisStyle);*/

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        //chart.addXAxisLabels(xAxis2);
        //chart.addXAxisLabels(xAxis3);
        //chart.addYAxisLabels(yAxis);
        chart.addYAxisLabels(yAxis2);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        return chart.toURLString();
	}
	
	
	private double roundMaxiumValue(double data[][]) {
		double max = Double.MIN_VALUE;
		for(double[] t : data){
			for(double a : t){
				max = Math.max(max, a); 
			}
		}
		
		if(max > 100) {
			long m = ((long) max)/100;
			return ((m+1) * 100);
		} else {
			return max;
		}
	}

}
