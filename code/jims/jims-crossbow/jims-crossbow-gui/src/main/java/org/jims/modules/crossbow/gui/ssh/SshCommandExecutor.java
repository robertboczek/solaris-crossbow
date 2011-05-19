package org.jims.modules.crossbow.gui.ssh;

import java.io.IOException;

import org.apache.commons.net.ssh.SSHClient;
import org.apache.commons.net.ssh.connection.Session.Command;
import org.apache.log4j.Logger;

public class SshCommandExecutor {
	
	private static final Logger logger = Logger.getLogger(SshCommandExecutor.class);
	
	private SSHClient ssh = new SSHClient();
	private Host host;

	public SshCommandExecutor(Host host) {
		this.host = host;
	}
	
	public void connect(String zoneName) throws IOException {
        ssh.loadKnownHosts();
        logger.debug("Trying to connect to: " + host.getAddress());
        ssh.connect(host.getAddress());
        
        ssh.authPassword(host.getUsername(), host.getPasswd());
        
        logger.debug("Logging to zone " + zoneName);
		Command cmd = ssh.startSession().exec("zlogin " + zoneName);
		logger.debug("zlogin exit status: " + cmd.getExitStatus());
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 * @throws IOException 
	 */
	public String execute(String command) throws IOException {
		
		logger.debug(host.getAddress() + " executing command: " + command);
		Command cmd = ssh.startSession().exec(command);
		return cmd.getOutputAsString();
	}
	
	public void disconnect() throws IOException {
		logger.debug("Disconnecting from host: " + host.getAddress());
		ssh.disconnect();
	}

}
