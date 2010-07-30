package agh.msc.xbowbase.jna;

import agh.msc.xbowbase.exception.ValidationException;
import agh.msc.xbowbase.flow.FlowInfo;
import agh.msc.xbowbase.flow.FlowMBean;
import agh.msc.xbowbase.lib.Flowadm;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class JNAFlowadm implements Flowadm {

	public JNAFlowadm() {

		handle = ( IFlowadm ) Native.loadLibrary( LIB_NAME, IFlowadm.class );
		handle.init();

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
	public void setAttributes( String flowName, Map< String, String > attributes ) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getAttributes(String flowName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException {

		for ( Map.Entry< String, String > entry : properties.entrySet() ) {

			String values[] = entry.getValue().split( "," );
			handle.set_property( flowName, entry.getKey(), values, values.length, 0 );

		}

	}

	@Override
	public Map< String, String > getProperties( String flowName ) {

		Map< String, String > properties = new HashMap< String, String >();

		IFlowadm.KeyValuePair kvp = handle.get_properties( flowName );
		properties.put( kvp.key, kvp.value );

		return properties;

	}

	@Override
	public void resetProperties(String flowName, Map<String, String> properties, boolean temporary) throws ValidationException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void create( FlowInfo flowInfo ) {
		handle.create( new IFlowadm.FlowInfoStruct( flowInfo ) );
	}


	private interface IFlowadm extends Library {

		public class KeyValuePair extends Structure {
			public String key, value;
		}

		public class FlowInfoStruct extends Structure {

			public FlowInfoStruct( FlowInfo flowInfo ) {
				this.name = flowInfo.getName();
				this.link = flowInfo.getLink();
				this.attrs = flatten( flowInfo.attributes );
				this.props = flatten( flowInfo.properties );
				this.temporary = ( flowInfo.temporary ? 1 : 0 );
			}


			String flatten( Map< String, String > attrs ) {

				StringBuffer stringBuffer = new StringBuffer();
				final String link = ",";

				for ( Map.Entry< String, String > entry : attrs.entrySet() ) {
					stringBuffer.append( entry.getKey() + "=" + entry.getValue() + link );
				}

				stringBuffer.setLength( stringBuffer.length() - link.length() );

				return stringBuffer.toString();

			}


			/*
			String join( String arr[], String link ) {

				StringBuffer stringBuffer = new StringBuffer();
				for ( String s : arr ) {
					stringBuffer.append( s + link );
				}

				if ( stringBuffer.length() > 0 ) {
					stringBuffer.setLength( stringBuffer.length() - link.length() );
				}

				return stringBuffer.toString();

			}
			 */

			public String name, link, attrs, props;
			public int temporary;
		}

		public void init();
		public String[] get_names();
		public int create( FlowInfoStruct flowInfo );
		public int remove( String flow );

		public int set_property( String flow, String key, String values[], int values_len, int temporary );
		public KeyValuePair get_properties( String flow );

	}


	private static final String LIB_NAME = "flowadm_wrapper";

	IFlowadm handle = null;

}
