package org.jims.modules.solaris.solaris10.mbeans.commands.zones;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jims.agent.exception.CommandException;
import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.solaris.commands.ModifyZoneCommand;

public class ModifyZoneSolaris10Command extends AbstractSolaris10ZoneCommand
		implements ModifyZoneCommand {

	private final static Logger logger = Logger.getLogger( ModifyZoneSolaris10Command.class );
	
	public ModifyZoneSolaris10Command() 
	{
		super();
	}

	public void modifyZone(ZoneInfo zoneInfo) throws CommandException 
	{
		
	}

	public void bootZone(String zoneName) throws CommandException 
	{		
		String[] cmdarray = this.createBootZoneCommand(zoneName);
		this.invokeOsCommand(cmdarray);
		
	}

	public void readyZone(String zoneName) throws CommandException 
	{
		String[] cmdarray = this.createReadyZoneCommand(zoneName);
		this.invokeOsCommand(cmdarray);
	}

	public void rebootZone(String zoneName) throws CommandException 
	{
		String[] cmdarray = this.createRebootZoneCommand(zoneName);
		this.invokeOsCommand(cmdarray);
	}

	public void shutdownZone(String zoneName) throws CommandException 
	{
		String[] cmdarray = this.createHaltZoneCommand(zoneName);
		this.invokeOsCommand(cmdarray);
	}

	@Override
	public void attachInterfaces( String zoneName, List< String > ifaces )
		throws CommandException {

		String[] cmdarray = this.createAttachInterfacesCommand( zoneName, ifaces );
		this.invokeOsCommand( cmdarray );

	}

	@Override
	public void configureInterfaces( String zoneName, List< String > ifaces, List< String > addresses )
		throws CommandException {

		String[] cmdarray = this.createConfigureInterfacesCommand( zoneName, ifaces, addresses );
		this.invokeOsCommand( cmdarray );

	}

	@Override
	public void setupForwarding( String zoneName, boolean enabled ) throws CommandException {

		String[] cmdarray = this.createSetupForwardingCommand( zoneName, enabled );
		this.invokeOsCommand( cmdarray );

	}

	@Override
	public void routeAdd( String zoneName, List< String > dests, List< String > gateways ) throws CommandException {

		String[] cmdarray = this.createRouteAddCommand( zoneName, dests, gateways );
		this.invokeOsCommand( cmdarray );

	}

	@Override
	public Map< String, String > readRoutes( String zoneName ) throws CommandException {

		Map< String, String > res = new HashMap< String, String >();

		String cmdarray[] = this.createReadRoutesCommand( zoneName );

		for ( String line : this.invokeOsCommand( cmdarray ) ) {

			String parts[] = line.split( ":" );
			res.put( parts[ 0 ], parts[ 1 ] );

		}

		return res;

	}

}
