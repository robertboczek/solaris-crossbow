package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.lib.Flowadm;
import java.util.List;


/**
 *
 * @author cieplik
 */
public class FlowManager implements FlowManagerMBean {

	@Override
	public List<String> getFlows() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void discover() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void create(FlowMBean flow) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void remove(String flowName, boolean temporary) {

		flowadm.remove( flowName );

	}

	public void setFlowadm( Flowadm flowadm ) {
		this.flowadm = flowadm;
	}


	Flowadm flowadm = null;

}
