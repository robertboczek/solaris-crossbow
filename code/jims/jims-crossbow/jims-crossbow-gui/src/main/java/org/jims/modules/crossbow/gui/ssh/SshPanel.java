package org.jims.modules.crossbow.gui.ssh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.KnownHosts;
import ch.ethz.ssh2.ServerHostKeyVerifier;
import ch.ethz.ssh2.Session;

public class SshPanel extends JPanel {

	private static final Logger logger = Logger.getLogger(SshPanel.class);
	private static final String knownHostPath = "~/.ssh/known_hosts";
	private static final String idDSAPath = "~/.ssh/id_dsa";
	private static final String idRSAPath = "~/.ssh/id_rsa";

	private JTextArea console;

	private Host host;
	private String zoneName;

	private InputStream in;
	private OutputStream out;
	private KnownHosts database = new KnownHosts();

	private RemoteConsumer remoteConsumer;
	private ConnectionThread connectionThread;

	private Session sess;

	private JFrame frame;
	private JScrollPane scrollPane;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5450809494952286822L;

	public SshPanel(Host host, String zoneName) {
		this.host = host;
		this.zoneName = zoneName;

		this.frame = (JFrame) this.getParent();
		createContents();
	}

	private void createContents() {

		Font f = new Font("Monospaced", Font.PLAIN, 16);

		console = new JTextArea(y, x);
		console.setText("Console v.1.0.0\n");
		console.setEditable(false);

		connectionThread = new ConnectionThread();
		connectionThread.start();

		console.setFont(f);
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		console.setSize(480, 640);

		// Create a tabbed pane
		scrollPane = new JScrollPane(console);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);

		this.setMaximumSize(new Dimension(520, 700));

		// console.setCaretColor(Color.WHITE);

		KeyAdapter kl = new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				int c = e.getKeyChar();

