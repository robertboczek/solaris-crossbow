package agh.msc.xbowbase.flow;

import java.util.List;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public interface FlowMBean {

	public String getName();

	public String getLink();

	public void setAttributes( Map< String, String > attributes );

	public Map< String, String > getAttributes();

	public void setProperties( Map< String, String > properties, boolean temporary );

	public Map< String, String > getProperties();

	public void resetProperties( List< String > properties, boolean temporary );

	public boolean isTemporary();

}
