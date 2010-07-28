package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class JNAFlowadm implements Flowadm {

	public JNAFlowadm() {

		handle = ( IFlowadm ) Native.loadLibrary( LIB_NAME, IFlowadm.class );

	}


	@Override
	public int remove( String flow ) {
		return handle.remove( flow );
	}


	@Override
	public String[] getNames() {
		return handle.get_names();
	}

	@Override
	public void setAttributes(String flowName, Map<String, String> attributes) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getAttributes(String flowName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProperties(String flowName, Map<String, String> properties, boolean temporary) throws ValidationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getProperties(String flowName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void resetProperties(String flowName, Map<String, String> properties, boolean temporary) throws ValidationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	private interface IFlowadm extends Library {

		public String[] get_names();
		public int remove( String flow );

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	IFlowadm handle = null;

}
