package org.jims.modules.crossbow.gui.ssh;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ConfigLoader {
	
	private static final String configLocation = "/ssh-config.xml";
	
	/**
	 * Loads ssh hosts addresses and their passwords
	 * 
	 * @return
	 */
	public List<Host> getSshHostsConfig() {

		InputStream inputStream = this.getClass().getResourceAsStream(configLocation);
		SAXParserFactory p = SAXParserFactory.newInstance();
		
		try {
			SAXParser parser = p.newSAXParser();
			SshConfigHandler sshConfigHandler = new SshConfigHandler();
			parser.parse(inputStream, sshConfigHandler);
			
			return sshConfigHandler.getSshHostsConfig();
		}catch (Exception e) {
			
			e.printStackTrace();
		}

		
		return null;
		
	}
}
