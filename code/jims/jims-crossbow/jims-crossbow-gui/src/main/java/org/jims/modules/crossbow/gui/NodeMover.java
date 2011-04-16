package org.jims.modules.crossbow.gui;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.IContainer;


public class NodeMover implements SelectionListener {
	
	public static interface NodesProvider {
		public Collection< GraphNode > provide();
	}


	public NodeMover( Graph graph, NodesProvider nodesProvider, IContainer target ) {
		this.graph = graph;
		this.nodesProvider = nodesProvider;
		this.target = target;
	}
	
	
	@Override
	public void widgetSelected( SelectionEvent e ) {
		
		Collection< GraphItem > rem = new LinkedList< GraphItem >();
		
		for ( Object o : nodesProvider.provide() ) {
			
			// Only GraphNodes are selected
			
			GraphNode graphNode = ( GraphNode ) o;
			
			GraphNode newGraphNode = new GraphNode( target, graphNode.getStyle() );
			
			cloneGraphItem( newGraphNode, graphNode );
			
			// Connections.
			
			List< Object > connections = new LinkedList< Object >();
			connections.addAll( graphNode.getSourceConnections() );
			connections.addAll( graphNode.getTargetConnections() );
			
			for ( Object sconn : connections ) {
				
				GraphConnection conn = ( GraphConnection ) sconn;
				
				GraphConnection newConn = new GraphConnection(
					graph, conn.getStyle(),
					( conn.getSource() == graphNode ) ? newGraphNode : conn.getSource(),
					( conn.getDestination() == graphNode ) ? newGraphNode : conn.getDestination()
				);
				
				cloneGraphItem( newConn, conn );
				
			}
			
			rem.add( graphNode );
			
		}
		
		for ( GraphItem node : rem ) {
			node.dispose();
		}
		
		target.applyLayout();
		
	}
	
	
	private void cloneGraphItem( GraphItem target, GraphItem source ) {
		
		target.setImage( source.getImage() );
		target.setText( source.getText() );
		target.setData( source.getData() );
		target.setVisible( source.isVisible() );
		
		if ( target instanceof GraphNode ) {
			( ( GraphNode ) target ).setTooltip( ( ( GraphNode ) source ).getTooltip() );
		}
		
		if ( target instanceof GraphConnection ) {
			
			GraphConnection sconn = ( GraphConnection ) source,
			                tconn = ( GraphConnection ) target;
			
			tconn.setLineColor( sconn.getLineColor() );
			tconn.setLineStyle( sconn.getLineStyle() );
			tconn.setLineWidth( sconn.getLineWidth() );
			
		}
		
	}
	
	
	@Override
	public void widgetDefaultSelected( SelectionEvent e ) {}
	

	private Graph graph;
	private NodesProvider nodesProvider;
	private IContainer target;
	
	private static final Logger logger = Logger.getLogger( NodeMover.class );

}
