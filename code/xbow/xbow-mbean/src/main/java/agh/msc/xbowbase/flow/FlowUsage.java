package agh.msc.xbowbase.flow;

import java.util.Date;


/**
 *
 * @author cieplik
 */
public class FlowUsage {

	public FlowUsage() {

	}

	public void FlowUsage( Date date, int iPackets, int rBytes, int iErrors,
	                       int oPackets, int oBytes, int oErrors ) {

	}

	public Date getDate() {
		return null;
	}

	public int getIPackets() {
		return 0;
	}

	public int getRBytes() {
		return 0;
	}

	public int getIErrors() {
		return 0;
	}

	public int getOPackets() {
		return 0;
	}

	public int getOBytes() {
		return 0;
	}

	public int getOErrors() {
		return 0;
	}

	protected Date date;
	protected int iPackets;
	protected int rBytes;
	protected int iErrors;
	protected int oPackets;
	protected int oBytes;
	protected int oErrors;

}
