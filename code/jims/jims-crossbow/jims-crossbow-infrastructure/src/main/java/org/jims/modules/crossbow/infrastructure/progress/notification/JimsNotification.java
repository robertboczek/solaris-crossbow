package org.jims.modules.crossbow.infrastructure.progress.notification;

import java.io.Serializable;

/**
 * Interfejs notyfikacji
 * 
 * @author Robert Boczek
 *
 */
public interface JimsNotification extends Serializable {
	
	public String getNodeIpAddress();

}
