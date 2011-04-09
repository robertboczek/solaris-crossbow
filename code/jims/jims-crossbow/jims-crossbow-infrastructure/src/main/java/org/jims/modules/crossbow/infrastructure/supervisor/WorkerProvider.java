package org.jims.modules.crossbow.infrastructure.supervisor;

import java.util.Map;
import org.jims.modules.crossbow.infrastructure.worker.WorkerMBean;


/**
 *
 * @author cieplik
 */
public interface WorkerProvider {

	Map< String, WorkerMBean > getWorkers();

}
