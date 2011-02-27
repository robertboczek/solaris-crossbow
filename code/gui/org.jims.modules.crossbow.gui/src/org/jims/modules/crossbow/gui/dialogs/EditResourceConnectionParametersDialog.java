package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import org.jims.modules.crossbow.gui.data.IpAddress;


/**
 * Dialog allowing user to specify network connection properties 
 * 
 * @author robert
 *
 */
public class EditResourceConnectionParametersDialog extends TitleAreaDialog{
	
	private Text bandwidth;
	private Combo priority;
	private GraphConnectionData graphConnectionData;
	private Combo leftEndpointAddress, rightEndpointAddress;
	
	private IpAddress oldIpAddress1, oldIpAddress2;

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

	}
	
	private void setInformation(){
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
		
		bandwidth.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				String errorMessage = validateBandwidth();
				if(errorMessage.equals("")){
					setInformation();
				}else{
					setMessage(errorMessage, IMessageProvider.ERROR);
				}
			}
			
		});
		
		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Priority:");

		priority = new Combo(parent, SWT.DROP_DOWN);
		priority.setLayoutData(gridData);
		priority.setItems(new String[]{"low", "medium", "high"});
		
		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Left interface endpoint:");

		leftEndpointAddress = new Combo(parent, SWT.DROP_DOWN);
		leftEndpointAddress.setLayoutData(gridData);		
		
		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Right interface endpoint:");

		rightEndpointAddress = new Combo(parent, SWT.DROP_DOWN);
		rightEndpointAddress.setLayoutData(gridData);
				
		for(IpAddress ipAddress : graphConnectionData.getLeftNode().getInterfaces()){
			if(ipAddress.getGraphConnectionData() == null || ipAddress.getGraphConnectionData() == graphConnectionData){
				leftEndpointAddress.add(ipAddress.toString());
				leftEndpointAddress.setData(ipAddress.toString(), ipAddress);				
			}			
		}
		
		
		for(IpAddress ipAddress : graphConnectionData.getRightNode().getInterfaces()){
			if(ipAddress.getGraphConnectionData() == null || ipAddress.getGraphConnectionData() == graphConnectionData){
				rightEndpointAddress.add(ipAddress.toString());
				rightEndpointAddress.setData(ipAddress.toString(), ipAddress);
			}
		}	
		
		for(int i = 0; i<leftEndpointAddress.getItemCount(); i++){
			IpAddress ipAddress = (IpAddress) leftEndpointAddress.getData(leftEndpointAddress.getItem(i));
			if(ipAddress.equals(graphConnectionData.getLeftNode().findIpAddress(graphConnectionData))){
				leftEndpointAddress.select(i);
				oldIpAddress1 = ipAddress;
			}
		}
		
		for(int i = 0; i<rightEndpointAddress.getItemCount(); i++){
			IpAddress ipAddress = (IpAddress) rightEndpointAddress.getData(rightEndpointAddress.getItem(i));
			if(ipAddress.equals(graphConnectionData.getRightNode().findIpAddress(graphConnectionData))){
				rightEndpointAddress.select(i);
				oldIpAddress2 = ipAddress;
			}
		}
		
		new Label(parent, SWT.NONE);new Label(parent, SWT.NONE);
		
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
		String errorMessage = validateBandwidth();
		
		/* W sumie nie musimy wymagac podania priorytetu
		 * if(this.priority.getText().equals("")){
			errorMessage += "You must select priority\n";
		}*/
		if(errorMessage.equals("") == false){
			setMessage(errorMessage, IMessageProvider.ERROR);
			valid = false;
		}
		
		return valid;
	}

	private String validateBandwidth() {
		String errorMessage = "";
		if(this.bandwidth.getText().equals("") == false){
			try{
				Integer i = Integer.parseInt(this.bandwidth.getText());
			}catch(NumberFormatException e){
				errorMessage += "Bandwidth must be integer \n";
			}
		}		
		return errorMessage;
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
		
		oldIpAddress1.setGraphConnectionData(null);
		
		IpAddress ipAddress = (IpAddress) leftEndpointAddress.getData(leftEndpointAddress.getItem(leftEndpointAddress.getSelectionIndex()));
		ipAddress.setGraphConnectionData(graphConnectionData);
		
		oldIpAddress2.setGraphConnectionData(null);
		
		ipAddress = (IpAddress) rightEndpointAddress.getData(rightEndpointAddress.getItem(rightEndpointAddress.getSelectionIndex()));
		ipAddress.setGraphConnectionData(graphConnectionData);
		
	}
}


