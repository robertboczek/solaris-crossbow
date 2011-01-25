package org.jims.modules.crossbow.gui;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.jims.modules.crossbow.gui.actions.ConfigurationUtil;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.gui.data.GraphNodeData;
import org.jims.modules.crossbow.gui.data.ResourceGraphNodeData;
import org.jims.modules.crossbow.gui.data.RouterGraphNodeData;
import org.jims.modules.crossbow.gui.data.SwitchGraphNodeData;
import org.jims.modules.crossbow.gui.dialogs.EditResourceConnectionParametersDialog;
import org.jims.modules.crossbow.gui.dialogs.EditResourceDialog;
import org.osgi.framework.Bundle;


/**
 * Main view in our GUI allows creating network structure with certain QoS
 * 
 * @author robert
 * 
 */
public class View extends ViewPart {
	public static final String ID = "org.jims.modules.crossbow.gui.view";
	private Graph graph;
	private int layout = 1;

	private Group buttonGroup;
	private Button addRouterButton, addSwitchButton, addResourceButton;
	private Button removeSelectedButton, connectButton;
	private Button validateButton, deployButton, saveButton, loadButton;

	private Text projectId;

	private List<GraphNodeData> graphItems = new LinkedList<GraphNodeData>();

	private Composite parent;

	public void createPartControl(final Composite parent) {

		this.parent = parent;

		buttonGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		buttonGroup.setText("Create requested resources");
		buttonGroup.setSize(200, 200);

		projectId = new Text(buttonGroup, SWT.NONE);
		projectId.setText("");
		projectId.setLocation(70, 20);
		projectId.pack();

		Label label = new Label(buttonGroup, SWT.NONE);
		label.setText("Project ID:");
		label.setLocation(10, 20);
		label.pack();

		// dodaje router do modelu sieci
		addRouterButton = new Button(buttonGroup, SWT.PUSH);
		addRouterButton.setToolTipText("Add router");
		addRouterButton.setLocation(30, 50);
		addRouterButton.setSize(60, 60);
		addRouterButton.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addRouterButton.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addRouterButton.setImage(loadImage("icons/router.jpg"));

		addRouterButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				GraphNodeData graphNodeData = new RouterGraphNodeData();
				createGraphItem(graphNodeData, "icons/router.jpg");
				graphItems.add(graphNodeData);
			}
		});

		// dodaje switch do modelu sieci
		addSwitchButton = new Button(buttonGroup, SWT.PUSH);
		addSwitchButton.setToolTipText("Add switch");
		addSwitchButton.setLocation(90, 50);
		addSwitchButton.setImage(loadImage("icons/switch.jpg"));
		addSwitchButton.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addSwitchButton.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addSwitchButton.setSize(60, 60);
		addSwitchButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				GraphNodeData graphNodeData = new SwitchGraphNodeData();
				createGraphItem(graphNodeData, "icons/switch.jpg");
				graphItems.add(graphNodeData);
			}

		});

		// dodaje nowy zasob do modelu sieci
		addResourceButton = new Button(buttonGroup, SWT.PUSH);
		addResourceButton.setToolTipText("Add resource");
		addResourceButton.setLocation(30, 110);
		addResourceButton.setImage(loadImage("icons/resource.jpg"));
		addResourceButton.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addResourceButton.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addResourceButton.setSize(60, 60);
		addResourceButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				GraphNodeData graphNodeData = new ResourceGraphNodeData();
				createGraphItem(graphNodeData, "icons/resource.jpg");
				graphItems.add(graphNodeData);
			}
		});

		// usuwa zaznaczone elementy z modelu sieci
		removeSelectedButton = new Button(buttonGroup, SWT.PUSH);
		removeSelectedButton.setToolTipText("Remove selected");
		removeSelectedButton.setLocation(90, 110);
		removeSelectedButton.setImage(loadImage("icons/delete.jpg"));
		removeSelectedButton.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		removeSelectedButton.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		removeSelectedButton.setSize(60, 60);
		removeSelectedButton.setEnabled(false);
		removeSelectedButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				removeSelected();
			}

		});

		// otwiera zaznaczony element modelu sieci do edycji
		connectButton = new Button(buttonGroup, SWT.PUSH);
		connectButton.setToolTipText("Connect resources");
		connectButton.setLocation(30, 170);
		connectButton.setImage(loadImage("icons/connect.jpg"));
		connectButton.setBackground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		connectButton.setForeground(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		connectButton.setSize(60, 60);
		connectButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List list = graph.getSelection();
				GraphConnection graphConnection = new GraphConnection(graph,
						SWT.None, (GraphNode) (list.get(0)), (GraphNode) (list
								.get(1)));
				graphConnection.setLineWidth(2);
				graphConnection.setLineColor(parent.getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				GraphConnectionData graphConnectionData = new GraphConnectionData(
						(GraphNodeData) (((GraphNode) (list.get(0))).getData()),
						(GraphNodeData) (((GraphNode) (list.get(1))).getData()));
				graphConnection.setData(graphConnectionData);
				graphConnection
						.setText(updateGraphConnectionText(graphConnectionData));
				((GraphNodeData) ((GraphNode) (list.get(0))).getData()).addEndpoing(graphConnectionData);
				((GraphNodeData) ((GraphNode) (list.get(1))).getData()).addEndpoing(graphConnectionData);
			}

		});
		connectButton.setEnabled(false);

		// przesyla stworzona strukture sieci do mappera
		validateButton = new Button(buttonGroup, SWT.PUSH);
		validateButton.setImage(loadImage("icons/verify.jpg"));
		validateButton.setLocation(90, 170);
		validateButton.setSize(60, 60);
		validateButton.setToolTipText("Validate network correctness");
		validateButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// @todo validate network structure

				MessageDialog.openInformation(null, "Validation result",
						"Network structure has been successfully validated");

				deployButton.setEnabled(true);
			}

		});

		// przesyla stworzona strukture sieci do mappera
		deployButton = new Button(buttonGroup, SWT.PUSH);
		deployButton.setImage(loadImage("icons/deployIt.jpg"));
		deployButton.setLocation(30, 230);
		deployButton.setSize(60, 60);
		deployButton.setToolTipText("Deploy network");
		deployButton.setEnabled(false);
		deployButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// @todo send network structure to mapper
			}

		});

		// zapisuje aktualna budowe sieci do pliku
		saveButton = new Button(buttonGroup, SWT.PUSH);
		saveButton.setImage(loadImage("icons/save.jpg"));
		saveButton.setLocation(90, 230);
		saveButton.setSize(60, 60);
		saveButton.setToolTipText("Save network");
		saveButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// File standard dialog
				FileDialog fileDialog = new FileDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						SWT.SAVE);
				fileDialog
						.setText("Type File Name Where To Save With Network Structure");
				fileDialog.setFilterExtensions(new String[] { "*.cro" });
				fileDialog.setFilterNames(new String[] { "Textfiles(*.cro)" });
				String selected = fileDialog.open();
				if (selected != null) {
					ConfigurationUtil.saveNetwork(selected, graphItems);
				}
			}
		});

		// wczytuje aktualna budowe sieci z pliku
		loadButton = new Button(buttonGroup, SWT.PUSH);
		loadButton.setImage(loadImage("icons/load.jpg"));
		loadButton.setLocation(30, 290);
		loadButton.setSize(60, 60);
		loadButton.setToolTipText("Load network structure");
		loadButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {

				// File standard dialog
				FileDialog fileDialog = new FileDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell());
				fileDialog.setText("Select File With Network Structure");
				fileDialog.setFilterExtensions(new String[] { "*.cro" });
				fileDialog.setFilterNames(new String[] { "Textfiles(*.cro)" });
				String selected = fileDialog.open();
				
				if (selected != null) {					

					graphItems.clear();

					for (Object object : graph.getConnections())
						((GraphConnection) object).setVisible(false);
					for (Object object : graph.getNodes())
						((GraphItem) object).setVisible(false);

					ConfigurationUtil.loadNetwork(selected, graphItems);
					for (GraphNodeData graphNode : graphItems) {
						String iconFileName = null;
						if (graphNode instanceof ResourceGraphNodeData) {
							iconFileName = "icons/resource.jpg";
						} else if (graphNode instanceof SwitchGraphNodeData) {
							iconFileName = "icons/switch.jpg";
						} else if (graphNode instanceof RouterGraphNodeData) {
							iconFileName = "icons/router.jpg";
						}

						createGraphItem(graphNode, iconFileName);
					}

					restoreGraphNodeConnections();
				}
			}

		});			
		

		buttonGroup.pack();

		graph = new Graph(parent, SWT.NONE);

		graph.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {

				List list = graph.getSelection();
				if (list.size() > 0) {
					removeSelectedButton.setEnabled(true);
				} else {
					removeSelectedButton.setEnabled(false);
				}

				if (list.size() == 2) {
					if (list.get(0) instanceof GraphNode
							&& list.get(1) instanceof GraphNode) {
						connectButton.setEnabled(true);
					} else {
						connectButton.setEnabled(false);
					}
				} else {
					connectButton.setEnabled(false);
				}
			}

		});

		graph.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {

				List list = graph.getSelection();
				if (list.size() == 1 && list.get(0) instanceof GraphConnection) {

					GraphConnection graphConnection = (GraphConnection) list
							.get(0);
					GraphConnectionData graphConnectionData = (GraphConnectionData) graphConnection
							.getData();
					EditResourceConnectionParametersDialog dialog = new EditResourceConnectionParametersDialog(
							null, graphConnectionData);

					dialog.create();
					if (dialog.open() == Window.OK) {

						graphConnection
								.setText(updateGraphConnectionText(graphConnectionData));
					}
				} else if (list.size() == 1 && list.get(0) instanceof GraphNode) {

					GraphNode graphNode = (GraphNode) list.get(0);
					GraphNodeData graphNodeData = (GraphNodeData) graphNode
							.getData();
					EditResourceDialog dialog = new EditResourceDialog(null,
							graphNodeData);

					dialog.create();
					if (dialog.open() == Window.OK) {

						graphNode.setTooltip(new org.eclipse.draw2d.Label(
								updateGraphNodeToolTip(graphNodeData), null));
					}
				}
			}
		});

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
		
		KeyListener keyListener = new KeyListener(){

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == 127){
					removeSelected();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {							
			}};
		
		parent.addKeyListener(keyListener);
		graph.addKeyListener(keyListener);
		buttonGroup.addKeyListener(keyListener);

	}

	/**
	 * Removes selected network elements
	 */
	protected void removeSelected() {
		for (Object object : graph.getSelection()) {
			if (object instanceof GraphItem) {
				GraphItem graphItem = (GraphItem) object;
				graphItem.setVisible(false);
				graphItems.remove(graphItem.getData());
				removeData(graphItem.getData());
			}
		}
	}

	/**
	 * Restores visual graph nodes connections between nodes after reading
	 * network structure from file
	 */
	protected void restoreGraphNodeConnections() {
		
		for (Object object : graph.getNodes()) {
			GraphNode graphNode = (GraphNode) object;
			if(graphNode.isVisible()){
				GraphNodeData data = (GraphNodeData) graphNode.getData();
				Set<GraphConnectionData> set = new HashSet<GraphConnectionData>();
				if (data instanceof ResourceGraphNodeData) {
					ResourceGraphNodeData resourceGraphNodeData = (ResourceGraphNodeData) data;
					if (resourceGraphNodeData.getGraphConnectionData() != null
							&& !set.contains(resourceGraphNodeData
									.getGraphConnectionData())) {

						GraphNode graphNode2 = findGraphNode(graphNode,
								resourceGraphNodeData.getGraphConnectionData());
						if(graphNode2 != null){
							set.add(resourceGraphNodeData.getGraphConnectionData());
							GraphConnection graphConnection = new GraphConnection(
									graph, SWT.None, graphNode, graphNode2);
							graphConnection.setLineWidth(2);
							graphConnection.setLineColor(parent.getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
							graphConnection.setData(resourceGraphNodeData
									.getGraphConnectionData());
							graphConnection
									.setText(updateGraphConnectionText(resourceGraphNodeData
											.getGraphConnectionData()));
						}					
					}
				}else if (data instanceof RouterGraphNodeData) {
					RouterGraphNodeData routerGraphNodeData = (RouterGraphNodeData) data;
					for(GraphConnectionData graphConnectionData : routerGraphNodeData.getListOfGraphConnections()){
						if (graphConnectionData != null && !set.contains(graphConnectionData)) {

							GraphNode graphNode2 = findGraphNode(graphNode,
									graphConnectionData);
							if(graphNode2 != null){
								set.add(graphConnectionData);
								GraphConnection graphConnection = new GraphConnection(
										graph, SWT.None, graphNode, graphNode2);
								graphConnection.setLineWidth(2);
								graphConnection.setLineColor(parent.getDisplay()
										.getSystemColor(SWT.COLOR_BLACK));
								graphConnection.setData(graphConnectionData);
								graphConnection
										.setText(updateGraphConnectionText(graphConnectionData));
								System.out.println("SDFSDFSDF");
							}					
						}
					  
					}
				}else if (data instanceof RouterGraphNodeData) {
					SwitchGraphNodeData switchGraphNodeData = (SwitchGraphNodeData) data;
					for(GraphConnectionData graphConnectionData : switchGraphNodeData.getListOfGraphConnections()){
						if (graphConnectionData != null && !set.contains(graphConnectionData)) {

							GraphNode graphNode2 = findGraphNode(graphNode,
									graphConnectionData);
							if(graphNode2 != null){
								set.add(graphConnectionData);
								GraphConnection graphConnection = new GraphConnection(
										graph, SWT.None, graphNode, graphNode2);
								graphConnection.setLineWidth(2);
								graphConnection.setLineColor(parent.getDisplay()
										.getSystemColor(SWT.COLOR_BLACK));
								graphConnection.setData(graphConnectionData);
								graphConnection
										.setText(updateGraphConnectionText(graphConnectionData));
							}					
						}
					}
				}
			}			
		}
	}

	private GraphNode findGraphNode(GraphNode graphNode2,
			GraphConnectionData graphConnectionData) {

		for (Object object : graph.getNodes()) {
			GraphNode graphNode = (GraphNode) object;
			if(graphNode.isVisible()){
				if (!graphNode.equals(graphNode2)) {
					GraphNodeData data = (GraphNodeData) graphNode.getData();
					if (data instanceof ResourceGraphNodeData) {
						ResourceGraphNodeData resourceGraphNodeData2 = (ResourceGraphNodeData) data;
						if (resourceGraphNodeData2.getGraphConnectionData() != null
								&& resourceGraphNodeData2.getGraphConnectionData()
										.equals(graphConnectionData)) {
							return graphNode;
						}
					}else if (data instanceof RouterGraphNodeData) {
						RouterGraphNodeData routerGraphNodeData = (RouterGraphNodeData) data;
						for(GraphConnectionData graphConnectionData2 : routerGraphNodeData.getListOfGraphConnections()){
							if(graphConnectionData2 != null && graphConnectionData2.equals(graphConnectionData)){
								return graphNode;
							}						
						}
					}else if (data instanceof SwitchGraphNodeData) {
						SwitchGraphNodeData switchGraphNodeData = (SwitchGraphNodeData) data;
						for(GraphConnectionData graphConnectionData2 : switchGraphNodeData.getListOfGraphConnections()){
							if(graphConnectionData2 != null && graphConnectionData2.equals(graphConnectionData)){
								return graphNode;
							}						
						}
					}
				}			
			}
		}
		return null;
	}

	/**
	 * Removes references to data from other network elements
	 * 
	 * @param data
	 *            Graph data to be removed
	 */
	protected void removeData(Object data) {

		if (data instanceof GraphConnectionData) {
			GraphConnectionData graphConnectionData = (GraphConnectionData) data;
			graphConnectionData.getLeftNode().removeEndpoint(
					graphConnectionData);
			graphConnectionData.getRightNode().removeEndpoint(
					graphConnectionData);
		} else if (data instanceof ResourceGraphNodeData) {
			ResourceGraphNodeData resourceGraphNodeData = (ResourceGraphNodeData) data;
			graphItems.remove(resourceGraphNodeData);
			if (resourceGraphNodeData.getGraphConnectionData() != null) {
				GraphConnectionData graphConnectionData = resourceGraphNodeData
						.getGraphConnectionData();
				graphConnectionData.getLeftNode().removeEndpoint(
						graphConnectionData);
				graphConnectionData.getRightNode().removeEndpoint(
						graphConnectionData);
			}
		} else if (data instanceof RouterGraphNodeData) {
			RouterGraphNodeData routerGraphNodeData = (RouterGraphNodeData) data;
			graphItems.remove(routerGraphNodeData);
			for (GraphConnectionData graphConnectionData : routerGraphNodeData
					.getListOfGraphConnections()) {
				graphConnectionData.getLeftNode().removeEndpoint(
						graphConnectionData);
				graphConnectionData.getRightNode().removeEndpoint(
						graphConnectionData);
			}
		} else if (data instanceof SwitchGraphNodeData) {
			SwitchGraphNodeData switchGraphNodeData = (SwitchGraphNodeData) data;
			graphItems.remove(switchGraphNodeData);
			for (GraphConnectionData graphConnectionData : switchGraphNodeData
					.getListOfGraphConnections()) {
				graphConnectionData.getLeftNode().removeEndpoint(
						graphConnectionData);
				graphConnectionData.getRightNode().removeEndpoint(
						graphConnectionData);
			}
		}

	}

	protected void createGraphItem(GraphNodeData g, String iconPath) {

		GraphNode graphNode = new GraphNode(graph, SWT.NONE, "");
		graphNode.setBackgroundColor(parent.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		graphNode.setImage(loadImage(iconPath));
		graphNode.setData(g);
		graphNode.setTooltip(new org.eclipse.draw2d.Label(
				updateGraphNodeToolTip(g), null));

	}

	protected String updateGraphNodeToolTip(GraphNodeData data) {

		StringBuilder sb = new StringBuilder();
		sb.append("IpAddress: ");
		if (data.getIpAddress() != null)
			sb.append(data.getIpAddress());
		else
			sb.append("");
		sb.append("\n Netmask: ");
		if (data.getNetmask() != null)
			sb.append(data.getNetmask());
		else
			sb.append("");
		sb.append("\n RepoId: ");
		if (data.getRepoId() != null)
			sb.append(data.getRepoId());
		else
			sb.append("");
		sb.append("\n ResourceId: ");
		if (data.getResourceId() != null)
			sb.append(data.getResourceId());
		else
			sb.append("");

		return sb.toString();
	}

	protected String updateGraphConnectionText(
			GraphConnectionData graphConnectionData) {
		StringBuilder sb = new StringBuilder();
		sb.append("Bandwidth: ");
		if (graphConnectionData.getBandwidth() != null)
			sb.append(graphConnectionData.getBandwidth());
		else
			sb.append("");
		sb.append("\n Priority: ");
		if (graphConnectionData.getPriority() != null)
			sb.append(graphConnectionData.getPriority());
		else
			sb.append("");

		return sb.toString();
	}

	protected Image loadImage(String fileName) {
		Bundle bundle = Activator.getDefault().getBundle();
		Path path = new Path(fileName);
		URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
		ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
		return descriptor.createImage();
	}

	public void setLayoutManager() {
		switch (layout) {
		case 1:
			graph.setLayoutAlgorithm(new TreeLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout++;
			break;
		case 2:
			graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
					LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);
			layout = 1;
			break;

		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
