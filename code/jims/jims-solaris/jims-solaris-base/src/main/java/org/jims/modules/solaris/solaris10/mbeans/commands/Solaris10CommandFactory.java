package org.jims.modules.solaris.solaris10.mbeans.commands;

import org.jims.agent.exception.CommandException;
import org.jims.agent.exception.JimsManagementException;
import org.jims.modules.solaris.commands.CreateProjectCommand;
import org.jims.modules.solaris.commands.CreateZoneCommand;
import org.jims.modules.solaris.commands.CreateZoneFromSnapshotCommand;
import org.jims.modules.solaris.commands.FsCommand;
import org.jims.modules.solaris.commands.GetProjectCommand;
import org.jims.modules.solaris.commands.GetProjectsCommand;
import org.jims.modules.solaris.commands.GetSolarisResourceControlsCommand;
import org.jims.modules.solaris.commands.GetZoneCommand;
import org.jims.modules.solaris.commands.GetZoneNameCommand;
import org.jims.modules.solaris.commands.GetZonesCommand;
import org.jims.modules.solaris.commands.ModifyProjectCommand;
import org.jims.modules.solaris.commands.ModifyZoneCommand;
import org.jims.modules.solaris.commands.RemoveProjectCommand;
import org.jims.modules.solaris.commands.RemoveZoneCommand;
import org.jims.modules.solaris.commands.SolarisCommandFactory;
import org.jims.modules.solaris.commands.SwitchExacctCommand;
import org.jims.modules.solaris.commands.fs.SolarisFsCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.exacct.Solaris10SwitchExacctCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.projects.CreateSolaris10ProjectCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.projects.GetSolaris10Project;
import org.jims.modules.solaris.solaris10.mbeans.commands.projects.GetSolaris10ProjectsCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.projects.RemoveSolaris10ProjectCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.projects.UpdateSolaris10Project;
import org.jims.modules.solaris.solaris10.mbeans.commands.rctl.GetSolaris10ResourceControlsCommand;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.CreateZoneFromSnapshotSolaris10Command;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.GetZoneNameSolaris10Command;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.GetZoneSolaris10Command;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.GetZonesSolaris10Command;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.ModifyZoneSolaris10Command;
import org.jims.modules.solaris.solaris10.mbeans.commands.zones.RemoveZoneSolaris10Command;

/**
 * Command factory for Solaris 10 management operations both on projects and zones.
 * Each operation has its own implementation in the form of Command. 
 * 
 * @author bombel
 * @version $Id: Solaris10CommandFactory.java 1328 2006-03-20 15:10:00 +0100 (Mon, 20 Mar 2006) pmvm05b $
 */
public class Solaris10CommandFactory extends SolarisCommandFactory
{
	public Solaris10CommandFactory() 
	{
		super();
	}

	@Override
	public CreateProjectCommand getCreateProjectCommand() throws CommandException
	{	
		try {
			return new CreateSolaris10ProjectCommand();
		} catch (JimsManagementException e) {
			throw new CommandException(e);
		}
	}

	@Override
	public CreateZoneCommand getCreateZoneCommand() throws CommandException 
	{
		return null;
	}

	public CreateZoneFromSnapshotCommand getCreateZoneFromSnapshotCommand() throws CommandException
	{
		try {
			return new CreateZoneFromSnapshotSolaris10Command();
		} catch (JimsManagementException e) {
			throw new CommandException(e);	
		}
	}
	
	public ModifyProjectCommand getModifyProjectCommand() throws CommandException 
	{
		try {
			return new UpdateSolaris10Project();
		} catch (JimsManagementException e) {
			throw new CommandException(e);	
		}
	}

	@Override
	public ModifyZoneCommand getModifyZoneCommand() throws CommandException 
	{
		return new ModifyZoneSolaris10Command();
	}

	@Override
	public GetProjectCommand getProjectCommand() throws CommandException 
	{
		try {
			return new GetSolaris10Project();
		} catch (JimsManagementException e) {
			throw new CommandException(e);
		}
	}

	public GetProjectsCommand getProjectsCommand() throws CommandException 
	{
		try {
			return new GetSolaris10ProjectsCommand();
		} catch (JimsManagementException e) {
			throw new CommandException(e);
		}
	}

	public RemoveProjectCommand getRemoveProjectCommand() throws CommandException 
	{
		try 
		{
			return new RemoveSolaris10ProjectCommand();
		} 
		catch (JimsManagementException e) 
		{
			throw new CommandException(e);
		}
	}

	public RemoveZoneCommand getRemoveZoneCommand() throws CommandException 
	{
		return new RemoveZoneSolaris10Command();
	}

	@Override
	public GetZoneCommand getZoneCommand() throws CommandException 
	{
		return new GetZoneSolaris10Command();
	}

	@Override
	public GetZonesCommand getZonesCommand() throws CommandException 
	{	
		return new GetZonesSolaris10Command();
	}
	
	public GetZoneNameCommand getZoneNameCommand() throws CommandException
	{
		return new GetZoneNameSolaris10Command();
	}

	@Override
	public SwitchExacctCommand getSwitchExacctCommand() throws CommandException {
		return new Solaris10SwitchExacctCommand();
	}

	
	@Override
	public FsCommand getFsCommand() throws CommandException
	{	
		return new SolarisFsCommand();
	}

	@Override
	public GetSolarisResourceControlsCommand getSolarisResourceControlsCommand() throws CommandException {
		return new GetSolaris10ResourceControlsCommand();
	}
}
