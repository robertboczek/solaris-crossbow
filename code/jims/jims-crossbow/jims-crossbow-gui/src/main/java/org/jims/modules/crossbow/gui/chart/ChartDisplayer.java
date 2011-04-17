package org.jims.modules.crossbow.gui.chart;

import java.awt.BorderLayout;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jims.modules.crossbow.objectmodel.policy.Policy;


/**
 * Displays graph with interface nad/or flow statistics
 * 
 * @author robert
 * 
 */
public class ChartDisplayer {
	
	public static int chartNumber = 0;

	/**
	 * Constructor 
	 * 
	 * @param chartTimeType Time axis type (minutely, hourly etc.)
	 * @param policies Array of flows to display on the chart
	 */
	public ChartDisplayer(ChartTimeType chartTimeType, Policy[] policies) {
		try {
			double [][]data = { {344.5, 622.7, 122.3}, {544.3, 1023.9, 111.3}, {0.0, 25.2, 300.0} };
			String legend[] = { "interface1", "interface2", "interface3" };
			displayChart(new ChartPreparer().prepareChart(data, legend, chartTimeType));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void displayChart(String urlString) throws Exception {

		JFrame frame = new JFrame("Chart number " + ++chartNumber);
		JLabel label = new JLabel(new ImageIcon(ImageIO
				.read(new URL(urlString))));
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		

	}

}
