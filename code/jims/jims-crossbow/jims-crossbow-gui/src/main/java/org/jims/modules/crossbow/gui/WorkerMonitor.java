package org.jims.modules.crossbow.gui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.zest.core.widgets.CGraphNode;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;


public class WorkerMonitor implements ConnectionTester.ConnectionStatusListener {

	public WorkerMonitor( Graph g, ComponentProxyFactory proxyFactory, Display display ) {
		
		this.g = g;
		this.proxyFactory = proxyFactory;
		this.display = display;
		
		this.popupMenu = new Menu( g );
		this.moveMenu = new Menu( popupMenu );
		
		MenuItem cascade = new MenuItem( popupMenu, SWT.CASCADE );
		cascade.setText( "Move to" );
		cascade.setMenu( moveMenu );
		
		nodesProvider = new NodeMover.NodesProvider() {
			@Override
			public Collection<GraphNode> provide() {
				return WorkerMonitor.this.g.getSelection();
			}
		};
		
		// Register the listener.
		
		g.addListener( SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent( Event event ) {
				
				if ( ( 3 == event.button ) && ( 0 != WorkerMonitor.this.g.getSelection().size() ) ) {
					
					for ( Object o : WorkerMonitor.this.g.getSelection() ) {
						if ( ( ! ( o instanceof GraphNode ) ) || ( o instanceof GraphContainer ) ) {
							return;
						}
					}
					
					popupMenu.setVisible( true );
					
				}
			}
		} );
		
		/*
		g.addListener( SWT.MouseWheel, new Listener() {
			
			@Override
			public void handleEvent( Event event ) {
				
				if ( ( 1 == WorkerMonitor.this.g.getSelection().size() ) ) {
					
					double scale = ( event.count > 0 ) ? 1.2 : 1 / 1.2;
					
					GraphContainer container = ( GraphContainer ) WorkerMonitor.this.g.getSelection().get( 0 );
					
					Dimension dim = container.getSize();
					container.setSize( dim.preciseWidth(), 50 );
					// container.setScale( 1 );
				
				}
				
			}
		} );
		*/
		
	}
	

	@Override
	public void connected( String server, int port ) {
		
		moveItems.clear();
		
		display.asyncExec( new Runnable() {
			
			@Override
			public void run() {
				
				disposeContainers();
				
				// Create GraphContainers for workers and update the popup menu.
				
				Collection< String > workers = proxyFactory.createProxy( SupervisorMBean.class ).getWorkers();
				for ( String worker : workers ) {
					
					// Create the container.
					
					GraphContainer container = new GraphContainer( g, SWT.NONE );
					container.setLayoutAlgorithm( new TreeLayoutAlgorithm( LayoutStyles.NO_LAYOUT_NODE_RESIZING ), false );
					container.setText( worker );
					container.setData( worker );
					container.open( true );
					
					// Add an item to the popup menu.
					
					MenuItem item = new MenuItem( moveMenu, SWT.NONE );
					
					// item.setMenu( moveMenu );
					
					item.setText( worker );
					item.addSelectionListener( new NodeMover( g, nodesProvider, container ) );
					
					moveItems.add( item );
					
				}
				
			}
			
		} );
		
	}
	
	
	@Override
	public void disconnected( String server, int port ) {
		
		display.asyncExec( new Runnable() {
			
			@Override
			public void run() {
				
				disposeContainers();
				
				for ( MenuItem item : moveItems ) {
					item.dispose();
				}
				
			}
			
		} );
		
	}
	
	
	private void disposeContainers() {
		
		Collection< GraphContainer > torem = new LinkedList< GraphContainer >();
		
		for ( Object o : g.getNodes() ) {
			if ( o instanceof GraphContainer ) {
				torem.add( ( GraphContainer ) o );
			}
		}
		
		for ( final GraphContainer container : torem ) {
			
			new NodeMover( g, new NodeMover.NodesProvider() {
				
				@Override
				public Collection< GraphNode > provide() {
					
					Collection< GraphNode > res = new LinkedList< GraphNode >();
					
					for ( Object node : container.getNodes() ) {
						res.add( ( GraphNode ) node );
					}
					
					return res;
				}
			}, g ).widgetSelected( null );
			
			container.close( true );
			container.dispose();
		}
		
	}
	
	
	private final Graph g;
	private ComponentProxyFactory proxyFactory;
	private Display display;
	private Menu popupMenu, moveMenu;
	private Collection< MenuItem > moveItems = new LinkedList< MenuItem >();
	private NodeMover.NodesProvider nodesProvider;
	
	private static final Logger logger = Logger.getLogger( WorkerMonitor.class );

}
