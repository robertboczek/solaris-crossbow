package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.jims.modules.crossbow.gui.data.FlowData;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.PortFilter;
import org.jims.modules.crossbow.objectmodel.filters.TransportFilter;
import org.jims.modules.crossbow.objectmodel.filters.Filter.Location;
import org.jims.modules.crossbow.objectmodel.filters.PortFilter.Protocol;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy.Priority;


public class FlowDialog extends TitleAreaDialog{
	
	public static final int DELETE_CODE = 0x855374;

	private Policy policy;		

	private Text bandwidth;
	private Combo priority;
	private Text port;
	
	
	private Combo location;
	private Combo protocol;

	public FlowDialog(Shell parentShell, Policy policy) {
		super(parentShell);
		
		this.policy = policy;
	}
	
	private void setControlsValues() {

	}

	private String prepareData(String string) {
		return string == null ? "" : string;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Edit flow properties");
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
		label3.setText("Flow name:");
		
		if(policy.getFilter() instanceof PortFilter) {
			PortFilter portFilter = (PortFilter) policy.getFilter();
			
			Label label4 = new Label(parent, SWT.NONE);
			label4.setText("Protocol:");
			
			protocol = new Combo(parent, SWT.DROP_DOWN);
			protocol.setLayoutData(gridData);
			protocol.setItems(new String[] { "tcp", "udp", "sctp" });
			
			protocol.setData("tcp", Protocol.TCP);
			protocol.setData("udp", Protocol.UDP);
			protocol.setData("sctp", Protocol.SCTP);
			
			int i = 0;
			for(i=0; i<protocol.getItems().length; i++) {
				if(protocol.getData(protocol.getItem(i)).equals(portFilter.getProtocol())) {
					protocol.select(i);
					break;
				}
			}
			
			Label label5 = new Label(parent, SWT.NONE);
			label5.setText("Location:");
			
			location = new Combo(parent, SWT.DROP_DOWN);
			location.setItems(new String[] { "LOCAL", "REMOTE" });
			
			location.setData("LOCAL", Location.LOCAL);
			location.setData("REMOTE", Location.REMOTE);
			
			for(i=0; i<2; i++) {
				if(location.getData(location.getItem(i)).equals(portFilter.getLocation())) {
					location.select(i);
					break;
				}
			}

			Label label6 = new Label(parent, SWT.NONE);
			label6.setText("Port number:");
			
			port = new Text(parent, SWT.NONE);
			port.setLayoutData(gridData);
			port.setText(String.valueOf(portFilter.getPort()));
		
		} else if(policy.getFilter() instanceof TransportFilter) {
			TransportFilter transportFilter = (TransportFilter) policy.getFilter();
			
			Label label4 = new Label(parent, SWT.NONE);
			label4.setText("Protocol:");
			
			protocol = new Combo(parent, SWT.DROP_DOWN);
			protocol.setLayoutData(gridData);
			protocol.setItems(new String[] { "tcp", "udp", "sctp", "icmp", "icmpv6" });
			
			protocol.setData("tcp", org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport.TCP);
			protocol.setData("icmp", org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport.ICMP);
			protocol.setData("icmpv6", org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport.ICMPV6);
			protocol.setData("sctp", org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport.SCTP);
			protocol.setData("udp", org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport.UDP);
			
			int i = 0;
			for(i=0; i<protocol.getItems().length; i++) {
				if(protocol.getData(protocol.getItem(i)).equals(transportFilter.getTransport())) {
					protocol.select(i);
					break;
				}
			}
			
		} else if(policy.getFilter() instanceof IpFilter) {
			IpFilter ipFilter = (IpFilter) policy.getFilter();
			
			Label label7 = new Label(parent, SWT.NONE);
			label7.setText("Location:");
			
			location = new Combo(parent, SWT.DROP_DOWN);
			location.setLayoutData(gridData);
			location.setItems(new String[] { "LOCAL", "REMOTE" });
			location.setData("LOCAL", org.jims.modules.crossbow.objectmodel.filters.IpFilter.Location.LOCAL);
			location.setData("REMOTE", org.jims.modules.crossbow.objectmodel.filters.IpFilter.Location.REMOTE);
			
			int i = 0;
			for(i=0; i<2; i++) {
				if(location.getData(location.getItem(i)).equals(ipFilter.getLocation())) {
					location.select(i);
					break;
				}
			}
			
		}
		
		if(policy instanceof BandwidthPolicy) {
			
			BandwidthPolicy bandwidthPolicy = (BandwidthPolicy) policy;
			
			Label label5 = new Label(parent, SWT.NONE);
			label5.setText("Bandwidth:");

			bandwidth = new Text(parent, SWT.NONE);
			bandwidth.setLayoutData(gridData);
			bandwidth.setSize(100, 13);
			bandwidth.setText("");

			bandwidth.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					String errorMessage = validateBandwidth();
					if (errorMessage.equals("")) {
						setInformation();
					} else {
						setMessage(errorMessage, IMessageProvider.ERROR);
					}
				}

			});
			
			bandwidth.setText(String.valueOf(bandwidthPolicy.getLimit()));
		} else if(policy instanceof PriorityPolicy) {
			
			PriorityPolicy priorityPolicy = (PriorityPolicy) policy;
			
			Label label6 = new Label(parent, SWT.NONE);
			label6.setText("Priority:");

			priority = new Combo(parent, SWT.DROP_DOWN);
			priority.setLayoutData(gridData);
			priority.setItems(new String[] { "low", "medium", "high" });

			priority.setData("low", PriorityPolicy.Priority.LOW);
			priority.setData("medium", PriorityPolicy.Priority.MEDIUM);
			priority.setData("high", PriorityPolicy.Priority.HIGH);
			
			for (int i = 0; i < priority.getItemCount(); i++) {
				if (((PriorityPolicy.Priority) priority
						.getData(priority.getItem(i)))
						.equals(priorityPolicy.getPriority())) {
					priority.select(i);
					break;
				}
			}
			
		}
		
		setControlsValues();
		
		return parent;
	}
	
	private String validateBandwidth() {
		String errorMessage = "";
		if (this.bandwidth.getText().equals("") == false) {
			try {
				Integer i = Integer.parseInt(this.bandwidth.getText());
			} catch (NumberFormatException e) {
				errorMessage += "Bandwidth must be integer \n";
			}
		}
		return errorMessage;
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
	
	/*
	 * Check whether passed argument integerValue is parsable to Integer
	 */
	private boolean isValidIntegerValue(String integerValue) {

		try{
			Integer i = Integer.parseInt(integerValue);
			if(i < 1)
				return false;
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}

	private boolean isValidInput() {
		boolean valid = true;
		
		String errorMessage = "";
		
		if (valid && policy instanceof BandwidthPolicy) {
			errorMessage = validateBandwidth();
		} else if(valid && policy instanceof PriorityPolicy && priority.getSelectionIndex() == -1) {
			errorMessage += "Select type of priority from combobox \n";
			valid = false;
		}
		
		if(valid && policy.getFilter() instanceof PortFilter) {
			
			if(this.port.equals("") == false && !isValidIntegerValue(this.port.getText())){
				errorMessage += "Port number must be positive integer \n";
				valid = false;
			}

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
		
		if(policy.getFilter() instanceof PortFilter) {
			PortFilter portFilter = (PortFilter) policy.getFilter();
			
			portFilter.setPort(Integer.parseInt(port.getText()));
			portFilter.setProtocol((Protocol)protocol.getData(protocol.getText()));
			portFilter.setLocation((Location)location.getData(location.getText()));

		} else if(policy.getFilter() instanceof TransportFilter) {
			
			TransportFilter transportFilter = (TransportFilter) policy.getFilter();
			transportFilter.setTransport((org.jims.modules.crossbow.objectmodel.filters.TransportFilter.Transport)
				protocol.getData(protocol.getText()));
			
		} else if(policy.getFilter() instanceof IpFilter) {
			
			IpFilter ipFilter = (IpFilter) policy.getFilter();
			
			ipFilter.setLocation((org.jims.modules.crossbow.objectmodel.filters.IpFilter.Location)location.getData(location.getText()));
			
		}
		
		if(policy instanceof PriorityPolicy) {
			
			PriorityPolicy priorityPolicy = (PriorityPolicy) policy;
			
			priorityPolicy.setPriority((Priority) priority.getData(priority.getText()));
		} else if(policy instanceof BandwidthPolicy) {
			BandwidthPolicy bandwidthPolicy = (BandwidthPolicy) policy;
			
			bandwidthPolicy.setLimit(Integer.valueOf(bandwidth.getText()));
		}
	}
}