				try {
					out.write(c);
				} catch (IOException e1) {
				}
				e.consume();
			}
		};

		console.addKeyListener(kl);
	}

	int x = 90, y = 30;

	/**
	 * This thread consumes output from the remote server and displays it in the
	 * terminal window.
	 * 
	 */
	class RemoteConsumer extends Thread {
		char[][] lines = new char[y][];
		int posy = 0;
		int posx = 0;

		private void addText(byte[] data, int len) {
			for (int i = 0; i < len; i++) {
				char c = (char) (data[i] & 0xff);

				if (c == 8) // Backspace, VERASE
				{
					if (posx < 0)
						continue;
					posx--;
					continue;
				}

				if (c == '\r') {
					posx = 0;
					continue;
				}

				if (c == '\n') {
					posy++;
					if (posy >= y) {
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						posy--;
						lines[y - 1] = new char[x];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
					continue;
				}

				if (c < 32) {
					continue;
				}

				if (posx >= x) {
					posx = 0;
					posy++;
					if (posy >= y) {
						posy--;
						for (int k = 1; k < y; k++)
							lines[k - 1] = lines[k];
						lines[y - 1] = new char[x];
						for (int k = 0; k < x; k++)
							lines[y - 1][k] = ' ';
					}
				}

				if (lines[posy] == null) {
					lines[posy] = new char[x];
					for (int k = 0; k < x; k++)
						lines[posy][k] = ' ';
				}

				lines[posy][posx] = c;
				posx++;
			}

			StringBuffer sb = new StringBuffer(x * y);

			for (int i = 0; i < lines.length; i++) {
				if (i != 0)
					sb.append('\n');

				if (lines[i] != null) {
					sb.append(lines[i]);
				}

			}
			setContent(sb.toString());
		}

		public void run() {
			byte[] buff = new byte[8192];

			try {
				while (true) {
					int len = in.read(buff);
					if (len == -1)
						return;
					addText(buff, len);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void setContent(String lines) {
		if (lines != null) {
			console.setText(lines);
			scrollPane.getVerticalScrollBar().setValue(
					scrollPane.getVerticalScrollBar().getMaximum());
		}
	}

	/**
	 * This ServerHostKeyVerifier asks the user on how to proceed if a key
	 * cannot be found in the in-memory database.
	 * 
	 */
	class AdvancedVerifier implements ServerHostKeyVerifier {
		public boolean verifyServerHostKey(String hostname, int port,
				String serverHostKeyAlgorithm, byte[] serverHostKey)
				throws Exception {
			final String host = hostname;
			final String algo = serverHostKeyAlgorithm;

			String message;

			/* Check database */

			int result = database.verifyHostkey(hostname,
					serverHostKeyAlgorithm, serverHostKey);

			switch (result) {
			case KnownHosts.HOSTKEY_IS_OK:
				return true;

			case KnownHosts.HOSTKEY_IS_NEW:
				message = "Do you want to accept the hostkey (type " + algo
						+ ") from " + host + " ?\n";
				break;

			case KnownHosts.HOSTKEY_HAS_CHANGED:
				message = "WARNING! Hostkey for " + host
						+ " has changed!\nAccept anyway?\n";
				break;

			default:
				throw new IllegalStateException();
			}

			/* Include the fingerprints in the message */

			String hexFingerprint = KnownHosts.createHexFingerprint(
					serverHostKeyAlgorithm, serverHostKey);
			String bubblebabbleFingerprint = KnownHosts
					.createBubblebabbleFingerprint(serverHostKeyAlgorithm,
							serverHostKey);

			message += "Hex Fingerprint: " + hexFingerprint
					+ "\nBubblebabble Fingerprint: " + bubblebabbleFingerprint;

			/* Now ask the user */

			int choice = JOptionPane.showConfirmDialog(SshPanel.this.frame,
					message);

			if (choice == JOptionPane.YES_OPTION) {
				/* Be really paranoid. We use a hashed hostname entry */

				String hashedHostname = KnownHosts
						.createHashedHostname(hostname);

				/* Add the hostkey to the in-memory database */

				database.addHostkey(new String[] { hashedHostname },
						serverHostKeyAlgorithm, serverHostKey);

				/* Also try to add the key to a known_host file */

				try {
					KnownHosts.addHostkeyToFile(new File(knownHostPath),
							new String[] { hashedHostname },
							serverHostKeyAlgorithm, serverHostKey);
				} catch (IOException ignore) {
				}

				return true;
			}

			if (choice == JOptionPane.CANCEL_OPTION) {
				throw new Exception(
						"The user aborted the server hostkey verification.");
			}

			return false;
		}
	}

	/**
	 * The SSH-2 connection is established in this thread. If we would not use a
	 * separate thread (e.g., put this code in the event handler of the "Login"
	 * button) then the GUI would not be responsive (missing window repaints if
	 * you move the window etc.)
	 */
	class ConnectionThread extends Thread {

		public void run() {
			Connection conn = new Connection(host.getAddress());

			try {
				/*
				 * 
				 * CONNECT AND VERIFY SERVER HOST KEY (with callback)
				 */

				String[] hostkeyAlgos = database
						.getPreferredServerHostkeyAlgorithmOrder(host
								.getAddress());

				if (hostkeyAlgos != null)
					conn.setServerHostKeyAlgorithms(hostkeyAlgos);

				conn.connect(new AdvancedVerifier());

				int i = 0;
				while (true) {
					if (conn.isAuthMethodAvailable(host.getUsername(),
							"password")) {
						String passwd = null;;
						if (host.getPasswd() == null || String.valueOf(host.getPasswd()).equals("")) {
							EnterSomethingDialog esd = new EnterSomethingDialog(
									SshPanel.this.frame,
									"DSA Authentication",
									new String[] {
											"Password was not provided for " + host.getAddress() + " address",
											"Enter DSA private key password:" },
									true);
							esd.setVisible(true);

							passwd = esd.answer;
						} else {
							passwd = String.valueOf(host.getPasswd());
						}

						logger.info("Trying to connect to with username: "
								+ host.getUsername() + " and password: "
								+ passwd);
						boolean res = conn.authenticateWithPassword(host
								.getUsername(), passwd);

						// max 5 tries
						if (res == true || i++ == 5)
							break;

						continue;
					}

					throw new IOException(
							"No supported authentication methods available.");
				}

				/*
				 * 
				 * AUTHENTICATION OK. DO SOMETHING.
				 */
				sess = conn.openSession();

				sess.requestPTY("dumb", x, y, 0, 0, null);
				sess.startShell();

				in = sess.getStdout();
				out = sess.getStdin();

				logger.info("Logging to zone: " + zoneName);
				out.write(("zlogin " + zoneName + "\n").getBytes());
				out.write("bash\n".getBytes());

				remoteConsumer = new RemoteConsumer();
				remoteConsumer.start();

				console.setEditable(true);

			} catch (IOException e) {
				logger.error("Connection to: " + host.getAddress()
						+ " failed....");
				logger.error(e);
				console
						.setText(console.getText()
								+ "\n Couldn't connect to host \n The console will be unavailable...");
				JOptionPane.showMessageDialog(SshPanel.this.frame,
						"Exception: " + e.getMessage());
			}
		}
	}

	/**
	 * This dialog displays a number of text lines and a text field. The text
	 * field can either be plain text or a password field.
	 */
	class EnterSomethingDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		JTextField answerField;
		JPasswordField passwordField;

		final boolean isPassword;

		String answer;

		public EnterSomethingDialog(JFrame parent, String title,
				String content, boolean isPassword) {
			this(parent, title, new String[] { content }, isPassword);
		}

		public EnterSomethingDialog(JFrame parent, String title,
				String[] content, boolean isPassword) {
			super(parent, title, true);

			this.isPassword = isPassword;

			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));

			for (int i = 0; i < content.length; i++) {
				if ((content[i] == null) || (content[i] == ""))
					continue;
				JLabel contentLabel = new JLabel(content[i]);
				pan.add(contentLabel);

			}

			answerField = new JTextField(20);
			passwordField = new JPasswordField(20);

			if (isPassword)
				pan.add(passwordField);
			else
				pan.add(answerField);

			KeyAdapter kl = new KeyAdapter() {
				public void keyTyped(KeyEvent e) {
					if (e.getKeyChar() == '\n')
						finish();
				}
			};

			answerField.addKeyListener(kl);
			passwordField.addKeyListener(kl);

			getContentPane().add(BorderLayout.CENTER, pan);

			setResizable(false);
			pack();
			setLocationRelativeTo(null);
		}

		private void finish() {
			if (isPassword)
				answer = new String(passwordField.getPassword());
			else
				answer = answerField.getText();
			dispose();
		}
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public void disconnect() {
		if (remoteConsumer != null) {
			remoteConsumer.interrupt();
		}
		if (connectionThread != null) {
			connectionThread.interrupt();
		}
		logger.info("Closing session with: " + host.getAddress());
		sess.close();
	}

}
