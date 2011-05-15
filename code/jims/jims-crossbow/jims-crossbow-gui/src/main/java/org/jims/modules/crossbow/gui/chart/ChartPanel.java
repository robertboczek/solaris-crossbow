package org.jims.modules.crossbow.gui.chart;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Logger;
import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;

public class ChartPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6036298192418208360L;
	
	private List<List<Map<LinkStatistics, Long>>> list;
	
	private JPanel centerPanel = new JPanel();
	private JPanel rightPanel = new JPanel();

	private String[] titles;

	private LinkStatisticTimePeriod linkStatisticTimePeriod;

	private ChartType chartType;
	
	private static final Logger logger = Logger.getLogger(ChartPanel.class);
	
	public ChartPanel() {
		super();
		
		this.setLayout(new BorderLayout());
		this.add(centerPanel, BorderLayout.LINE_START);
		this.add(rightPanel, BorderLayout.LINE_END);
		
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		
		JRadioButton inputPacketsButton = new JRadioButton();
		inputPacketsButton.setText("Input packtes");
		inputPacketsButton.setSelected(true);
		inputPacketsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeChart(LinkStatistics.IPACKETS);
			}
			
		});
		
		JRadioButton inputBytesButton = new JRadioButton();
		inputBytesButton.setText("Input bytes");
		inputBytesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeChart(LinkStatistics.RBYTES);
			}
			
		});
		
		JRadioButton outputPacketsButton = new JRadioButton();
		outputPacketsButton.setText("Output packets");
		outputPacketsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeChart(LinkStatistics.OPACKETS);
			}
			
		});
		
		JRadioButton outputBytesButton = new JRadioButton();
		outputBytesButton.setText("Output bytes");
		outputBytesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeChart(LinkStatistics.OBYTES);
			}
			
		});
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(inputPacketsButton);
		buttonGroup.add(inputBytesButton);
		buttonGroup.add(outputPacketsButton);
		buttonGroup.add(outputBytesButton);
		
		this.rightPanel.add(inputPacketsButton);
		this.rightPanel.add(inputBytesButton);
		this.rightPanel.add(outputPacketsButton);
		this.rightPanel.add(outputBytesButton);
		
	}

	protected void changeChart(LinkStatistics linkStatistics) {
		
		try {
			//add(new JLabel(new ImageIcon(ImageIO.read(new URL("http://chart.apis.google.com/chart?cht=lc&chxt=y,x&chs=600x450&chts=FFFFFF,14&chls=3,1,0|3,1,0&chg=25.0,25.0,3,2&chco=CA3D05,87CEEB&chd=e:UCkOHH,fq7kGe&chdl=interface1|interface2&chxs=0,FFFFFF,12,0|1,FFFFFF,12,0&chf=bg,s,1F1D1D|c,lg,0,363433,1.0,2E2B2A,0.0&chtt=Average+bandwidth+on+selected+interfaces+and+flows&chxl=0:|0.0|275.0|550.0|825.0|1100.0|1:|-1h|-45min|-30min|-15min|now&chm=d,CA3D05,0,-1,12,0|d,FFFFFF,0,-1,8,0|d,87CEEB,1,-1,12,0|d,FFFFFF,1,-1,8,0")))));
			String url = new ChartPreparer().prepareChart(list, titles, ChartDescription.CHART_TITLE, linkStatisticTimePeriod, chartType, linkStatistics);
			logger.debug("Getting image from url " + url);
			JLabel label = new JLabel(new ImageIcon(ImageIO
					.read(new URL(url))));
			
			this.remove(centerPanel);
			centerPanel = new JPanel();
			centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
			this.add(centerPanel, BorderLayout.LINE_START);
			
			centerPanel.add(label);
			
			this.validate();
			this.repaint();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setStatisticsList(List<List<Map<LinkStatistics, Long>>> list, String []titles, LinkStatisticTimePeriod linkStatisticTimePeriod,
			ChartType chartType) {
		
		this.list = list;
		this.titles = titles;
		this.linkStatisticTimePeriod = linkStatisticTimePeriod;
		this.chartType = chartType;
		
		changeChart(LinkStatistics.IPACKETS);
	}

}
