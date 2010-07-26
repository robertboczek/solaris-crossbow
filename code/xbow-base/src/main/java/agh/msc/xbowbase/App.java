package agh.msc.xbowbase;

import agh.msc.xbowbase.flow.FlowManager;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Native;


/**
 * Hello world!
 *
 * libflowadm, JNA snippet
 *
 */
public class App {

	public static void main( String args[] ) {

		Flowadm flowadm = ( Flowadm ) Native.loadLibrary( "wrapper", Flowadm.class );

		System.out.println( "libflowadm initialized, rc == " + flowadm.init() );

		FlowManager flowManager = new FlowManager();
		flowManager.setFlowadm( flowadm );

		flowManager.remove( args[ 0 ], true );

	}

}
