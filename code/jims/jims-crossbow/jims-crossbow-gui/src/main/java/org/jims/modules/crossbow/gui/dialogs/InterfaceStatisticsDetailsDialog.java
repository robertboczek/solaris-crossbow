package org.jims.modules.crossbow.gui.dialogs;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;
import org.jims.modules.crossbow.gui.chart.ChartDisplayer;
import org.jims.modules.crossbow.gui.data.GraphConnectionData;
import org.jims.modules.crossbow.gui.statistics.StatisticAnalyzer.EndpointStatistic;
import org.jims.modules.crossbow.objectmodel.resources.Interface;

/**
 * Dialog displaying details about interfaces statistics
 * 
 * @author robert
 * 
 */
public class InterfaceStatisticsDetailsDialog extends TitleAreaDialog {

	private static final Logger logger = Logger
			.getLogger(InterfaceStatisticsDetailsDialog.class);

	private Combo endpoints;

	private Text receivedBytesLabel;
	private Text receivedPacketsLabel;
	private Text avgBytesReceived;
	private Text sentBytesLabel;
	private Text sentPacketsLabel;
	private Text avgBytesSent;

	private Combo chartType;

	private Interface interf = null;

	private GraphConnectionData graphConnectionData;

	public InterfaceStatisticsDetailsDialog(Shell parentShell,
			GraphConnectionData graphConnectionData) {
		super(parentShell);

		this.graphConnectionData = graphConnectionData;
	}

	public void setControlsValues() {

		if (graphConnectionData.getEndp1() != null
				&& graphConnectionData.getEndp1() instanceof Interface) {
			endpoints.add(((Interface) graphConnectionData.getEndp1())
					.getIpAddress().toString());
			endpoints.setData(((Interface) graphConnectionData.getEndp1())
					.getIpAddress().toString(), graphConnectionData
					.getStatistic1());

		}

		if (graphConnectionData.getEndp2() != null
				&& graphConnectionData.getEndp2() instanceof Interface) {
			endpoints.add(((Interface) graphConnectionData.getEndp2())
					.getIpAddress().toString());
			endpoints.setData(((Interface) graphConnectionData.getEndp2())
					.getIpAddress().toString(), graphConnectionData
					.getStatistic2());
		}

	}

	@Override
	public void create() {
		super.create();
		setTitle("Interface details");
		setInformation();

	}

	private void setInformation() {
		setMessage("Select interface to see statistic details",
				IMessageProvider.INFORMATION);
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

		Label label0 = new Label(parent, SWT.NONE);
		label0.setText("Wybierz interfejs: ");

		endpoints = new Combo(parent, SWT.NONE);
		endpoints.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				logger.debug(endpoints.getSelectionIndex());

				if (endpoints.getSelectionIndex() == 0) {
					actualizeLabels((EndpointStatistic) endpoints
							.getData(endpoints.getText()));
					interf = ((Interface) graphConnectionData.getEndp1());
				} else if (endpoints.getSelectionIndex() == 1) {
					actualizeLabels((EndpointStatistic) endpoints
							.getData(endpoints.getText()));
					interf = ((Interface) graphConnectionData.getEndp2());
				}

			}
		});

		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Recieved bytes:");

		receivedBytesLabel = new Text(parent, SWT.NONE);
		receivedBytesLabel.setText("");
		receivedBytesLabel.setEnabled(false);

		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Recieved packets:");

		receivedPacketsLabel = new Text(parent, SWT.NONE);
		receivedPacketsLabel.setText("");
		receivedPacketsLabel.setEnabled(false);

		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Average received kbps:");

		avgBytesReceived = new Text(parent, SWT.NONE);
		avgBytesReceived.setText("");
		avgBytesReceived.setEnabled(false);

		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Bytes received");

		sentBytesLabel = new Text(parent, SWT.NONE);
		sentBytesLabel.setText("");
		sentBytesLabel.setEnabled(false);

		Label label5 = new Label(parent, SWT.NONE);
		label5.setText("Sent packets:");

		sentPacketsLabel = new Text(parent, SWT.NONE);
		sentPacketsLabel.setText("");
		sentPacketsLabel.setEnabled(false);

		Label label6 = new Label(parent, SWT.NONE);
		label6.setText("Average sent kbps:");

		avgBytesSent = new Text(parent, SWT.NONE);
		avgBytesSent.setText("");
		avgBytesSent.setEnabled(false);

		Label label7 = new Label(parent, SWT.NONE);
		label7.setText("Select chart type: ");

		chartType = new Combo(parent, SWT.NONE);
		chartType.add("Last minute");
		chartType.setData("Last minute", LinkStatisticTimePeriod.MINUTELY);
		chartType.add("Last 5 minutes");
		chartType.setData("Last 5 minutes",
				LinkStatisticTimePeriod.FIVE_MINUTELY);
		chartType.add("Last hour");
		chartType.setData("Last hour", LinkStatisticTimePeriod.HOURLY);
		chartType.add("Last day");
		chartType.setData("Last day", LinkStatisticTimePeriod.DAILY);

		chartType.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				logger.debug("Opening chart");

				new ChartDisplayer((LinkStatisticTimePeriod) chartType
						.getData(chartType.getText()),
						new Interface[] { interf }, null);
			}
		});

		endpoints.setText("");

		setControlsValues();

		return parent;
	}

	/**
	 * Aktualizuje etykiety wg wybranego interfejsu
	 * 
	 * @param statistic
	 *            Zebrane statystyki dla wybranego interfejsu
	 */
	protected void actualizeLabels(EndpointStatistic statistic) {

		System.out.println(statistic);

		if (statistic == null) {
			return;
		}

		logger.debug("Received bytes: " + statistic.getReceivedBytes());
		receivedBytesLabel
				.setText(String.valueOf(statistic.getReceivedBytes()));
		receivedPacketsLabel.setText(statistic.getReceivedPackets().toString());
		avgBytesReceived.setText(new Double(8.0 * statistic
				.getAverageStatistics().get(LinkStatistics.RBYTES) / 1024.0)
				.toString());
		sentBytesLabel.setText(statistic.getSentBytes().toString());
		sentPacketsLabel.setText(statistic.getSentPackets().toString());
		avgBytesSent.setText(new Double(8.0 * statistic.getAverageStatistics()
				.get(LinkStatistics.OBYTES) / 1024.0).toString());

	}

}
