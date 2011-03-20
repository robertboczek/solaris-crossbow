package org.jims.modules.solaris.solaris10.mbeans.commands.zones;

import org.jims.agent.exception.CommandException;
import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.solaris.commands.RemoveZoneCommand;

public class RemoveZoneSolaris10Command extends AbstractSolaris10ZoneCommand
		implements RemoveZoneCommand {

	public RemoveZoneSolaris10Command() 
	{
		super();
	}

	public void removeZone(ZoneInfo zoneInfo) throws CommandException 
	{
		final String[] cmdarray = this.removeZoneCommand( zoneInfo.getName() );

		this.invokeOsCommand( cmdarray );
	}

}
