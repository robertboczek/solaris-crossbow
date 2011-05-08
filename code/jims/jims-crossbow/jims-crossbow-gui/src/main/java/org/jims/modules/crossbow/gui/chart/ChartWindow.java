package org.jims.modules.crossbow.gui.chart;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import java.net.URL;
import java.util.Map;
import java.util.List;

import org.jims.modules.crossbow.enums.LinkStatisticTimePeriod;
import org.jims.modules.crossbow.enums.LinkStatistics;

/**
 * Form contaning all displayed charts
 * 
 * @author robert
 * 
 */
public class ChartWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5840390131933712393L;

	private JTabbedPane jTabbedPane;
	private JPopupMenu menu;

	public ChartWindow() {
		super("Statistic charts");

		createContent();
	}

	private void createContent() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		this.setLayout(new BorderLayout());

		jTabbedPane = new JTabbedPane();
		jTabbedPane.addMouseListener(new MousePopupListener());
		add(jTabbedPane);

		menu = new JPopupMenu();

		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				jTabbedPane.remove(jTabbedPane.getSelectedComponent());
			}
		});

		menu.add(closeItem);

	}

	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		private void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				menu.show(ChartWindow.this, e.getX(), e.getY());
			}
		}
	}

	/**
	 * Add new chart to jTabbed pane
	 * 
	 * @param title
	 *            Title of the chart
	 * @param imageIcon
	 *            ImageIcon object contaning image to be displayed
	 */
	public void addChart(String title, List<List<Map<LinkStatistics, Long>>> list, String []titles, 
			LinkStatisticTimePeriod linkStatisticTimePeriod, ChartType chartType) throws Exception {

		ChartPanel chartPanel = new ChartPanel();
		chartPanel.setStatisticsList(list, titles, linkStatisticTimePeriod, chartType);

		jTabbedPane.addTab(title, chartPanel);
		jTabbedPane.setSelectedComponent(chartPanel);
	}
}
