package org.jims.modules.crossbow.gui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
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
import org.jims.modules.crossbow.gui.actions.ComponentProxyFactory;
import org.jims.modules.crossbow.gui.actions.ConfigurationUtil;
import org.jims.modules.crossbow.gui.actions.DiscoveryHandler;
import org.jims.modules.crossbow.gui.actions.ModelToGraphTranslator;
import org.jims.modules.crossbow.gui.actions.RepoManagerProxyFactory;
import org.jims.modules.crossbow.gui.actions.SupervisorProxyFactory;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.gui.dialogs.EditResourceDialog;
import org.jims.modules.crossbow.gui.dialogs.InterfaceStatisticsDetailsDialog;
import org.jims.modules.crossbow.gui.dialogs.IpAddressDialog;
import org.jims.modules.crossbow.gui.dialogs.ProgressShell;
import org.jims.modules.crossbow.gui.dialogs.SelectNetworkInterfacesDialog;
import org.jims.modules.crossbow.gui.jmx.JmxConnector;
import org.jims.modules.crossbow.gui.statistics.StatisticAnalyzer;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;
import org.jims.modules.crossbow.infrastructure.appliance.RepoManagerMBean;
import org.jims.modules.crossbow.infrastructure.progress.CrossbowNotificationMBean;
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

	private static final Logger logger = Logger.getLogger(Gui.class);

	private DataBindingContext m_bindingContext;

	public static final int REFRESH_TIME = 10000;// 10seconds

	private static ComponentProxyFactory componentProxyFactory;

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

	private ProgressShell progressShell;

	private boolean updateGraphConnection = false;
	private List<Object> modelObjects = new LinkedList<Object>();

	private JmxConnector jmxConnector;
	private DiscoveryHandler discoveryHandler;
	private ConnectionTester connectionTester;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Display display = Display.getDefault();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				try {
					Display display = Display.getDefault();

					componentProxyFactory = new ComponentProxyFactory();

					ModelToGraphTranslator translator = new ModelToGraphTranslator();

					Gui shell = new Gui(display, new DiscoveryHandler(
							new SupervisorProxyFactory() {

								@Override
								public SupervisorMBean createSupervisor() {
									return componentProxyFactory
											.createSupervisor();
								}

							}, translator));

					// Set the graph's style.

					translator.setColor(
							ModelToGraphTranslator.Element.GRAPH_EDGE, shell
									.getDisplay().getSystemColor(
											SWT.COLOR_BLACK));

					translator.setColor(
							ModelToGraphTranslator.Element.GRAPH_NODE, shell
									.getDisplay().getSystemColor(
											SWT.COLOR_WHITE));

					shell.setMaximized(true);

					shell.open();
					shell.layout();
					while (!shell.isDisposed()) {
						shell.updateGraphConnections();
						if (!display.readAndDispatch()) {
							display.sleep();
						}
						Thread.sleep( 5 );
					}

					shell.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public Gui(Display display, DiscoveryHandler discoveryHandler) {
		super(display, SWT.SHELL_TRIM);

		this.discoveryHandler = discoveryHandler;

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
					logger.trace("Saving network structure");
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

					logger.trace("Clearing existing items");
					graphConnectionDataList.clear();
					modelObjects = ConfigurationUtil.loadNetwork(selected);
					logger.trace("Restoring network structure from file");
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
					logger.info("Closing gui");
					Gui.this.close();
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
				// @todo dorobic wyswietlanie okienka z informacja o systemie i
				// autorach
			}
		});

		createContents();
	}

	public void stop() {

		logger.info("Stopping all running threads");

		if (connectionTester != null) {
			connectionTester.stopThread();
		}

		if (statisticAnalyzer != null) {
			logger.info("Stopping threads responsible for statistics");
			statisticAnalyzer.stopGatheringStatistics();
		}

		if (connectionLabelUpdater != null) {
			logger
					.info("Interrupting thread responsible for updating network statistics");
			connectionLabelUpdater.interrupt();
		}
	}

	public void updateGraphConnections() {

		// logger.trace("Updating graph connections details");

		if (updateGraphConnection) {
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
		}

		if (connectionTester.getConnected()) {
			this.setText("Connected");
		} else {
			this.setText("Not connected");
		}

		if (progressShell != null) {
			if (!progressShell.isRunning()) {
				logger.info("Stopping ProgressShell thread");
				progressShell = null;
			} else {
				progressShell.update();
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

				logger.trace("Starting deploying network");

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

					logger.error("Couldn't get MBeanServerConnection");
					MessageDialog.openError(null, "Connection problem",
							"Couldn't connect to specified MBean Server");

					e.printStackTrace();
					return;
				}

				try {
					final SupervisorMBean supervisor = JMX.newMBeanProxy(mbsc,
							new ObjectName("Crossbow:type=Supervisor"),
							SupervisorMBean.class);

					final ObjectModel objectModel = new ObjectModel();

					registerObjects(objectModel, modelObjects);

					CrossbowNotificationMBean crossbowNotificationMBean = JMX
							.newMBeanProxy(mbsc, new ObjectName(
									"Crossbow:type=CrossbowNotification"),
									CrossbowNotificationMBean.class);

					crossbowNotificationMBean.reset();
					logger.trace("Reseting progress state before deployment");

					progressShell = new ProgressShell(Gui.this, jmxConnector);
					progressShell.create();
					if (progressShell.open() == Window.OK) {
					}

					new Thread() {

						public void run() {
							try {
								Actions actions = new Actions();

								for (Appliance app : objectModel
										.getAppliances()) {
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

								logger.info("Starting deployment");
								supervisor.instantiate(objectModel, actions);

							} catch (ModelInstantiationException e) {
								e.printStackTrace();
							}
						}
					}.start();

				} catch (Exception e) {
					MessageDialog.openError(null, "Connection problem",
							"Couldn't get Supervisor.class");

					e.printStackTrace();
					return;
				}

				logger.info("Starting new threads gathering statistics");
				statisticAnalyzer = new StatisticAnalyzer(
						graphConnectionDataList, jmxConnector);
				statisticAnalyzer.startGatheringStatistics();
			}

		});
		// graph.applyLayout();
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
					interf.setResourceId("IFACE" + ifaceNo);
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

		logger.trace("Hiding all items");

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

				logger.info("Adding new Router");

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

				logger.info("Adding new Switch");

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

				logger.info("Adding new appliance");

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

				logger.info("Connecting two links");

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
							graphNode.getData(), new RepoManagerProxyFactory() {

								@Override
								public RepoManagerMBean getRepoManager() {

									RepoManagerMBean repoManager = componentProxyFactory
											.createRepoManager();

									if (null == repoManager) {
										repoManager = new RepoManagerMBean() {

											@Override
											public List<String> getIds() {
												return new LinkedList<String>();
											}

											@Override
											public String getRepoPath() {
												return null;
											}

											@Override
											public void setRepoPath(String arg0) {
											}

										};

									}

									return repoManager;
								}

							});

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

		Button discoverBtn = new Button(buttonGroup, SWT.PUSH);
		FormData fd_discoverBtn = new FormData();

		fd_discoverBtn.bottom = new FormAttachment(0, 277);
		fd_discoverBtn.right = new FormAttachment(100, -28);
		fd_discoverBtn.top = new FormAttachment(deployButton, 0, SWT.TOP);
		fd_discoverBtn.left = new FormAttachment(addSwitchButton, 0, SWT.LEFT);
		discoverBtn.setLayoutData(fd_discoverBtn);
		discoverBtn.setToolTipText("Discover");
		discoverBtn.setImage(loadImage("icons/discover.jpg"));

		discoverBtn.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event event) {
				discoveryHandler.handle(graph);
			}

		});

		resetConnectionDetailsLabel();
		connectionTester = new ConnectionTester(this);

		this.addKeyListener(keyListener);
		graph.addKeyListener(keyListener);
		buttonGroup.addKeyListener(keyListener);

		connectionLabelUpdater = new GraphConnectionLabelUpdater();
		connectionLabelUpdater.start();
		m_bindingContext = initDataBindings();

	}

	private void createGraphConnectionData(GraphNode graphNode,
			GraphNode graphNode2, Endpoint endp1, Endpoint endp2) {

		if (graphNode == null || graphNode2 == null) {
			return;
		}
		GraphConnectionData graphConnectionData = new GraphConnectionData(
				graphNode.getData(), graphNode2.getData(), endp1, endp2);

		logger.info("Restoring network connection between " + endp1 + " and "
				+ endp2);

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

		// @todo w przyszlosci trzeba zapamietac obiekty jesli zrobiony byl
		// wczesniej deployment
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

		logger.trace("Creating new graph item");

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

		logger.debug("Updating tool tip for object " + obj);

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

		private static final int KILO = 1024;
		private static final double EIGHT = 8.0;

		@Override
		public void run() {

			try {

				while (true) {

					logger.info("Refreshing average network bandwidth");

					for (GraphConnectionData graphConnectionData : graphConnectionDataList) {

						StringBuilder sb = new StringBuilder();
						if (graphConnectionData.getEndp1() != null
								&& graphConnectionData.getEndp1() instanceof Interface) {
							sb.append(((Interface) graphConnectionData
									.getEndp1()).getIpAddress());
						}
						if (graphConnectionData.getStatistic1() != null) {
							sb.append(" received: ");
							sb.append(countAvgBandwidth(graphConnectionData
									.getStatistic1().getAverageStatistics()
									.get(LinkStatistics.RBYTES)));
							sb.append(" sent: ");
							sb.append(countAvgBandwidth(graphConnectionData
									.getStatistic1().getAverageStatistics()
									.get(LinkStatistics.OBYTES)));
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
							sb.append(countAvgBandwidth(graphConnectionData
									.getStatistic2().getAverageStatistics()
									.get(LinkStatistics.RBYTES)));
							sb.append("kbps sent: ");
							sb.append(countAvgBandwidth(graphConnectionData
									.getStatistic2().getAverageStatistics()
									.get(LinkStatistics.OBYTES)));
							sb.append("kbps");
						}

						logger.info("Updated statistics " + sb.toString());

						graphConnectionData.setToolTip(sb.toString());

					}

					updateGraphConnection = true;

					Thread.sleep(REFRESH_TIME);

				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		private String countAvgBandwidth(double value) {

			return String.valueOf(EIGHT * value / KILO);
		}

	}

	/**
	 * Zwraca adres zdalnego MBean servera
	 * 
	 * @return
	 */
	public String getConnectionAddress() {
		return componentProxyFactory.getMbServer();
	}

	public String getConnectionPort() {
		return componentProxyFactory.getMbPort();
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue supervisorsAddressObserveTextObserveWidget = SWTObservables
				.observeText(supervisorsAddress, SWT.Modify);
		IObservableValue componentProxyFactoryMbServerObserveValue = PojoObservables
				.observeValue(componentProxyFactory, "mbServer");
		bindingContext.bindValue(supervisorsAddressObserveTextObserveWidget,
				componentProxyFactoryMbServerObserveValue, null, null);
		//
		IObservableValue supervisorPortObserveTextObserveWidget = SWTObservables
				.observeText(supervisorPort, SWT.Modify);
		IObservableValue componentProxyFactoryMbPortObserveValue = PojoObservables
				.observeValue(componentProxyFactory, "mbPort");
		bindingContext.bindValue(supervisorPortObserveTextObserveWidget,
				componentProxyFactoryMbPortObserveValue, null, null);
		//
		return bindingContext;
	}
}
