package org.jims.modules.crossbow.gui;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;


public class WorkerMonitor implements ConnectionTester.ConnectedListener {

	public WorkerMonitor( Graph g, ComponentProxyFactory proxyFactory, Display display ) {
		
		this.g = g;
		this.proxyFactory = proxyFactory;
		this.display = display;
		
	}
	

	@Override
	public void connected( String server ) {
		
		display.asyncExec( new Runnable() {
			
			@Override
			public void run() {
				
				List< Object > torem = new LinkedList< Object >();
				
				for ( Object o : g.getNodes() ) {
					if ( o instanceof GraphContainer ) {
						torem.add( o );
					}
				}
				
				for ( Object o : torem ) {
					( ( GraphContainer ) o ).dispose();
				}
				
				for ( String worker : proxyFactory.createProxy( SupervisorMBean.class ).getWorkers() ) {
					
					GraphContainer container = new GraphContainer( g, SWT.NONE );
					container.setText( worker );
					container.open( true );
					
				}
				
			}
			
		} );
		
	}
	
	
	private Graph g;
	private ComponentProxyFactory proxyFactory;
	private Display display;
	
	private static final Logger logger = Logger.getLogger( WorkerMonitor.class );

}
