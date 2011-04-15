package org.jims.modules.crossbow.gui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;


public class WorkerMonitor implements ConnectionTester.ConnectionStatusListener {

	public WorkerMonitor( Graph g, ComponentProxyFactory proxyFactory, Display display ) {
		
		this.g = g;
		this.proxyFactory = proxyFactory;
		this.display = display;
		
		this.popupMenu = new Menu( g );
		
		// Register the listener.
		
		g.addListener( SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent( Event event ) {
				
				if ( ( 3 == event.button ) && ( 0 != WorkerMonitor.this.g.getSelection().size() ) ) {
					
					for ( Object o : WorkerMonitor.this.g.getSelection() ) {
						if ( ! ( o instanceof GraphNode ) ) {
							return;
						}
					}
					
					popupMenu.setVisible( true );
					
				}
			}
		} );
		
	}
	

	@Override
	public void connected( String server ) {
		
		display.asyncExec( new Runnable() {
			
			@Override
			public void run() {
				
				disposeContainers();
				
				// Create GraphContainers for workers and update the popup menu.
				
				Collection< String > workers = proxyFactory.createProxy( SupervisorMBean.class ).getWorkers();
				for ( String worker : workers ) {
					
					// Create the container.
					
					GraphContainer container = new GraphContainer( g, SWT.NONE );
					container.setText( worker );
					container.open( true );
					
					// Add an item to the popup menu.
					
					MenuItem item = new MenuItem( popupMenu, SWT.NONE );
					
					item.setText( "Move to " + worker );
					item.addSelectionListener( new NodeMover( g, container ) );
					
				}
				
			}
			
		} );
		
	}
	
	
	@Override
	public void disconnected( String server ) {
		
		display.asyncExec( new Runnable() {
			
			@Override
			public void run() {
				disposeContainers();
				disposeMoveItems();
			}
			
		} );
		
	}
	
	
	private void disposeContainers() {
		
		List< Object > torem = new LinkedList< Object >();
		
		for ( Object o : g.getNodes() ) {
			if ( o instanceof GraphContainer ) {
				torem.add( o );
			}
		}
		
		for ( Object o : torem ) {
			( ( GraphContainer ) o ).dispose();
		}
		
	}
	
	
	private void disposeMoveItems() {
		
		Collection< MenuItem > rem = new LinkedList< MenuItem >();
		
		for ( MenuItem item : popupMenu.getItems() ) {
			
			if ( item.getText().startsWith( "Move to" ) ) {  // TODO-DAWID change!
				rem.add( item );
			}
			
		}
		
		for ( MenuItem item : rem ) {
			item.dispose();
		}
		
	}
	
	
	private final Graph g;
	private ComponentProxyFactory proxyFactory;
	private Display display;
	private Menu popupMenu;
	
	private static final Logger logger = Logger.getLogger( WorkerMonitor.class );

}
