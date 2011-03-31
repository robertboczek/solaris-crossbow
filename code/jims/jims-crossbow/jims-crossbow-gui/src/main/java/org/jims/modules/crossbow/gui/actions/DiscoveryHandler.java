package org.jims.modules.crossbow.gui.actions;

import java.util.Map;

import org.eclipse.zest.core.widgets.Graph;
import org.jims.modules.crossbow.objectmodel.ObjectModel;


public class DiscoveryHandler {
	
	public DiscoveryHandler( SupervisorProxyFactory supervisorFactory,
	                         ModelToGraphTranslator translator ) {
		
		this.supervisorFactory = supervisorFactory;
		this.translator = translator;
		
	}
	
	
	public Graph handle( Graph graph ) {
		
		Map< String, ObjectModel > models = supervisorFactory.createSupervisor().discover();
		
		translator.translate( graph, models.values().iterator().next() );
		
		return graph;
		
	}
	
	
	private SupervisorProxyFactory supervisorFactory;
	private ModelToGraphTranslator translator;

}
