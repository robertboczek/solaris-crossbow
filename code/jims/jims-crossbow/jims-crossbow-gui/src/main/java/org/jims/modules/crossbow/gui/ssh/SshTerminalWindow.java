package org.jims.modules.crossbow.gui.ssh;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

/**
 * Form contaning all displayed charts
 * 
 * @author robert
 * 
 */
public class SshTerminalWindow extends JFrame {
	
		private static final Logger logger = Logger.getLogger(SshTerminalWindow.class);

		private static final long serialVersionUID = 5840390131933712393L;

		private JTabbedPane jTabbedPane;
		private JPopupMenu menu;
		
		Set<SshPanel> panels = new HashSet<SshPanel>();

		public SshTerminalWindow() {
			super("Ssh terminals");

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
					
					logger.debug("Removing panel tab");
					
					if(jTabbedPane.getSelectedComponent() instanceof SshPanel) {
						SshPanel sshPanel = (SshPanel) jTabbedPane.getSelectedComponent();
						logger.debug("Closing ssh connection with host: " + sshPanel.getHost());
						sshPanel.disconnect();
						panels.remove(sshPanel);
					}
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
					menu.show(SshTerminalWindow.this, e.getX(), e.getY());
				}
			}
		}

		public void addTerminal(Host host, String zoneName) throws Exception {

			SshPanel sshPanel = new SshPanel(host, zoneName);

			jTabbedPane.addTab(host.getAddress(), sshPanel);
			jTabbedPane.setSelectedComponent(sshPanel);
		}
		
		public void closeWindow() {
			
			logger.debug("Closing all existing connections");
			for(SshPanel sshPanel : panels) {
				sshPanel.disconnect();
			}
			
			Runnable r = new Runnable()
			{
				public void run()
				{
					SshTerminalWindow.this.dispose();
				}
			};

			SwingUtilities.invokeLater(r);
			this.dispose();
		}
	}
