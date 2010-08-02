package agh.msc.xbowbase.flow;


/**
 *
 * @author cieplik
 */
public interface Publisher {

	public void publish( Object object );

	public void unpublish( Object object );

}
