package org.jims.modules.crossbow.gui.ssh;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

public class SshPanel extends JPanel {
	
	private static final Logger logger = Logger.getLogger(SshPanel.class);

	private JTextArea console = new JTextArea();
	
	private static final int ENTER = 10;
	private static final int BACKSPACE = 8;

	private Host host;
	private SshCommandExecutor sshCommandExecutor;

	private String zoneName;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5450809494952286822L;

	public SshPanel(Host host, String zoneName) {
		this.host = host;
		this.zoneName = zoneName;
		createContents();
	}

	private void createContents() {
		
		this.setLayout(new BorderLayout());
		this.add(console, BorderLayout.CENTER);

		console.setText("Console v.1.0.0");

		sshCommandExecutor = new SshCommandExecutor(host);
		boolean connect = true;
		try {
			sshCommandExecutor.connect(zoneName);
			connect = false;
		} catch (IOException e1) {
			logger.error("Connection to: " + host.getAddress() + " failed....");
			logger.error(e1);
		}

		if (connect) {
			console.addKeyListener(new KeyListener() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == ENTER) {
						
						String command = console.getText().substring(console.getText().lastIndexOf("\n") + 1);
						logger.trace("Typed command: \'"+command+"\'");
						
					} else if (e.getKeyCode() == BACKSPACE) {
						if (console.getText().charAt(
								console.getText().length() - 1) == 10) {
							console.setText(console.getText() + "\n");
						}
					}
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
		} else {
			console.setText(console.getText()+ "\n Couldn't connect to host \n The console will be unavailable...");
			console.setEditable(false);
		}
	}
	
	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public void disconnect() {
		try {
			sshCommandExecutor.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
