package org.jims.modules.crossbow.infrastructure.progress.notification;

/**
 * Notyfikacja z aktualnym postepem deploymentu
 * 
 * @author Robert Boczek
 * 
 */
public class ProgressNotification implements JimsNotification {

	private int max;
	private int current;
	private String ipAddress;

	public ProgressNotification(int current, int max, String ipAddress) {
		this.current = current;
		this.max = max;
		
		this.ipAddress = ipAddress;
	}

	public int getMax() {
		return max;
	}

	public int getCurrent() {
		return current;
	}

	@Override
	public String getNodeIpAddress() {
		return this.ipAddress;
	}

}
