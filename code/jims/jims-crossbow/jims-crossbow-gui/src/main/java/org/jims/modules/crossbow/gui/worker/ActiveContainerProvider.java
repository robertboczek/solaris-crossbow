package org.jims.modules.crossbow.gui.worker;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphContainer;
import org.jims.modules.crossbow.gui.worker.TooltipStatsHandler.ContainerProvider;


public class ActiveContainerProvider implements ContainerProvider {
	
	public ActiveContainerProvider( Display display, Graph graph ) {
		this.display = display;
		this.graph = graph;
	}
	
	
	@Override
	public GraphContainer provide( final String url ) {
		
		final List< GraphContainer > conts = new LinkedList< GraphContainer >();
		
		display.syncExec( new Runnable() {
			
			@Override
			public void run() {
					for ( Object node : graph.getNodes() ) {
						if ( ( node instanceof GraphContainer )
						     && ( url.equals( ( ( GraphContainer ) node ).getText() ) ) ) {
							conts.add( ( GraphContainer ) node );
							return;
						}
					}
			}
			
		} );
		
		return conts.get( 0 );
		
	}

					
	private Display display;
	private Graph graph;
	
}
