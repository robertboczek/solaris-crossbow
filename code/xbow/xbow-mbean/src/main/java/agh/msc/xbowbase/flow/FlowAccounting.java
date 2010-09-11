package agh.msc.xbowbase.flow;

import java.util.Date;
import java.util.List;


/**
 *
 * @author cieplik
 */
public class FlowAccounting implements FlowAccountingMBean {

        private static final String notSupported = "Not supported yet.";

	@Override
	public boolean isAccountingEnabled() {
		return accountingEnabled;
	}

	@Override
	public void enableAccounting() {
		throw new UnsupportedOperationException(notSupported);
	}

	@Override
	public void disableAccounting() {
		throw new UnsupportedOperationException(notSupported);
	}

	@Override
	public FlowUsage getUsage(String flowName) {
		throw new UnsupportedOperationException(notSupported);
	}

	@Override
	public List<FlowUsage> getUsage(String flowName, Date start, Date end) {
		throw new UnsupportedOperationException(notSupported);
	}


	private boolean accountingEnabled;

}
