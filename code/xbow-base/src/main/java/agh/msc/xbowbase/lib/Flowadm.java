package agh.msc.xbowbase.lib;

import agh.msc.xbowbase.exception.ValidationException;
import java.util.Map;


/**
 *
 * @author cieplik
 */
public interface Flowadm {

	/* Flow management methods. */

	public String[] getNames();

	public int remove( String flow );


	/* Flow details management methods. */

	void setAttributes( String flowName, Map< String, String > attributes ) throws ValidationException;

	Map< String, String > getAttributes( String flowName );

	void setProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException;

	Map< String, String > getProperties( String flowName );

	void resetProperties( String flowName, Map< String, String > properties, boolean temporary ) throws ValidationException;

}
