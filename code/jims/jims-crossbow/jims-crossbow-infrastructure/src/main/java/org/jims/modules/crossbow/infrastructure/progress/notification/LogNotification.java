package org.jims.modules.crossbow.infrastructure.progress.notification;

/**
 * Notyfikacja z logiem co wlasnie udalo sie wykonac, badz
 * co bedzie aktualnie wykonywane
 * 
 * @author Robert Boczek
 *
 */
public class LogNotification implements JimsNotification{

	private String log;
	private String ipAddress;

	public LogNotification(String log, String ipAddress) {
		this.log = log;
		this.ipAddress = ipAddress;
	}

	public String getLog() {
		return log;
	}

	@Override
	public String getNodeIpAddress() {
		return this.ipAddress;
	}
}
