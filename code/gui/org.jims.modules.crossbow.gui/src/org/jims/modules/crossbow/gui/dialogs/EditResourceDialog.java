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
import org.jims.modules.crossbow.gui.data.GraphNodeData;
import org.jims.modules.crossbow.gui.data.IpAddress;


/**
 * Dialog allowing user to specify resource properties
 * 
 * @author robert
 *
 */
public class EditResourceDialog extends TitleAreaDialog{
		
	private Text repoId;
	private Text resourceId;
	private Combo interfaces;
	
	private GraphNodeData graphNodeData;
	private Button addInterfaceButton;

	public EditResourceDialog(Shell parentShell, GraphNodeData graphNodeData) {
		super(parentShell);
		
		this.graphNodeData = graphNodeData;
	}
	
	private void setControlsValues() {		
		for(IpAddress ipAddress : graphNodeData.getInterfaces()){
			interfaces.add(ipAddress.toString());
			interfaces.setData(ipAddress.toString(), ipAddress);
		}
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
		
		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Repo Id:");

		repoId = new Text(parent, SWT.NONE);
		repoId.setLayoutData(gridData);
		repoId.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if(!repoId.getText().equals("")){
					setInformation();
				}else{
					setMessage("Repo id can't be empty", IMessageProvider.ERROR);
				}
			}			
		});		
		
		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Resource Id:");

		resourceId = new Text(parent, SWT.NONE);
		resourceId.setLayoutData(gridData);
		resourceId.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if(!resourceId.getText().equals("")){
					setInformation();
				}else{
					setMessage("Resource id can't be empty", IMessageProvider.ERROR);
				}
			}			
		});
		
		Label label5 = new Label(parent, SWT.NONE);
		label5.setText("Select interface:");

		interfaces = new Combo(parent, SWT.NONE);
		interfaces.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				IpAddress ipAddress = (IpAddress) interfaces.getData(interfaces.getText());
				IpAddressDialog f = new IpAddressDialog(null, ipAddress);
				f.create();
				if(f.open() == Window.OK){
					int index = interfaces.getSelectionIndex();
					interfaces.remove(index);
					
					if(f.getReturnCode() == IpAddressDialog.DELETE_CODE){
						graphNodeData.getInterfaces().remove(ipAddress);
					}else{
						interfaces.add(ipAddress.toString(), index);
						interfaces.setData(ipAddress.toString(), ipAddress);						
					}
				}
				interfaces.setText("");
			}			
		});
		
		new Label(parent, SWT.NONE);

		addInterfaceButton = new Button(parent, SWT.PUSH);
		addInterfaceButton.setText("Add interface");
		addInterfaceButton.addListener(SWT.MouseDown, new Listener(){

			@Override
			public void handleEvent(Event arg0) {
				
				IpAddress ipAddress = new IpAddress();
				IpAddressDialog f = new IpAddressDialog(null, ipAddress);
				f.create();
				if(f.open() == Window.OK){					
					interfaces.add(ipAddress.toString());
					interfaces.setData(ipAddress.toString(), ipAddress);
					graphNodeData.getInterfaces().add(ipAddress);
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
		graphNodeData.setRepoId(repoId.getText());
		graphNodeData.setResourceId(resourceId.getText());		
	}
}

