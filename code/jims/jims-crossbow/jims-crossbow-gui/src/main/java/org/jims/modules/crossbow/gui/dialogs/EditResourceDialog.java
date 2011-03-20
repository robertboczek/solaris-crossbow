package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.objectmodel.filters.address.IpAddress;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;

/**
 * Dialog allowing user to specify resource properties
 * 
 * @author robert
 * 
 */
public class EditResourceDialog extends TitleAreaDialog {

	private Text repoId;
	private Text resourceId;
	private Combo interfaces;

	private Object object;
	private Button addInterfaceButton;
	private boolean isAddressable;
	private boolean hasAMachine;
	private Label label5;

	public EditResourceDialog(Shell parentShell, Object object) {
		super(parentShell);

		this.object = object;

		hasAMachine = (object instanceof Appliance) && (((Appliance)object).getType().equals(ApplianceType.MACHINE));
		isAddressable = (object instanceof Appliance);

	}

	private void setControlsValues() {

		if (isAddressable) {
			for (Interface interfac : ((Appliance) object).getInterfaces()) {
				IpAddress ipAddress = interfac.getIpAddress();
				interfaces.add(ipAddress.toString());
				interfaces.setData(ipAddress.toString(), interfac);
			}
			resourceId.setText(prepareData(((Appliance) object).getResourceId()));
		} else {
			resourceId.setText(prepareData(((Switch) object).getResourceId()));
		}
		

		if (!isAddressable) {
			interfaces.setVisible(false);
			label5.setVisible(false);
			addInterfaceButton.setVisible(false);
		}

		if (hasAMachine) {
			repoId.setText(prepareData(((Appliance)object).getRepoId()));
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
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		if (hasAMachine) {
			Label label3 = new Label(parent, SWT.NONE);
			label3.setText("Repo Id:");

			repoId = new Text(parent, SWT.NONE);
			repoId.setLayoutData(gridData);
			repoId.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (!repoId.getText().equals("")) {
						setInformation();
					} else {
						setMessage("Repo id can't be empty",
								IMessageProvider.ERROR);
					}
				}
			});

		}

		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Resource Id:");

		resourceId = new Text(parent, SWT.NONE);
		resourceId.setLayoutData(gridData);
		resourceId.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (!resourceId.getText().equals("")) {
					setInformation();
				} else {
					setMessage("Resource id can't be empty",
							IMessageProvider.ERROR);
				}
			}
		});

		label5 = new Label(parent, SWT.NONE);
		label5.setText("Select interface:");

		interfaces = new Combo(parent, SWT.NONE);
		interfaces.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				Interface interfac = (Interface) interfaces.getData(interfaces
						.getText());
				IpAddressDialog f = new IpAddressDialog(null, interfac);
				f.create();
				int index = interfaces.getSelectionIndex();
				interfaces.remove(index);
				
				if (f.open() == Window.OK) {
				}
				
				interfaces.add(interfac.getIpAddress().toString());
				interfaces.setData(interfac.getIpAddress().toString(), interfac);
				interfaces.setText("");
			}
		});

		new Label(parent, SWT.NONE);

		addInterfaceButton = new Button(parent, SWT.PUSH);
		addInterfaceButton.setText("Add interface");
		addInterfaceButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				Interface interfac = new Interface(((Appliance) object).getResourceId(), ((Appliance) object).getRepoId());
				IpAddress ipAddress = new IpAddress("0.0.0.0", 24);
				interfac.setIpAddress(ipAddress);
				IpAddressDialog f = new IpAddressDialog(null, interfac);
				f.create();
				if (f.open() == Window.OK) {
					interfaces.add(ipAddress.toString());
					interfaces.setData(ipAddress.toString(), interfac);
					((Appliance) object).addInterface(interfac);
				}
			}

		});

		new Label(parent, SWT.NONE);
		setControlsValues();

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
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

		if (hasAMachine && repoId.getText().equals("")) {
			valid = false;
			errorMessage += "RepoId can't be empty \n";
		}

		if (resourceId.getText().equals("")) {
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
		if(hasAMachine) {
			((Appliance)object).setRepoId(repoId.getText());
		}
		if(isAddressable) {
			((Appliance)object).setResourceId(resourceId.getText());
		} else {
			((Switch)object).setResourceId(resourceId.getText());
		}
	}
}
