package org.jims.modules.crossbow.gui.ssh;

public class Host {
	
	private String address;
	private String username;
	private char[] passwd;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public char[] getPasswd() {
		return passwd;
	}
	public void setPasswd(char[] passwd) {
		this.passwd = passwd;
	}
	
	@Override
	public String toString() {
		return address;
	}

}
