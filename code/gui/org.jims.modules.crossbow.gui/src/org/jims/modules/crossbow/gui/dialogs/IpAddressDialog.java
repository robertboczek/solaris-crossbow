package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.jims.modules.crossbow.gui.data.FlowData;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.filters.Filter;
import org.jims.modules.crossbow.objectmodel.filters.IpFilter;
import org.jims.modules.crossbow.objectmodel.filters.PortFilter;
import org.jims.modules.crossbow.objectmodel.filters.TransportFilter;
import org.jims.modules.crossbow.objectmodel.policy.BandwidthPolicy;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.policy.PriorityPolicy;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

public class IpAddressDialog extends TitleAreaDialog {

	public static final int CANCEL_CODE = 0x8574;

	private Interface interfac;

	private Text address;
	private Spinner netmask;
	private Combo flows;
	private Button addFlowButton;

	private Text bandwidth;
	private Combo priority;

	private String selectedFlowType;

	private String bandwidthOrPriority;

	public IpAddressDialog(Shell parentShell, Interface interfac) {
		super(parentShell);

		this.interfac = interfac;
	}

	private void setControlsValues() {
		if (interfac.getIpAddress() != null) {
			if (interfac.getIpAddress().getAddress() != null) {
				this.address.setText(prepareData(interfac.getIpAddress()
						.getAddress()));
				this.netmask.setValues(interfac.getIpAddress().getNetmask(),
						30, 30, 2, 1, 1);
			}
		}

		if (interfac.getPoliciesList().size() > 0) {
			for (Policy policy : interfac.getPoliciesList()) {
				if (policy.getFilter() instanceof AnyFilter) {
					if (policy instanceof PriorityPolicy) {
						PriorityPolicy priorityPolicy = (PriorityPolicy) policy;
						for (int i = 0; i < priority.getItemCount(); i++) {
							if (((PriorityPolicy.Priority) priority
									.getData(priority.getItem(i)))
									.equals(priorityPolicy.getPriority())) {
								priority.select(i);
								break;
							}
						}
					} else if (policy instanceof BandwidthPolicy) {
						BandwidthPolicy bandwidthPolicy = (BandwidthPolicy) policy;
						bandwidth.setText(String.valueOf(bandwidthPolicy
								.getLimit()));
					}
				} else {
					flows.add(policy.toString());
					flows.setData(policy.toString(), policy);
				}
			}
		}
	}

