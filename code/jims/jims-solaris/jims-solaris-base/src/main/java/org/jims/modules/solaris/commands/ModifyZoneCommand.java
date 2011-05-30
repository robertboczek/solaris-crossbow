package org.jims.modules.solaris.commands;

import java.util.List;
import java.util.Map;
import org.jims.agent.exception.CommandException;
import org.jims.model.solaris.solaris10.ZoneInfo;

/**
 * 
 * @author bombel
 * @version $Id: ModifyZoneCommand.java 514 2005-11-16 13:44:09 +0100 (Wed, 16 Nov 2005) bombel $
 */
public interface ModifyZoneCommand 
{
	public void modifyZone(ZoneInfo zoneInfo) throws CommandException;
	
	public void bootZone(String zoneName) throws CommandException;
	public void rebootZone(String zoneName) throws CommandException;
	public void shutdownZone(String zoneName) throws CommandException;
	public void readyZone(String zoneName) throws CommandException;

	public void attachInterfaces( String zoneName, List< String > ifaces ) throws CommandException;
	public void configureInterfaces( String zoneName, List< String > ifaces, List< String > addresses ) throws CommandException;
	public void setupForwarding( String zoneName, boolean enabled ) throws CommandException;
	public void routeAdd( String zoneName, List< String > dests, List< String > gateways ) throws CommandException;
	public Map< String, String > readRoutes( String zoneName ) throws CommandException;

}
