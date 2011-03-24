package org.jims.modules.crossbow.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.gui.actions.ConfigurationUtil;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.gui.dialogs.EditResourceDialog;
import org.jims.modules.crossbow.gui.dialogs.InterfaceStatisticsDetailsDialog;
import org.jims.modules.crossbow.gui.dialogs.IpAddressDialog;
import org.jims.modules.crossbow.gui.dialogs.ProgressShell;
import org.jims.modules.crossbow.gui.dialogs.SelectNetworkInterfacesDialog;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.gui.statistics.StatisticAnalyzer;
import org.jims.modules.crossbow.infrastructure.supervisor.SupervisorMBean;
import org.jims.modules.crossbow.infrastructure.worker.exception.ModelInstantiationException;
import org.jims.modules.crossbow.objectmodel.Actions;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Endpoint;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

/**
 * Main view in our GUI allows creating network structure with certain QoS
 * 
 * @author robert
 * 
 */
public class Gui extends Shell {

	public static final int REFRESH_TIME = 10000;// 10seconds

	private Graph graph;
	private int layout = 1;

	private Group buttonGroup;
	private Button addRouterButton, addSwitchButton, addResourceButton;
	private Button removeSelectedButton, connectButton;
	private Button validateButton, deployButton;

	// watek odpowiedzialny za obliczanie statystyk interfejsow
	private StatisticAnalyzer statisticAnalyzer;

	private GraphConnectionLabelUpdater connectionLabelUpdater;

	private List<GraphConnectionData> graphConnectionDataList = new LinkedList<GraphConnectionData>();

	private Text projectId;
	private Text supervisorsAddress;
	private Text supervisorPort;

	private boolean updateGraphConnection = false;
	private List<Object> modelObjects = new LinkedList<Object>();

