package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

/**
 * Class used to select which network interfaces we would like to connect
 * 
 * @author robert
 * 
 */
public class SelectNetworkInterfacesDialog extends TitleAreaDialog {

	private Combo interfaces1;
	private Combo interfaces2;

	private Object object;
	private Object object2;
	private Interface rightEndpointInterface;
	private Interface leftEndpointInterface;
	
	private Label label5, label6;

	public SelectNetworkInterfacesDialog(Shell parentShell,
			Object object, Object object2) {
		super(parentShell);

		this.object = object;
		this.object2 = object2;
	}

	private void setControlsValues() {
		if (object instanceof Appliance) {
			Appliance appliance = (Appliance) object;
			for (Interface interfac : appliance.getInterfaces()) {
				if(interfac.getEndpoint() == null) {
					interfaces1.add(interfac.getIpAddress().toString());
					interfaces1.setData(interfac.getIpAddress().toString(), interfac);
				}
			}
		}else{
			interfaces1.setVisible(false);
			label5.setVisible(false);
		}

		if (object2 instanceof Appliance) {
			Appliance appliance = (Appliance) object2;
			for (Interface interfac : appliance.getInterfaces()) {
				if(interfac.getEndpoint() == null) {
					interfaces2.add(interfac.getIpAddress().toString());
					interfaces2.setData(interfac.getIpAddress().toString(), interfac);
				}
			}
		}else{
			interfaces2.setVisible(false);
			label6.setVisible(false);
		}
	}

	@Override
	public void create() {
		super.create();
		setTitle("Choose network interfaces:");
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

		label5 = new Label(parent, SWT.NONE);
		label5.setText("Select first endpoint interface:");

		interfaces1 = new Combo(parent, SWT.NONE);

		label6 = new Label(parent, SWT.NONE);
		label6.setText("Select second endpoint interface:");

		interfaces2 = new Combo(parent, SWT.NONE);

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

		try {
			Interface interfac = null;
			if(object instanceof Appliance && interfaces1.getSelectionIndex() != -1) {
				interfac = (Interface) interfaces1.getData(interfaces1
					.getItem(interfaces1.getSelectionIndex()));
			}
			
			Interface interfac2 = null;
			if(object2 instanceof Appliance && interfaces2.getSelectionIndex() != -1)
				interfac2 = (Interface) interfaces2.getData(interfaces2
					.getItem(interfaces2.getSelectionIndex()));

			if ((interfac == null && object instanceof Appliance) ||
					(interfac2 == null && object2 instanceof Appliance))
			{
				throw new Exception();
			}

			leftEndpointInterface = interfac;
			rightEndpointInterface = interfac2;
		} catch (Exception e) {
			valid = false;
			errorMessage = "For router and resource you must select interface";
		}

		if (!errorMessage.equals(""))
			MessageDialog.openError(null, "Error", errorMessage);

		return valid;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public Interface getRightEndpoint() {
		return rightEndpointInterface;
	}

	public Interface getLeftEndpoint() {
		return leftEndpointInterface;
	}

}