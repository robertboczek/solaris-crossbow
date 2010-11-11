package org.jims.modules.crossbow.flow;

import java.util.Date;
import java.util.List;


/**
 *
 * @author cieplik
 */
public class FlowAccounting implements FlowAccountingMBean {

	@Override
	public boolean isAccountingEnabled() {
		return accountingEnabled;
	}

	@Override
	public void enableAccounting() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void disableAccounting() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public FlowUsage getUsage(String flowName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<FlowUsage> getUsage(String flowName, Date start, Date end) {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private boolean accountingEnabled;

}
