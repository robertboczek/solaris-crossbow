package org.jims.modules.crossbow.gui.ssh;

import java.io.IOException;

import org.apache.commons.net.ssh.SSHClient;
import org.apache.commons.net.ssh.connection.Session;
import org.apache.log4j.Logger;

public class SshCommandExecutor {
	
	private static final Logger logger = Logger.getLogger(SshCommandExecutor.class);
	
	private SSHClient ssh = new SSHClient();
	private Host host;
	private Session session = null;

	public SshCommandExecutor(Host host) {
		this.host = host;
	}
	
	public void connect(String zoneName) throws IOException {
        ssh.loadKnownHosts();
        logger.debug("Trying to connect to: " + host.getAddress());
        ssh.connect(host.getAddress());
        ssh.authPassword(host.getUsername(), host.getPasswd());
        
        ssh.setKeepAlive(true);
        session = ssh.startSession();
        ssh.setKeepAlive(true);
        session.setAutoExpand(true);
        
//        session.
        //Shell shell = session.startShell();
        
        logger.debug("Logging to zone " + zoneName);
		//Command cmd = session.exec("zlogin " + zoneName);
		//logger.debug(cmd.getOutputAsString());
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 * @throws IOException 
	 */
	public String execute(String command) throws IOException {
		
		logger.debug(host.getAddress() + " executing command: " + command);
		if(session != null) {
			return session.exec(command).getOutputAsString();
		}
		return null;
	}
	
	public void disconnect() throws IOException {
		logger.debug("Disconnecting from host: " + host.getAddress());
		session.close();
		ssh.disconnect();
	}

}