	private String prepareData(String string) {
		return string == null ? "" : string;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Type ip address and netmask");

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
		gridData.minimumWidth = 200;

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Address:");

		address = new Text(parent, SWT.NONE);
		address.setLayoutData(gridData);

		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Netmask:");

		netmask = new Spinner(parent, SWT.NONE);
		netmask.setValues(24, 16, 30, 0, 1, 10);
		netmask.setLayoutData(gridData);

		address.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				String errorMessage = validateAddress();
				if (errorMessage.equals("")) {
					setInformation();
				} else {
					setMessage(errorMessage, IMessageProvider.ERROR);
				}
			}

		});

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

		Label label6 = new Label(parent, SWT.NONE);
		label6.setText("Priority:");

		priority = new Combo(parent, SWT.DROP_DOWN);
		priority.setLayoutData(gridData);
		priority.setItems(new String[] { "low", "medium", "high" });

		priority.setData("low", PriorityPolicy.Priority.LOW);
		priority.setData("medium", PriorityPolicy.Priority.MEDIUM);
		priority.setData("high", PriorityPolicy.Priority.HIGH);

		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Flows:");

		flows = new Combo(parent, SWT.NONE);
		flows.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				Policy policy = (Policy) flows.getData(flows.getText());
				FlowDialog f = new FlowDialog(null, policy);
				f.create();
				if (f.open() == Window.OK) {
					int index = flows.getSelectionIndex();
					flows.remove(index);

					if (f.getReturnCode() != FlowDialog.DELETE_CODE) {
						flows.add(policy.toString(), index);
						flows.setData(policy.toString(), policy);
					}
				}
				flows.setText("");
			}
		});

		addFlowButton = new Button(parent, SWT.PUSH);
		addFlowButton.setText("Add flow");
		addFlowButton.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event arg0) {

				SelectFilterTypeDialog selectFilterTypeDialog = new SelectFilterTypeDialog(
						null, IpAddressDialog.this);

				selectFilterTypeDialog.create();

				Filter filter = null;
				Policy policy = null;

				if (selectFilterTypeDialog.open() == Window.OK) {

					if (selectedFlowType.equals("PortFilter")) {
						filter = new PortFilter(null, 1000, null);
					} else if (selectedFlowType.equals("IpFilter")) {
						filter = new IpFilter(interfac.getIpAddress(), null);
					} else if (selectedFlowType.equals("TransportFilter")) {
						filter = new TransportFilter(null);
					}

					if (bandwidthOrPriority.equals("Bandwidth")) {
						policy = new BandwidthPolicy(10000);
						policy.setName("");
					} else if (bandwidthOrPriority.equals("Priority")) {
						policy = new PriorityPolicy("", null, filter);
					}
				}

				policy.setFilter(filter);

				FlowDialog f = new FlowDialog(null, policy);
				f.create();
				if (f.open() == Window.OK) {

					if (f.getReturnCode() != FlowDialog.DELETE_CODE) {						
						flows.add(policy.toString());
						flows.setData(policy.toString(), policy);
					}
				}

				flows.setText("");
			}

		});

		new Label(parent, SWT.NONE);

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

		/* Button removeButton = createButton(parent, CANCEL, "Delete", false); */
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL_CODE);
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
		String errorMessage = validateAddress();

		if (valid) {
			errorMessage = validateBandwidth();
		}

		if (errorMessage.equals("") == false) {
			setMessage(errorMessage, IMessageProvider.ERROR);
			valid = false;
		}

		return valid;
	}

	private String validateAddress() {
		String errorMessage = "";

		try {
			String[] tab = address.getText().split("\\.");
			if (tab.length != 4) {
				throw new Exception();
			}
			for (String part : tab) {
				Integer i = Integer.parseInt(part);
				if (i > 255 || i < 0) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			errorMessage += "Address must in ipv4 format \n";
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
		this.interfac.getIpAddress().setAddress(this.address.getText());
		this.interfac.getIpAddress().setNetmask(
				Integer.parseInt(netmask.getText()));

		// aktualizuje bandwidth
		BandwidthPolicy bandwidthPolicy = null;
		for (Policy policy : interfac.getPoliciesList()) {
			if (policy.getFilter() instanceof AnyFilter) {
				if (policy instanceof BandwidthPolicy) {
					bandwidthPolicy = (BandwidthPolicy) policy;
					break;
				}
			}
		}
		if (bandwidth.getText().equals("")) {
			if (bandwidthPolicy != null) {
				interfac.getPoliciesList().remove(bandwidthPolicy);
			}
		} else {
			if (bandwidthPolicy == null) {
				bandwidthPolicy = new BandwidthPolicy(Integer.valueOf(bandwidth
						.getText()));
				bandwidthPolicy.setName("BandwidthPolicy");
				bandwidthPolicy.setFilter(new AnyFilter());
				bandwidthPolicy.setPort(interfac);
				interfac.getPoliciesList().add(bandwidthPolicy);
			} else {
				bandwidthPolicy.setLimit(Integer.valueOf(bandwidth.getText()));
			}
		}

		// aktualizuje priority
		PriorityPolicy priorityPolicy = null;
		for (Policy policy : interfac.getPoliciesList()) {
			if (policy.getFilter() instanceof AnyFilter) {
				if (policy instanceof PriorityPolicy) {
					priorityPolicy = (PriorityPolicy) policy;
					break;
				}
			}
		}

		if (priority.getSelectionIndex() != -1) {
			if (priorityPolicy == null) {
				priorityPolicy = new PriorityPolicy("PriorityPolicy",
						((PriorityPolicy.Priority) priority.getData(priority
								.getText())), new AnyFilter());
				priorityPolicy.setPort(interfac);
				interfac.addPolicy(priorityPolicy);
			} else {
				priorityPolicy.setPriority(((PriorityPolicy.Priority) priority
						.getData(priority.getText())));
			}
		}

		for (int i = 0; i < flows.getItemCount(); i++) {
			Policy policy = (Policy) flows.getData(flows.getItem(i));
			// @todo dorobic UUID
			if (!interfac.getPoliciesList().contains(policy)) {
				interfac.getPoliciesList().add(policy);
			}
		}
	}

	public void setSelectedFlowType(String text) {
		this.selectedFlowType = text;

	}

	public void setBandwidthOrPriority(String text) {
		this.bandwidthOrPriority = text;
	}

}
