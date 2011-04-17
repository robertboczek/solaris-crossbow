package org.jims.modules.crossbow.gui.chart;

public class ChartDescription {
	
	public static final String[] MINUTE_CHART_X_AXIS = { "-60s", "-45s", "-30s", "-15s", "now" };
	public static final String[] FIVE_MINUTES_CHART_X_AXIS = { "-5min", "-4min", "-3min", "-2min", "-1min", "now" };
	public static final String[] HOUR_CHART_X_AXIS = { "-1h", "-45min", "-30min", "-15min", "now" };
	public static final String[] DAY_CHART_X_AXIS = { "-1day", "-18h", "-12h", "-6h", "now" };
	
	public static final String Y_AXIS_DESCRIPTION = "Average bandwidth [kb/s]";
	public static final String X_AXIS_DESCRIPTION = "Time";
	
	public static final String CHART_TITLE = "Average bandwidth on selected interfaces and flows";
	
	

}
