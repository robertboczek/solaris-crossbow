package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.jims.modules.crossbow.gui.chart.ChartDisplayer;
import org.jims.modules.crossbow.gui.chart.ChartTimeType;
import org.jims.modules.crossbow.objectmodel.ObjectModel;
import org.jims.modules.crossbow.objectmodel.filters.AnyFilter;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;

/**
 * Allows user to select max 3 flows to be presented on the same chart
 * 
 * @author robert
 *
 */
public class SelectFlowForChart extends Shell{
	
	private ObjectModel objectModel;

	public SelectFlowForChart(Shell parentShell, ObjectModel objectModel) {
		super(parentShell);
		setSize(450, 324);
		
		this.objectModel = objectModel;
		
		final Button addButton = new Button(this, SWT.NONE);
		final Button removeButton = new Button(this, SWT.NONE);
		final Button btnDisplayChart = new Button(this, SWT.NONE);
		final Combo chartTimeType = new Combo(this, SWT.NONE);
		
		
		final List list1 = new List(this, SWT.BORDER);
		list1.setBounds(42, 22, 142, 177);
		list1.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {

					addButton.setEnabled(list1.getSelection().length > 0);
			}

		});
		
		final List list2 = new List(this, SWT.BORDER);
		list2.setBounds(267, 22, 142, 177);
		list2.addListener(SWT.MouseDown, new Listener() {

			@Override
			public void handleEvent(Event event) {

					removeButton.setEnabled(list2.getSelection().length > 0);
			}

		});
		
		fillList(list1, objectModel);
		
		addButton.setBounds(190, 63, 68, 23);
		addButton.setText("=>");
		addButton.setEnabled(false);
		addButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				String[] selectedTab = list1.getSelection();
				
				if(list2.getItems().length + selectedTab.length > 3) {
					MessageDialog.openError(null, "Error", "You can display maximally 3 flows on the same chart");
					return;
				}
				
				for(String selected : selectedTab) {
					list2.add(selected);
					list2.setData(selected, list1.getData(selected));
					list1.remove(selected);
				}
				addButton.setEnabled(false);
				list1.setSelection(-1);
				
				if(list2.getItems().length > 0 && chartTimeType.getSelectionIndex() >= 0) {
					btnDisplayChart.setEnabled(true);
				}
			}

		});
		
		removeButton.setBounds(190, 105, 68, 23);
		removeButton.setText("<=");
		removeButton.setEnabled(false);
		removeButton.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				String[] selectedTab = list2.getSelection();
				for(String selected : selectedTab) {
					list1.add(selected);
					list1.setData(selected, list2.getData(selected));
					list2.remove(selected);
				}
				removeButton.setEnabled(false);
				if(list2.getItems().length == 0 || chartTimeType.getSelectionIndex() == -1) {
					btnDisplayChart.setEnabled(false);
				}
			}

		});
		
		chartTimeType.setBounds(267, 210, 142, 21);
		chartTimeType.add("Last minute");
		chartTimeType.setData("Last minute", ChartTimeType.MINUTELY);
		chartTimeType.add("Last 5 minutes");
		chartTimeType.setData("Last 5 minutes", ChartTimeType.FIVE_MINUTELY);
		chartTimeType.add("Last hour");
		chartTimeType.setData("Last hour", ChartTimeType.HOURLY);
		chartTimeType.add("Last day");
		chartTimeType.setData("Last day", ChartTimeType.DAILY);
		chartTimeType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if(list2.getItems().length > 0) {
					btnDisplayChart.setEnabled(true);
				}
			}
		});
		Label lblSelectOneTo = new Label(this, SWT.NONE);
		lblSelectOneTo.setBounds(42, 3, 216, 13);
		lblSelectOneTo.setText("Select from one to three flows");
		
		Label lblChooseTheTime = new Label(this, SWT.NONE);
		lblChooseTheTime.setBounds(43, 213, 156, 13);
		lblChooseTheTime.setText("Choose the time range");
		
		btnDisplayChart.setBounds(267, 246, 142, 23);
		btnDisplayChart.setEnabled(false);
		btnDisplayChart.setText("Display chart");
		btnDisplayChart.addListener(SWT.MouseUp, new Listener() {

			@Override
			public void handleEvent(Event arg0) {
				Policy[] policies = new Policy[list2.getItems().length];
				for(int i = 0; i < policies.length; i++) {
					policies[i] = (Policy) list2.getData(list2.getItem(i));
				}
				
				new ChartDisplayer((ChartTimeType) chartTimeType.getData(chartTimeType.getText()), policies);
			}

		});
	}
	
	private void fillList(List list, ObjectModel objectModel) {
		
		for(Policy policy : objectModel.getPolicies()) {
			if(!(policy.getFilter() instanceof AnyFilter)) {
				list.add(policy.getName());
				list.setData(policy.getName(), policy);
			}
		}
	}
	
	@Override
	protected void checkSubclass() {
	}
	
}