	private JmxConnector jmxConnector;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Gui shell = new Gui(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				shell.updateGraphConnections();
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			shell.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public Gui(Display display) {
		super(display, SWT.SHELL_TRIM);

		Menu menu = new Menu(this, SWT.BAR);
		setMenuBar(menu);

		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");

		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);

		MenuItem mntmSave = new MenuItem(menu_1, SWT.NONE);
		mntmSave.setText("Save");
		mntmSave.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// File standard dialog
				FileDialog fileDialog = new FileDialog(Gui.this, SWT.SAVE);
				fileDialog
						.setText("Type File Name Where To Save With Network Structure");
				fileDialog.setFilterExtensions(new String[] { "*.cro" });
				fileDialog.setFilterNames(new String[] { "Textfiles(*.cro)" });
				String selected = fileDialog.open();
				if (selected != null) {
					ConfigurationUtil.saveNetwork(selected, modelObjects);
				}
			}
		});

		MenuItem mntmLoad = new MenuItem(menu_1, SWT.NONE);
		mntmLoad.setText("Load");
		mntmLoad.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				// File standard dialog
				FileDialog fileDialog = new FileDialog(Gui.this);
				fileDialog.setText("Select File With Network Structure");
				fileDialog.setFilterExtensions(new String[] { "*.cro" });
				fileDialog.setFilterNames(new String[] { "Textfiles(*.cro)" });
				String selected = fileDialog.open();

				if (selected != null) {

					hideItems();

					projectId.setText("");

					graphConnectionDataList.clear();
					modelObjects = ConfigurationUtil.loadNetwork(selected);
					for (Object obj : modelObjects) {
						String iconFileName = null;
						if ((obj instanceof Appliance)
								&& ((Appliance) (obj)).getType().equals(
										ApplianceType.MACHINE)) {
							iconFileName = "icons/resource.jpg";
						} else if (obj instanceof Switch) {
							iconFileName = "icons/switch.jpg";
						} else if ((obj instanceof Appliance)
								&& ((Appliance) (obj)).getType().equals(
										ApplianceType.ROUTER)) {
							iconFileName = "icons/router.jpg";
						}

						createGraphItem(obj, iconFileName);
					}

					restoreGraphNodeConnections();

					graph.applyLayout();
				}
			}
		});

		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.setText("Exit");
		mntmExit.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(null, "Exit?", "Are you sure")) {
					System.exit(0);
				}
			}
		});

		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");

		Menu menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);

		MenuItem mntmClear = new MenuItem(menu_2, SWT.NONE);
		mntmClear.setText("Clear");
		mntmClear.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAllGraphItems();
			}
		});

		MenuItem mntmGraph = new MenuItem(menu, SWT.CASCADE);
		mntmGraph.setText("Graph");

		Menu menu_3 = new Menu(mntmGraph);
		mntmGraph.setMenu(menu_3);

		MenuItem mntmOrganizeGraph = new MenuItem(menu_3, SWT.NONE);
		mntmOrganizeGraph.setText("Organize graph");
		mntmOrganizeGraph.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				graph.applyLayout();
			}
		});

		MenuItem mntmValidateNetwork = new MenuItem(menu_3, SWT.NONE);
		mntmValidateNetwork.setText("Validate network");
		mntmValidateNetwork.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				validateNetwork();
			}
		});

		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");

		Menu menu_4 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_4);

		MenuItem mntmAboutCrossbowDeployer = new MenuItem(menu_4, SWT.NONE);
		mntmAboutCrossbowDeployer.setText("About crossbow deployer");
		mntmAboutCrossbowDeployer.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateGraphConnections();
			}
		});

		createContents();
	}

	public void stop() {

		if (statisticAnalyzer != null) {
			statisticAnalyzer.stopGatheringStatistics();
		}

		if (connectionLabelUpdater != null) {
			connectionLabelUpdater.interrupt();
		}
	}

	public void updateGraphConnections() {
		if (!updateGraphConnection) {
			return;
		}
		updateGraphConnection = false;
		for (Object obj : graph.getConnections()) {
			if (obj instanceof GraphConnection) {
				GraphConnection graphConnection = (GraphConnection) obj;
				if (((GraphConnectionData) graphConnection.getData())
						.toString() != null) {
					graphConnection
							.setText(((GraphConnectionData) graphConnection
									.getData()).toString());
				}
			}
		}

		if (jmxConnector != null) {
			try {
				this.setText("Connected to "
						+ jmxConnector.getUrl() + "\n"
						+ jmxConnector.getMBeanServerConnectionDetails());
			} catch (IOException e) {
				e.printStackTrace();
				resetConnectionDetailsLabel();
			}
		}
	}

	private void resetConnectionDetailsLabel() {
		this.setText("Not connected");
	}

	/**
	 * Validates network structure and content
	 */
	protected void validateNetwork() {
		// @todo validate network structure

		if (!IpAddressDialog.isIpv4(supervisorsAddress.getText())) {
			MessageDialog.openError(null, "Validation result",
					"Please type endpoint address in ipv4 format");
			return;
		}

		try {
			int i = Integer.parseInt(supervisorPort.getText());
			if (i <= 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Validation result",
					"Port number must be positive number");
			return;
		}

		MessageDialog.openInformation(null, "Validation result",
				"Network structure has been successfully validated");

		deployButton.setEnabled(true);
		deployButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {

				for (Object obj : modelObjects) {
					if (obj instanceof Switch) {
						Switch swit = (Switch) obj;
						swit.setProjectId(projectId.getText());
					} else if (obj instanceof Appliance) {
						Appliance app = (Appliance) obj;
						app.setProjectId(projectId.getText());
						for (Interface interf : app.getInterfaces()) {
							interf.setProjectId(projectId.getText());
						}
					}
				}

				jmxConnector = new JmxConnector(supervisorsAddress.getText(),
						Integer.parseInt(supervisorPort.getText()));

				MBeanServerConnection mbsc = null;
				try {
					mbsc = jmxConnector.getMBeanServerConnection();
				} catch (Exception e) {

					MessageDialog.openError(null, "Connection problem",
							"Couldn't connect to specified MBean Server");

					e.printStackTrace();
					return;
				}

				supervisorsAddress.setEnabled(false);
				supervisorPort.setEnabled(false);

				projectId.setEnabled(false);

				SupervisorMBean supervisor = null;

				try {
					supervisor = JMX.newMBeanProxy(mbsc, new ObjectName(
							"Crossbow:type=Supervisor"), SupervisorMBean.class);
				} catch (Exception e) {
					MessageDialog.openError(null, "Connection problem",
							"Couldn't get Supervisor.class");

					e.printStackTrace();
					return;
				}

				ObjectModel objectModel = new ObjectModel();

				registerObjects(objectModel, modelObjects);

				try {
					Actions actions = new Actions();

					for (Appliance app : objectModel.getAppliances()) {
						actions.insert(app, Actions.ACTION.ADD);
					}
					for (Interface interf : objectModel.getPorts()) {
						actions.insert(interf, Actions.ACTION.ADD);
					}
					for (Switch swit : objectModel.getSwitches()) {
						actions.insert(swit, Actions.ACTION.ADD);
					}
					for (Policy policy : objectModel.getPolicies()) {
						actions.insert(policy, Actions.ACTION.ADD);
					}

					supervisor.instantiate(objectModel, actions);

				} catch (ModelInstantiationException e) {
					e.printStackTrace();

					MessageDialog.openError(null,
							"Problem with sending network structure",
							"Network structure couldn't be sent");

					e.printStackTrace();
					return;
				}

				Display display = Display.getDefault();
				Shell dlgShell = new ProgressShell(Gui.this.getDisplay());
				dlgShell.setSize(300, 250);
				dlgShell.setLocation(200, 200);
				dlgShell.open();
				dlgShell.layout();
				while (!dlgShell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}

				statisticAnalyzer = new StatisticAnalyzer(
						graphConnectionDataList, jmxConnector);
				statisticAnalyzer.startGatheringStatistics();
			}

		});
		graph.applyLayout();
	}

	protected void registerObjects(ObjectModel objectModel, List<Object> objects) {

		for (Object obj : objects) {
			if (obj instanceof Switch) {
				Switch swit = (Switch) obj;
				objectModel.register(swit);
			} else if (obj instanceof Appliance) {
				Appliance app = (Appliance) obj;
				objectModel.register(app);
				
				int ifaceNo = 0;
				for (Interface interf : app.getInterfaces()) {
					interf.setResourceId( "IFACE" + String.valueOf( ifaceNo ) );
					objectModel.register(interf);
					for (Policy policy : interf.getPoliciesList()) {
						objectModel.register(policy);
					}
					
					++ifaceNo;
				}
			}
		}

	}

	/**
	 * Clears graph component and object model behind it
	 */
	protected void clearAllGraphItems() {

		modelObjects.clear();
		graphConnectionDataList.clear();
		// items were removed so i hide them
		hideItems();

	}

	private void hideItems() {
		for (Object object : graph.getConnections())
			((GraphConnection) object).setVisible(false);
		for (Object object : graph.getNodes())
			((GraphItem) object).setVisible(false);
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Crossbow deployer");
		setSize(600, 500);

		createPartControl();

	}

	@Override
	protected void checkSubclass() {
	}

	public void createPartControl() {

		this.setLayout(new FormLayout());

		buttonGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		FormData fd_buttonGroup = new FormData();
		fd_buttonGroup.right = new FormAttachment(0, 184);
		fd_buttonGroup.bottom = new FormAttachment(100, -10);
		fd_buttonGroup.top = new FormAttachment(0, 5);
		fd_buttonGroup.left = new FormAttachment(0, 5);
		buttonGroup.setLayoutData(fd_buttonGroup);
		buttonGroup.setText("Create requested resources");
		buttonGroup.setLayout(new FormLayout());

		projectId = new Text(buttonGroup, SWT.NONE);
		FormData fd_projectId = new FormData();
		fd_projectId.top = new FormAttachment(0, 7);
		fd_projectId.left = new FormAttachment(0, 67);
		projectId.setLayoutData(fd_projectId);
		projectId.setText("");
		projectId.pack();

		Label label = new Label(buttonGroup, SWT.NONE);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(0, 7);
		fd_label.left = new FormAttachment(0, 7);
		label.setLayoutData(fd_label);
		label.setText("Project ID:");
		label.pack();

		// dodaje router do modelu sieci
		addRouterButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_addRouterButton = new FormData();
		fd_addRouterButton.bottom = new FormAttachment(0, 97);
		fd_addRouterButton.right = new FormAttachment(0, 87);
		fd_addRouterButton.top = new FormAttachment(0, 47);
		fd_addRouterButton.left = new FormAttachment(0, 27);
		addRouterButton.setLayoutData(fd_addRouterButton);
		addRouterButton.setToolTipText("Add router");
		addRouterButton.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addRouterButton.setForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addRouterButton.setImage(loadImage("icons/router.jpg"));

		addRouterButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Appliance appliance = new Appliance("", "",
						ApplianceType.ROUTER);
				createGraphItem(appliance, "icons/router.jpg");
				modelObjects.add(appliance);
			}
		});

		// dodaje switch do modelu sieci
		addSwitchButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_addSwitchButton = new FormData();
		fd_addSwitchButton.bottom = new FormAttachment(0, 97);
		fd_addSwitchButton.right = new FormAttachment(0, 147);
		fd_addSwitchButton.top = new FormAttachment(0, 47);
		fd_addSwitchButton.left = new FormAttachment(0, 87);
		addSwitchButton.setLayoutData(fd_addSwitchButton);
		addSwitchButton.setToolTipText("Add switch");
		addSwitchButton.setImage(loadImage("icons/switch.jpg"));
		addSwitchButton.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addSwitchButton.setForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addSwitchButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Switch swit = new Switch("", "");
				createGraphItem(swit, "icons/switch.jpg");
				modelObjects.add(swit);
			}

		});

		// dodaje nowy zasob do modelu sieci
		addResourceButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_addResourceButton = new FormData();
		fd_addResourceButton.bottom = new FormAttachment(0, 157);
		fd_addResourceButton.right = new FormAttachment(0, 87);
		fd_addResourceButton.top = new FormAttachment(0, 97);
		fd_addResourceButton.left = new FormAttachment(0, 27);
		addResourceButton.setLayoutData(fd_addResourceButton);
		addResourceButton.setToolTipText("Add resource");
		addResourceButton.setImage(loadImage("icons/resource.jpg"));
		addResourceButton.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addResourceButton.setForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		addResourceButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Appliance appliance = new Appliance("", "",
						ApplianceType.MACHINE);
				createGraphItem(appliance, "icons/resource.jpg");
				modelObjects.add(appliance);
			}
		});

		// usuwa zaznaczone elementy z modelu sieci
		removeSelectedButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_removeSelectedButton = new FormData();
		fd_removeSelectedButton.bottom = new FormAttachment(0, 157);
		fd_removeSelectedButton.right = new FormAttachment(0, 147);
		fd_removeSelectedButton.top = new FormAttachment(0, 97);
		fd_removeSelectedButton.left = new FormAttachment(0, 87);
		removeSelectedButton.setLayoutData(fd_removeSelectedButton);
		removeSelectedButton.setToolTipText("Remove selected");
		removeSelectedButton.setImage(loadImage("icons/delete.jpg"));
		removeSelectedButton.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		removeSelectedButton.setForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		removeSelectedButton.setEnabled(false);
		removeSelectedButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				removeSelected();
			}

		});

		// otwiera zaznaczony element modelu sieci do edycji
		connectButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_connectButton = new FormData();
		fd_connectButton.bottom = new FormAttachment(0, 217);
		fd_connectButton.right = new FormAttachment(0, 87);
		fd_connectButton.top = new FormAttachment(0, 157);
		fd_connectButton.left = new FormAttachment(0, 27);
		connectButton.setLayoutData(fd_connectButton);
		connectButton.setToolTipText("Connect resources");
		connectButton.setImage(loadImage("icons/connect.jpg"));
		connectButton.setBackground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		connectButton.setForeground(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		connectButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {

				List list = graph.getSelection();

				SelectNetworkInterfacesDialog d = new SelectNetworkInterfacesDialog(
						null, ((GraphNode) (list.get(0))).getData(),
						((GraphNode) (list.get(1))).getData());
				d.create();
				if (d.open() == Window.OK) {

					createGraphConnectionData((GraphNode) (list.get(0)),
							(GraphNode) (list.get(1)), d.getRightEndpoint(), d
									.getLeftEndpoint());

					if (d.getLeftEndpoint() != null) {
						if (d.getRightEndpoint() != null) {
							d.getLeftEndpoint().setEndpoint(
									d.getRightEndpoint());
							d.getRightEndpoint().setEndpoint(
									d.getLeftEndpoint());
						} else if (((GraphNode) (list.get(1))).getData() instanceof Switch) {
							d.getLeftEndpoint().setEndpoint(
									(Switch) ((GraphNode) (list.get(1)))
											.getData());
							((Switch) ((GraphNode) (list.get(1))).getData())
									.getEndpoints().add(d.getLeftEndpoint());
						}

					} else if (((GraphNode) (list.get(0))).getData() instanceof Switch) {

						if (d.getRightEndpoint() != null) {
							((Switch) ((GraphNode) (list.get(0))).getData())
									.getEndpoints().add(d.getRightEndpoint());
							d.getRightEndpoint().setEndpoint(
									((Switch) ((GraphNode) (list.get(0)))
											.getData()));
						} else if (((GraphNode) (list.get(1))).getData() instanceof Switch) {
							((Switch) ((GraphNode) (list.get(0))).getData())
									.getEndpoints()
									.add(
											(Switch) ((GraphNode) (list.get(1)))
													.getData());
							((Switch) ((GraphNode) (list.get(1))).getData())
									.getEndpoints()
									.add(
											(Switch) ((GraphNode) (list.get(0)))
													.getData());
						}
					}
				}
			}

		});
		connectButton.setEnabled(false);

		// przesyla stworzona strukture sieci do mappera
		validateButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_validateButton = new FormData();
		fd_validateButton.bottom = new FormAttachment(0, 217);
		fd_validateButton.right = new FormAttachment(0, 147);
		fd_validateButton.top = new FormAttachment(0, 157);
		fd_validateButton.left = new FormAttachment(0, 87);
		validateButton.setLayoutData(fd_validateButton);
		validateButton.setImage(loadImage("icons/verify.jpg"));
		validateButton.setToolTipText("Validate network correctness");
		validateButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				validateNetwork();
			}

		});

		// przesyla stworzona strukture sieci do mappera
		deployButton = new Button(buttonGroup, SWT.PUSH);
		FormData fd_deployButton = new FormData();
		fd_deployButton.bottom = new FormAttachment(0, 277);
		fd_deployButton.right = new FormAttachment(0, 87);
		fd_deployButton.top = new FormAttachment(0, 217);
		fd_deployButton.left = new FormAttachment(0, 27);
		deployButton.setLayoutData(fd_deployButton);
		deployButton.setImage(loadImage("icons/deployIt.jpg"));
		deployButton.setToolTipText("Deploy network");
		deployButton.setEnabled(false);
		deployButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// @todo send network structure to mapper
			}

		});

		buttonGroup.pack();

		graph = new Graph(this, SWT.NONE);
		graph.setLayout(new FormLayout());
		FormData fd_graph = new FormData();
		fd_graph.left = new FormAttachment(buttonGroup, 6);
		fd_graph.bottom = new FormAttachment(0, 459);
		fd_graph.right = new FormAttachment(0, 584);
		fd_graph.top = new FormAttachment(0, 10);
		graph.setLayoutData(fd_graph);

		this.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent arg0) {
			}

			@Override
			public void controlResized(ControlEvent arg0) {
				FormData fd_graph = (FormData) graph.getLayoutData();
				fd_graph.left = new FormAttachment(buttonGroup, 6);
				fd_graph.bottom = new FormAttachment(0,
						Gui.this.getSize().y - 65);
				fd_graph.right = new FormAttachment(0,
						Gui.this.getSize().x - 16);
				fd_graph.top = new FormAttachment(0, 10);
			}

		});

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
				if (list.size() == 1 && list.get(0) instanceof GraphNode) {

					GraphNode graphNode = (GraphNode) list.get(0);

					EditResourceDialog dialog = new EditResourceDialog(null,
							graphNode.getData());

					dialog.create();
					if (dialog.open() == Window.OK) {

						String toolTip = updateGraphNodeToolTip(graphNode
								.getData());
						graphNode.setTooltip(new org.eclipse.draw2d.Label(
								toolTip, null));
						graphNode.setText(toolTip);
					}
				} else if (list.size() == 1
						&& list.get(0) instanceof GraphConnection) {

					System.out.println("Graph connection details");

					InterfaceStatisticsDetailsDialog dlg = new InterfaceStatisticsDetailsDialog(
							null,
							(GraphConnectionData) (((GraphConnection) list
									.get(0)).getData()));
					dlg.create();
					if (dlg.open() == Window.OK) {
					}

				}
			}
		});

		graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(
				LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == 127) {
					removeSelected();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		};

		Label label2 = new Label(buttonGroup, SWT.NONE);
		FormData fd_label2 = new FormData();
		fd_label2.top = new FormAttachment(5, 267);
		fd_label2.left = new FormAttachment(0, 7);
		label2.setLayoutData(fd_label2);
		label2.setText("Deployment address:  ");
		label2.pack();

		supervisorsAddress = new Text(buttonGroup, SWT.NONE);
		FormData fd_projectId1 = new FormData();
		fd_projectId1.top = new FormAttachment(5, 287);
		fd_projectId1.left = new FormAttachment(15, 55);
		supervisorsAddress.setLayoutData(fd_projectId1);
		supervisorsAddress.setText("");
		supervisorsAddress.pack();

		Label label3 = new Label(buttonGroup, SWT.NONE);
		FormData fd_label3 = new FormData();
		fd_label3.top = new FormAttachment(5, 307);
		fd_label3.left = new FormAttachment(0, 7);
		label3.setLayoutData(fd_label3);
		label3.setText("Deployment port:  ");
		label3.pack();

		supervisorPort = new Text(buttonGroup, SWT.NONE);
		FormData fd_projectId2 = new FormData();
		fd_projectId2.top = new FormAttachment(5, 327);
		fd_projectId2.left = new FormAttachment(15, 55);
		supervisorPort.setLayoutData(fd_projectId2);
		supervisorPort.setText("");
		supervisorPort.pack();
		
		resetConnectionDetailsLabel();

		this.addKeyListener(keyListener);
		graph.addKeyListener(keyListener);
		buttonGroup.addKeyListener(keyListener);

		connectionLabelUpdater = new GraphConnectionLabelUpdater();
		connectionLabelUpdater.start();

	}

	private void createGraphConnectionData(GraphNode graphNode,
			GraphNode graphNode2, Endpoint endp1, Endpoint endp2) {

		System.err.println(graphNode + " restore " + graphNode2);
		if (graphNode == null || graphNode2 == null) {
			return;
		}
		GraphConnectionData graphConnectionData = new GraphConnectionData(
				graphNode.getData(), graphNode2.getData(), endp1, endp2);

		System.err.println(endp1 + " restore " + endp2);

		graphConnectionDataList.add(graphConnectionData);

		GraphConnection graphConnection = new GraphConnection(graph, SWT.NONE,
				graphNode, graphNode2);
		graphConnection.setData(graphConnectionData);
		graphConnection.setLineWidth(2);
		graphConnection.setLineColor(Gui.this.getDisplay().getSystemColor(
				SWT.COLOR_BLACK));

		updateGraphConnection = true;

	}

	/**
	 * Removes selected network elements
	 */
	protected void removeSelected() {

		for (Object object : graph.getSelection()) {
			if (object instanceof GraphItem) {
				GraphItem graphItem = (GraphItem) object;
				graphItem.setVisible(false);
				modelObjects.remove(graphItem.getData());
				graphConnectionDataList.remove(object);
				removeData(graphItem.getData());
			}
		}
		removeSelectedButton.setEnabled(false);
	}

	/**
	 * Restores visual graph nodes connections between nodes after reading
	 * network structure from file
	 */
	/**
	 * 
	 */
	protected void restoreGraphNodeConnections() {

		List<Endpoint> restoredEndpoints = new LinkedList<Endpoint>();

		for (Object node : graph.getNodes()) {
			GraphNode graphNode = (GraphNode) node;

			if (graphNode.isVisible()) {
				Object object = graphNode.getData();

				if (object instanceof Switch) {
					Switch swit = (Switch) object;

					List<Endpoint> endpoints = swit.getEndpoints();
					System.err.println(endpoints.size());
					for (Endpoint endpoint : endpoints) {
						GraphNode secondNode = findSecondEndpointObject(endpoint);
						System.err.println(secondNode);

						if (!restoredEndpoints.contains(endpoint)
								&& !restoredEndpoints.contains(swit)) {
							createGraphConnectionData(graphNode, secondNode,
									endpoint, null);
						}
					}

					if (!restoredEndpoints.contains(swit)) {
						restoredEndpoints.add(swit);
					}
				} else if (object instanceof Appliance) {

					Appliance appliance = (Appliance) object;
					for (Interface interfac : appliance.getInterfaces()) {

						if (interfac.getEndpoint() == null) {
							continue;
						}

						GraphNode secondNode = findSecondEndpointObject(interfac
								.getEndpoint());

						if (!restoredEndpoints.contains(interfac.getEndpoint())
								&& !restoredEndpoints.contains(interfac)) {
							createGraphConnectionData(graphNode, secondNode,
									interfac, interfac.getEndpoint());
						}

						if (!restoredEndpoints.contains(interfac)) {
							restoredEndpoints.add(interfac);
						}
					}
				}

			}
		}
	}

	private GraphNode findSecondEndpointObject(Endpoint endpoint) {

		for (Object graphNode : graph.getNodes()) {
			if (((GraphNode) graphNode).isVisible()) {
				Object object = ((GraphNode) graphNode).getData();
				if (object instanceof Switch) {
					Switch swit = (Switch) object;
					System.err.println(swit.getEndpoints().size());
					if (swit.getUUID().equals(endpoint.getUUID())) {
						return (GraphNode) graphNode;
					}
				} else if (object instanceof Appliance) {
					Appliance appliance = (Appliance) object;
					for (Interface interfac : appliance.getInterfaces()) {
						if (interfac.getUUID().equals(endpoint.getUUID())) {
							return (GraphNode) graphNode;
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
			removeGraphNodeConnection((GraphConnectionData) data);

		} else {
			removeElement(data);
		}
	}

	private void removeElement(Object data) {

		if (data instanceof Appliance) {
			Appliance appliance = (Appliance) data;
			modelObjects.remove(appliance);
			for (Interface interfac : appliance.getInterfaces()) {
				removeReferenceToEndpoint(interfac.getEndpoint(), interfac);
			}
			appliance.setInterfaces(new LinkedList<Interface>());

		} else if (data instanceof Switch) {
			Switch switc = (Switch) data;
			for (Endpoint endpoint : switc.getEndpoints()) {
				removeReferenceToEndpoint(endpoint, switc);
			}
			switc.setEndpoints(new LinkedList<Endpoint>());
		}
	}

	/**
	 * Removes from endpoint connection with endpoint2
	 * 
	 * @param endpoint
	 * @param endpoint2
	 */
	private void removeReferenceToEndpoint(Endpoint endpoint, Endpoint endpoint2) {

		if (endpoint != null) {
			if (endpoint instanceof Switch) {
				Switch switc = (Switch) endpoint;
				switc.getEndpoints().remove(endpoint2);
			} else if (endpoint instanceof Interface) {
				Interface interfac = (Interface) endpoint;
				interfac.setEndpoint(null);
			}
		}
	}

	private void removeGraphNodeConnection(
			GraphConnectionData graphConnectionData) {

		removeElement(graphConnectionData.getLeftNode());
		removeElement(graphConnectionData.getLeftNode());
	}

	protected void createGraphItem(Object g, String iconPath) {

		GraphNode graphNode = new GraphNode(graph, SWT.NONE, "");
		graphNode.setBackgroundColor(this.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));
		graphNode.setImage(loadImage(iconPath));
		graphNode.setData(g);
		String toolTipText = updateGraphNodeToolTip(g);
		graphNode.setTooltip(new org.eclipse.draw2d.Label(toolTipText));

		if (g instanceof Appliance) {
			Appliance appliance = (Appliance) g;
			if (projectId.getText() == null || projectId.getText().equals("")) {
				projectId.setText(appliance.getProjectId());
			}
		}

		graphNode.setText(toolTipText);

	}

	protected String updateGraphNodeToolTip(Object obj) {

		StringBuilder sb = new StringBuilder();
		if (obj instanceof Appliance) {

			Appliance appliance = (Appliance) obj;
			if (appliance.getType() != null
					&& appliance.getType().equals(ApplianceType.MACHINE)) {
				sb.append("RepoId: ");
				if (appliance.getRepoId() != null)
					sb.append(appliance.getRepoId());
			}
			sb.append("\nResourceId: ");
			if (appliance.getResourceId() != null)
				sb.append(appliance.getResourceId());
		} else if (obj instanceof Switch) {

			Switch swit = (Switch) obj;
			sb.append("\nResourceId: ");
			if (swit.getResourceId() != null)
				sb.append(swit.getResourceId());
		}
		sb.append("\n");
		if (obj instanceof Appliance) {
			Appliance appliance = (Appliance) obj;
			for (Interface inter : appliance.getInterfaces()) {
				if (inter.getIpAddress() != null) {
					sb.append(inter.getIpAddress().toString() + "\n");
				}
			}
		}

		return sb.toString();
	}

	protected Image loadImage(String fileName) {

		ImageDescriptor descriptor = ImageDescriptor.createFromFile(null,
				fileName);
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

	public class GraphConnectionLabelUpdater extends Thread {

		@Override
		public void run() {

			try {

				while (true) {

					for (GraphConnectionData graphConnectionData : graphConnectionDataList) {

						StringBuilder sb = new StringBuilder();
						if (graphConnectionData.getEndp1() != null
								&& graphConnectionData.getEndp1() instanceof Interface) {
							sb.append(((Interface) graphConnectionData
									.getEndp1()).getIpAddress());
						}
						if (graphConnectionData.getStatistic1() != null) {
							sb.append(" received: ");
							sb.append(8.0 * graphConnectionData.getStatistic1()
									.getAverageStatistics().get(
											LinkStatistics.RBYTES) / 1024.0);
							sb.append(" sent: ");
							sb.append(8.0 * graphConnectionData.getStatistic1()
									.getAverageStatistics().get(
											LinkStatistics.OBYTES) / 1024.0);
						}

						if (!sb.toString().equals("")) {
							sb.append("  ;  ");
						}

						if (graphConnectionData.getEndp2() != null
								&& graphConnectionData.getEndp2() instanceof Interface) {
							sb.append(((Interface) graphConnectionData
									.getEndp2()).getIpAddress());
						}
						if (graphConnectionData.getStatistic2() != null) {
							sb.append(" received: ");
							sb.append(8.0 * graphConnectionData.getStatistic2()
									.getAverageStatistics().get(
											LinkStatistics.RBYTES) / 1024.0);
							sb.append("kbps sent: ");
							sb.append(8.0 * graphConnectionData.getStatistic2()
									.getAverageStatistics().get(
											LinkStatistics.OBYTES) / 1024.0);
							sb.append("kbps");
						}

						graphConnectionData.setToolTip(sb.toString());

					}

					updateGraphConnection = true;

					Thread.sleep(REFRESH_TIME);

				}

			} catch (InterruptedException e) {
			}

		}

	}
}