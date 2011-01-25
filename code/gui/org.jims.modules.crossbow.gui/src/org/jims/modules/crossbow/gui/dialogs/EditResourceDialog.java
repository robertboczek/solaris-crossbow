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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.data.GraphNodeData;


/**
 * Dialog allowing user to specify resource properties
 * 
 * @author robert
 *
 */
public class EditResourceDialog extends TitleAreaDialog{
	
	private Text address;
	private Spinner netmask;
	private Text repoId;
	private Text resourceId;
	
	private GraphNodeData graphNodeData;

	public EditResourceDialog(Shell parentShell, GraphNodeData graphNodeData) {
		super(parentShell);
		
		this.graphNodeData = graphNodeData;
	}
	
	private void setControlsValues() {		
		address.setText(prepareData(graphNodeData.getIpAddress()));
		if(graphNodeData.getNetmask() != null && !graphNodeData.getNetmask().equals(""))
			netmask.setSelection(Integer.valueOf(graphNodeData.getNetmask()));
		repoId.setText(prepareData(graphNodeData.getRepoId()));
		resourceId.setText(prepareData(graphNodeData.getResourceId()));		
	}

	private String prepareData(String string) {
		return string == null ? "" : string;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Edit resource properties");
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

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Address:");

		address = new Text(parent, SWT.NONE);
		address.setLayoutData(gridData);
		
		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Netmask:");

		netmask = new Spinner(parent, SWT.NONE);
		netmask.setValues(24, 16, 30, 0, 1, 10);
		netmask.setLayoutData(gridData);
		
		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Repo Id:");

		repoId = new Text(parent, SWT.NONE);
		repoId.setLayoutData(gridData);
		
		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Resource Id:");

		resourceId = new Text(parent, SWT.NONE);
		resourceId.setLayoutData(gridData);
		
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
		try{
			String []tab = address.getText().split("\\.");
			if(tab.length != 4){
				throw new Exception();
			}
			for(String part : tab){
				Integer i = Integer.parseInt(part);
				if(i > 255 || i < 0){
					throw new Exception();
				}
			}
		}catch(Exception e){
			valid = false;
			errorMessage += "Address must in ipv4 format \n";
		}
		
		if(repoId.getText().equals("")){
			valid = false;
			errorMessage += "RepoId can't be empty \n";
		}
		
		if(resourceId.getText().equals("")){
			valid = false;
			errorMessage += "ResourceId can't be empty \n";
		}
		
		if(errorMessage.equals("") == false)
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
		
		graphNodeData.setIpAddress(address.getText());
		graphNodeData.setNetmask(netmask.getText());
		graphNodeData.setRepoId(repoId.getText());
		graphNodeData.setResourceId(resourceId.getText());		
	}
}

