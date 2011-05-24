package org.jims.modules.crossbow.gui.ssh;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SshConfigHandler extends DefaultHandler {
	
	private List<Host> hosts = null;
	private Host newHost;
	private boolean address = false, passwd = false, username = false;
	
	private Logger logger = Logger.getLogger(SshConfigHandler.class);
	

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		if(address) {
			address = false;
			String value = new String( ch , start , length );
			newHost.setAddress(value);
			logger.trace("With address: " + value);
		} else if(passwd) {
			passwd = false;
			char[] passwd = Arrays.copyOfRange(ch, start, start+length);
			newHost.setPasswd(passwd);
			logger.trace("And password: " + String.valueOf(passwd));
		} else if(username) {
			username = false;
			String value = new String( ch , start , length );
			newHost.setUsername(value);
			logger.trace("Username: " + value);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(qName.equals("host")) {
			hosts.add(newHost);
			newHost = null;
		}
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
		
		hosts = new LinkedList<Host>();
		
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		if(qName.equals("host")) {
			logger.trace("New host");
			newHost = new Host();
		} else if(qName.equals("address")) {
			address = true;
		} else if(qName.equals("password")) {
			passwd = true;
		} else if(qName.equals("user")) {
			username = true;
		}
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}
	
	public List<Host> getSshHostsConfig() {
		
		return hosts;
		
	}

}
