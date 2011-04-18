package org.jims.modules.crossbow.gui.worker;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.jims.modules.solaris.solaris10.mbeans.GlobalZoneMonitoringMBean;


public class TooltipStatsHandler implements StatsManager.StatsHandler {
	
	public static interface ContainerProvider {
		public GraphContainer provide( String url );
	}

	
	public TooltipStatsHandler( ContainerProvider provider, Display display, int interval ) {
		this.provider = provider;
		this.display = display;
		this.interval = interval;
	}

	@Override
	public void handle( final Map< String, GlobalZoneMonitoringMBean > monitors ) {
		
		this.monitors = monitors;
		
		task = new TimerTask() {
			
			@Override
			public void run() {
				
				for ( Map.Entry< String, GlobalZoneMonitoringMBean > e : monitors.entrySet() ) {
					
					String url = e.getKey();
					final GlobalZoneMonitoringMBean monitor = e.getValue();
					
					logger.debug( String.format( "Refreshing worker's statistics (url: %s).",
					                             url ) );
					
					final GraphContainer container = TooltipStatsHandler.this.provider.provide( url );
					
					if ( null != container ) {
						
						final String stats = String.format(   "CPU usage: %.2f\n"
						                                    + "Mem usage: %.2f",
						                                    monitor.getPercentageCpuUsage(),
						                                    monitor.getPercentageMemoryUsage() );
						
						TooltipStatsHandler.this.display.asyncExec( new Runnable() {
							
							@Override
							public void run() {
								if ( ! container.isDisposed() ) {
									
									IFigure figure = container.getNodeFigure();
									
									if ( null == figure.getToolTip() ) {
										figure.setToolTip( new Label( stats ) );
									} else {
										( ( Label ) figure.getToolTip() ).setText( stats );
									}
									
								}
							}
							
						} );
						
					}
					
				}
				
			}
		};
		
		timer = new Timer();
		timer.schedule( task, 0, interval );
		
	}
	
	
	@Override
	public void stop() {
		if ( null != timer ) {
			timer.cancel();
		}
	}
	
	
	Map< String, GlobalZoneMonitoringMBean > monitors;
	private ContainerProvider provider;
	private Display display;
	TimerTask task;
	Timer timer;
	private int interval;
	
	private static final Logger logger = Logger.getLogger( TooltipStatsHandler.class );

}
