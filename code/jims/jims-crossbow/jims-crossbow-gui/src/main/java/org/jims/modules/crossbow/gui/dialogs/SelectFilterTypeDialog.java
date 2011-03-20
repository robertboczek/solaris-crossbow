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

public class SelectFilterTypeDialog extends TitleAreaDialog{
	
	private Combo flowType;
	private Combo bandwidthOrPriority;
	
	private IpAddressDialog ipAddressDialog;

	public SelectFilterTypeDialog(Shell parentShell, IpAddressDialog ipAddressDialog) {
		super(parentShell);
		
		this.ipAddressDialog = ipAddressDialog;
	}

	private void setControlsValues() {
		
	}

	@Override
	public void create() {
		super.create();
		setTitle("Choose type of new flow");
		setInformation();
	}

	private void setInformation() {
		setMessage("Select type of new flow", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label label5 = new Label(parent, SWT.NONE);
		label5.setText("Select type of new filter:");

		flowType = new Combo(parent, SWT.NONE);
		flowType.add("PortFilter");
		flowType.add("IpFilter");
		flowType.add("TransportFilter");

		Label label6 = new Label(parent, SWT.NONE);
		label6.setText("Select priority or bandwidth:");

		bandwidthOrPriority = new Combo(parent, SWT.NONE);
		bandwidthOrPriority.add("Priority");
		bandwidthOrPriority.add("Bandwidth");

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
			

			if (flowType.getSelectionIndex() == -1 || bandwidthOrPriority.getSelectionIndex() == -1)
			{
				throw new Exception();
			}

		} catch (Exception e) {
			valid = false;
			errorMessage = "You must select type of new flow and whether this flow uses priority or bandwidth";
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
		ipAddressDialog.setSelectedFlowType(flowType.getText());
		ipAddressDialog.setBandwidthOrPriority(bandwidthOrPriority.getText());
		super.okPressed();
	}
}
