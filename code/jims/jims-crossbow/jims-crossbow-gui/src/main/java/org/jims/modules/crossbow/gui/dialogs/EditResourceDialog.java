package org.jims.modules.crossbow.gui.dialogs;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jims.modules.crossbow.gui.NetworkStructureHelper;
import org.jims.modules.crossbow.gui.actions.RepoManagerProxyFactory;
import org.jims.modules.crossbow.gui.ssh.SshTerminalWindow;
import org.jims.modules.crossbow.gui.validation.FieldValidatorFactory;
import org.jims.modules.crossbow.gui.validation.RegexpStringFieldValidator;
import org.jims.modules.crossbow.gui.validation.ValidationToolkitFactory;
import org.jims.modules.crossbow.gui.validation.RegexpStringFieldValidator.Descriptor;
import org.jims.modules.crossbow.objectmodel.Actions.Action;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.RoutingTable;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.util.struct.Pair;

import com.richclientgui.toolbox.validation.ValidatingField;
import com.richclientgui.toolbox.validation.string.StringValidationToolkit;

/**
 * Dialog allowing user to specify resource properties
 * 
 * @author robert
 * 
 */
public class EditResourceDialog extends TitleAreaDialog {

	private ValidatingField<String> repoId;
	// private Text resourceId;
	private ValidatingField<String> resourceId;
	private Combo interfaces;

	private Object object;
	private Button addInterfaceButton;
	private Button openTerminalButton;
	private boolean isAddressable;
	private boolean hasAMachine;
	private Label interfaceLabel;
	private RepoManagerProxyFactory repoManagerProxyFactory;
	private NetworkStructureHelper networkStructureHelper;
	private Shell parentShell;

	private StringValidationToolkit strToolkit;
	private ValidationToolkitFactory validationToolkitFactory;

	private FieldValidatorFactory fieldValidatorFactory;
	private SshTerminalWindow terminalWindow;

	private static final Descriptor VAL_REPO_ID = new Descriptor("[a-zA-Z]+",
			"", "- only letters are allowed");

	private static final Logger logger = Logger
			.getLogger(EditResourceDialog.class);

	public EditResourceDialog(Shell parentShell,
			FieldValidatorFactory fieldValidatorFactory, Object object,
			RepoManagerProxyFactory repoManagerProxyFactory,
			NetworkStructureHelper networkStructureHelper,
			SshTerminalWindow terminalWindow) {

		super(parentShell);

		this.parentShell = parentShell;
		this.terminalWindow = terminalWindow;

		this.fieldValidatorFactory = fieldValidatorFactory;

		this.object = object;
		this.repoManagerProxyFactory = repoManagerProxyFactory;
		this.networkStructureHelper = networkStructureHelper;

		hasAMachine = (object instanceof Appliance)
				&& (((Appliance) object).getType()
						.equals(ApplianceType.MACHINE));
		isAddressable = (object instanceof Appliance);

	}

	private void setControlsValues() {

		if (isAddressable) {
			for (Interface interfac : ((Appliance) object).getInterfaces()) {
				IpAddress ipAddress = interfac.getIpAddress();
				interfaces.add(ipAddress.toString());
				interfaces.setData(ipAddress.toString(), interfac);
			}
			resourceId.setContents(prepareData(((Appliance) object)
					.getResourceId()));
		} else {
			resourceId.setContents(prepareData(((Switch) object)
					.getResourceId()));
		}

		if (!isAddressable) {
			interfaces.setVisible(false);
			interfaceLabel.setVisible(false);
			addInterfaceButton.setVisible(false);
		}

		if (hasAMachine) {
			repoId.setContents(prepareData(((Appliance) object).getRepoId()));
		}
	}

	private String prepareData(String string) {
		return string == null ? "" : string;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Edit resource properties");
		setInformation();
	}

