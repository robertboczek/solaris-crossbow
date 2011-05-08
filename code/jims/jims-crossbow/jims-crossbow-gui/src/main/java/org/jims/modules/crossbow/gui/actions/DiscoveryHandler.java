package org.jims.modules.crossbow.gui.actions;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.widgets.Graph;
import org.jims.modules.crossbow.gui.NetworkStructureHelper;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.gui.dialogs.SelectDiscoverProjectDialog;
import org.jims.modules.crossbow.infrastructure.Pair;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;
import org.jims.modules.crossbow.objectmodel.Assignments;
import org.jims.modules.crossbow.objectmodel.ObjectModel;

public class DiscoveryHandler {

	private String selectedProject;

	public DiscoveryHandler(SupervisorProxyFactory supervisorFactory,
			ModelToGraphTranslator translator) {

		this.supervisorFactory = supervisorFactory;
		this.translator = translator;

	}

	public void setSelectedProject(String text) {
		this.selectedProject = text;
	}

	public Graph handle(Graph graph, Text projectId,
			NetworkStructureHelper networkStructureHelper,
			List<GraphConnectionData> graphConnectionDataList) {

		networkStructureHelper.clearAllItems();
		graphConnectionDataList.clear();

		SupervisorMBean supervisorMBean = supervisorFactory.createSupervisor();
		if (supervisorMBean == null) {
			MessageDialog.openError(null, "Connection problem",
					"You must be connected to discover");
		}
		Map<String, Pair< ObjectModel, Assignments > > models = supervisorMBean.discover();

		String[] projecNames = models.keySet().toArray( new String[ 0 ] );
			
		SelectDiscoverProjectDialog dialog = new SelectDiscoverProjectDialog(
				graph.getShell(), projecNames, this);
		dialog.create();
		if (dialog.open() == Window.OK) {
			if (models.get(selectedProject) != null) {
				logger.trace("Starting restoring project: " + selectedProject
						+ " structure");
				translator.translate(graph, models.get(selectedProject).first, models.get(selectedProject).second,
						networkStructureHelper, graphConnectionDataList);
				projectId.setText(selectedProject);
			} else {
				logger.error("Selected project was not found");
			}

		}

		return graph;

	}

	private static final Logger logger = Logger
			.getLogger(DiscoveryHandler.class);
	private SupervisorProxyFactory supervisorFactory;
	private ModelToGraphTranslator translator;

}
