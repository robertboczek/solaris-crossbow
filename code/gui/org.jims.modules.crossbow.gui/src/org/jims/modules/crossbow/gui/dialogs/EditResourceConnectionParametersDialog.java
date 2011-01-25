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
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;


/**
 * Dialog allowing user to specify network connection properties 
 * @author robert
 *
 */
public class EditResourceConnectionParametersDialog extends TitleAreaDialog{
	
	private Text bandwidth;
	private Combo priority;
	private Button addFlowButton;
	private GraphConnectionData graphConnectionData;

	public EditResourceConnectionParametersDialog(Shell parentShell, GraphConnectionData graphConnectionData) {
		super(parentShell);
		
		this.graphConnectionData = graphConnectionData;
	}
	
	private void setControlsValues() {
		this.bandwidth.setText(prepareData(graphConnectionData.getBandwidth()));
		this.priority.setText(prepareData(graphConnectionData.getPriority()));		
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
		gridData.minimumWidth = 200;		

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Bandwidth:");

		bandwidth = new Text(parent, SWT.NONE);
		bandwidth.setLayoutData(gridData);
		bandwidth.setSize(100, 13);
		
		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Priority:");

		priority = new Combo(parent, SWT.DROP_DOWN);
		priority.setLayoutData(gridData);
		priority.setItems(new String[]{"low", "medium", "high"});
		
		new Label(parent, SWT.NONE);new Label(parent, SWT.NONE);	
		
		new Label(parent, SWT.NONE);
		addFlowButton = new Button(parent, SWT.PUSH);
		addFlowButton.setText("Add flow");
		addFlowButton.setFont(JFaceResources.getDialogFont());
		setButtonLayoutData(addFlowButton);
		
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
			Integer i = Integer.parseInt(this.bandwidth.getText());
		}catch(NumberFormatException e){
			errorMessage += "Bandwidth must be integer \n";
			valid = false;
		}
		
		if(this.priority.getText().equals("")){
			errorMessage += "You must select priority\n";
			valid = false;
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
		this.graphConnectionData.setBandwidth(this.bandwidth.getText());
		this.graphConnectionData.setPriority(this.priority.getText());
		
	}
}