	private void setInformation() {
		setMessage("Provide resource details", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		strToolkit = validationToolkitFactory.createStringValidationToolkit();

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		if (hasAMachine) {
			Label label3 = new Label(parent, SWT.NONE);
			label3.setText("Repo Id:");

			repoId = strToolkit.createComboField(parent,
					new RegexpStringFieldValidator(VAL_REPO_ID), true, "",
					repoManagerProxyFactory.getRepoManager().getIds().toArray(
							new String[0]));

			repoId.getControl().setLayoutData(gridData);
		}

		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Resource Id:");

		resourceId = strToolkit
				.createTextField(
						parent,
						fieldValidatorFactory
								.createNameValidator(
										hasAMachine ? Appliance.class
												: Switch.class,
										"",
										"- must not be empty\n- has to start with a letter\n- must not contain underscores"),
						true, "");

		resourceId.getControl().setLayoutData(gridData);

		interfaceLabel = new Label(parent, SWT.NONE);
		interfaceLabel.setText("Select interface:");

		interfaces = new Combo(parent, SWT.NONE);
		interfaces.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				Interface interfac = (Interface) interfaces.getData(interfaces
						.getText());
				Interface newInterfac = new Interface(interfac.getResourceId(),
						interfac.getProjectId());
				newInterfac.setPoliciesList(interfac.getPoliciesList());
				newInterfac.setIpAddress(interfac.getIpAddress());

				IpAddressDialog f = new IpAddressDialog(null, newInterfac,
						networkStructureHelper);
				f.create();
				int index = interfaces.getSelectionIndex();
				interfaces.remove(index);

				if (f.open() == Window.OK) {
					interfac.setPoliciesList(newInterfac.getPoliciesList());
					interfac.setIpAddress(newInterfac.getIpAddress());
					networkStructureHelper.updateElement(interfac);

					interfaces.add(interfac.getIpAddress().toString());
					interfaces.setData(interfac.getIpAddress().toString(),
							interfac);
					interfaces.setText("");
				}

			}
		});

		new Label(parent, SWT.NONE);

		addInterfaceButton = new Button(parent, SWT.PUSH);
		addInterfaceButton.setText("Add interface");
		addInterfaceButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				Interface interfac = new Interface(((Appliance) object)
						.getResourceId(), ((Appliance) object).getRepoId());
				IpAddress ipAddress = new IpAddress("0.0.0.0", 24);
				interfac.setIpAddress(ipAddress);
				IpAddressDialog f = new IpAddressDialog(null, interfac,
						networkStructureHelper);
				f.create();
				if (f.open() == Window.OK) {
					interfaces.add(ipAddress.toString());
					interfaces.setData(ipAddress.toString(), interfac);
					((Appliance) object).addInterface(interfac);

					// updejtuje element
					networkStructureHelper.addNewElement(interfac);
				}
			}

		});

		new Label(parent, SWT.NONE);

		openTerminalButton = new Button(parent, SWT.PUSH);
		openTerminalButton.setText("Open terminal");
		openTerminalButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				try {
					terminalWindow.addTerminal(null, null);
				} catch (Exception e) {
					logger.debug("Couldn't add terminal");
					logger.error(e);
				}

				// @todo open terminal
				if (terminalWindow.isVisible()) {
					terminalWindow.setVisible(true);
				}
			}

		});
		// open terminal only if it's deployed
		addInterfaceButton.setEnabled(hasAMachine
				&& !networkStructureHelper.getApplianceAction(
						(Appliance) object).equals(Action.ADD));

		setControlsValues();

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;

		parent.setLayoutData(gridData);
		createOkButton(parent, OK, "Save", true);

		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});

		if (object instanceof Appliance) {

			Button rtButton = new Button(parent, SWT.NONE);
			rtButton.setText("RTables");

			// Add a SelectionListener
			rtButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {

					Appliance app = (Appliance) object;

					List<Pair<String, String>> entries = new LinkedList<Pair<String, String>>();

					for (Map.Entry<IpAddress, IpAddress> entry : app
							.getRoutingTable().getRoutes().entrySet()) {
						entries.add(new Pair<String, String>(entry.getKey()
								.toString(), entry.getValue().toString()));
					}

					RoutingTableDialog dialog = new RoutingTableDialog(
							parentShell, SWT.NONE, entries);
					dialog.open();

					RoutingTable routingTable = new RoutingTable();
					for (Pair<String, String> entry : entries) {
						routingTable.routeAdd(
								IpAddress.fromString(entry.first), IpAddress
										.fromString(entry.second));
					}

					app.setRoutingTable(routingTable);

				}
			});

		}

	}

	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}

	private boolean isValidInput() {
		boolean valid = true;

		String errorMessage = "";

		if (hasAMachine && repoId.getContents().equals("")) {
			valid = false;
			errorMessage += "RepoId can't be empty \n";
		}

		if (resourceId.getContents().equals("")) {
			valid = false;
			errorMessage += "ResourceId can't be empty \n";
		}

		if (errorMessage.equals("") == false)
			MessageDialog.openError(null, "Error", errorMessage);

		return valid;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	private void saveInput() {
		if (hasAMachine) {
			((Appliance) object).setRepoId(repoId.getContents());
		}
		if (isAddressable) {
			((Appliance) object).setResourceId(resourceId.getContents());
		} else {
			((Switch) object).setResourceId(resourceId.getContents());
		}
	}

	public void setValidationToolkitFactory(
			ValidationToolkitFactory validationToolkitFactory) {
		this.validationToolkitFactory = validationToolkitFactory;
	}
}
