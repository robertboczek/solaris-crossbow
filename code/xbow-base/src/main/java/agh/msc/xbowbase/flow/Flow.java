package agh.msc.xbowbase.flow;

import java.util.Map;


/**
 *
 * @author cieplik
 */
public class Flow implements FlowMBean {

  public Flow() {

  }

  public void setName (String name) {

  }

  public void setLink (String link) {

  }

  public void setAttrs (Map< String, String > attrs) {

  }

  public void setProps (Map< String, String > props) {

  }

  public void setTemporary (boolean temporary) {

  }

	@Override
	public String getName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getLink() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setAttributes(Map<String, String> attributes) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getAttributes() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setProperties(Map<String, String> properties, boolean temporary) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<String, String> getProperties() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void resetProperties(Map<String, String> properties, boolean temporary) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isTemporary() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	protected String name;
	protected String link;
	protected Map< String, String > attrs;
	protected Map< String, String > props;
	protected boolean temporary;

}
