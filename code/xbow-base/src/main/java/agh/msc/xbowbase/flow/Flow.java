package agh.msc.xbowbase.flow;

import agh.msc.xbowbase.lib.Flowadm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public class Flow implements FlowMBean {

	public Flow() {

	}

	public void setName( String name ) {
		this.name = name;
	}

	public void setLink( String link ) {
		this.link = link;
	}

	public void setAttrs( Map< String, String > attrs ) {
		this.attrs = attrs;
	}

	public void setProps( Map< String, String > props ) {
		this.props = props;
	}

	public void setTemporary( boolean temporary ) {
		this.temporary = temporary;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLink() {
		return link;
	}

	@Override
	public void setAttributes( Map< String, String > attributes ) {

		try {

			flowadm.setAttributes( name, attributes );
			attributes = flowadm.getAttributes( name );

		} catch ( Exception e ) {

			e.printStackTrace();

		}

	}


	@Override
	public Map< String, String > getAttributes() {
		return attrs;
	}


	@Override
	public void setProperties( Map< String, String > properties, boolean temporary ) {

		try {

			flowadm.setProperties( name, properties, temporary );
			props = flowadm.getProperties( name );

		} catch ( Exception e ) {

			e.printStackTrace();

		}

	}

	@Override
	public Map< String, String > getProperties() {
		return props;
	}


	@Override
	public void resetProperties( List< String > properties, boolean temporary ) {

		try {

			flowadm.resetProperties( name, properties, temporary );
			props = flowadm.getProperties( name );

		} catch ( Exception e ) {

			e.printStackTrace();

		}

	}


	@Override
	public boolean isTemporary() {
		return temporary;
	}


	public void setFlowadm( Flowadm flowadm ) {
		this.flowadm = flowadm;
	}

	protected String name;
	protected String link;
	protected Map< String, String > attrs = new HashMap< String, String >();
	protected Map< String, String > props = new HashMap< String, String >();
	protected boolean temporary;

	private Flowadm flowadm = null;

}
