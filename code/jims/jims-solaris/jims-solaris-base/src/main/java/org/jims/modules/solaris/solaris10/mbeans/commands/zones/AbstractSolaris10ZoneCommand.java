package org.jims.modules.solaris.solaris10.mbeans.commands.zones;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jims.model.solaris.solaris10.ZoneInfo;
import org.jims.modules.solaris.commands.AbstractSolarisCommand;

/**
 * Common functionality used by zone commands.
 * 
 * @author bombel
 * @version $Id: AbstractSolaris10ZoneCommand.java 1018 2006-02-02 10:19:22 +0100 (Thu, 02 Feb 2006) bombel $
 */
public abstract class AbstractSolaris10ZoneCommand extends AbstractSolarisCommand
{
	public final static String CREATE_ZONE_FROM_SNAPSHOT = "zone/jims_zcreate_from_clone.sh";
	public final static String CREATE_ZONE = "zone/jims_zcreate.sh";
	public final static String DELETE_ZONE = "zone/jims_zdelete.sh";
	public final static String ATTACH_INTERFACES = "zone/jims_zattach_ifaces.sh";
	public final static String CONFIGURE_INTERFACES = "zone/jims_zconfig_ifaces.sh";
	public final static String SETUP_FORWARDING = "zone/jims_zsetup_forwarding.sh";
	public final static String ROUTE_MANAGEMENT = "zone/jims_zmanage_routes.sh";

	
	public AbstractSolaris10ZoneCommand() 
	{
		super();
	}

    public String[] createNewZoneCommand(ZoneInfo zone)
    {                                        
        List<String> cmdtokenslist = new ArrayList<String>();
        
        String scriptPath = prepareJimsScriptPath(CREATE_ZONE);
        
        cmdtokenslist.add( scriptPath );
        cmdtokenslist.add( "-z" );
        cmdtokenslist.add( zone.getName() );
        cmdtokenslist.add( "-p" );
        cmdtokenslist.add( zone.getZonepath() );

        if ( null != zone.getPhysical() ) {
            cmdtokenslist.add( "-n" );
            cmdtokenslist.add( zone.getAddress() + "," + zone.getPhysical() );
        }

        cmdtokenslist.add( "-a" );
        cmdtokenslist.add( Boolean.toString( zone.isAutoboot() ) );
        //TO DO - Rctls
        
        return cmdtokenslist.toArray( new String[]{}) ;
    }
    
    public String[] createNewZoneFromSnapshotCommand(ZoneInfo zone, String srcSnapshot)
    {                                        
        List<String> cmdtokenslist = new ArrayList<String>();
                
        String scriptPath = prepareJimsScriptPath(CREATE_ZONE_FROM_SNAPSHOT);
        
        cmdtokenslist.add( scriptPath );                 
        cmdtokenslist.add( "-z" );
        cmdtokenslist.add( zone.getName() );

        if ( null != zone.getPhysical() ) {
            cmdtokenslist.add( "-n" );
            cmdtokenslist.add( zone.getAddress() + "," + zone.getPhysical() );
        }

        cmdtokenslist.add( "-a" );
        cmdtokenslist.add( Boolean.toString( zone.isAutoboot() ) );
        cmdtokenslist.add( "-l" );
        cmdtokenslist.add( zone.getZfsPool() );
        if ( zone.getPool() != null && !zone.getPool().isEmpty()) {
        	cmdtokenslist.add( "-m" );
        	cmdtokenslist.add( zone.getPool() );
        }
        cmdtokenslist.add( "-s" );
        cmdtokenslist.add( srcSnapshot );
        
        return cmdtokenslist.toArray( new String[]{}) ;
    }
    
	/**
	 * 
	 * @param zoneInfo
	 * @return
	 */
	public String[] createBootZoneCommand(String zoneName)
	{
		List<String> cmdtokenslist = new ArrayList<String>();
		
		cmdtokenslist.add("/usr/sbin/zoneadm");
		cmdtokenslist.add("-z");
		cmdtokenslist.add( zoneName );
		cmdtokenslist.add( "boot" );
		
		return cmdtokenslist.toArray( new String[]{}) ;
	}
	
	/**
	 * 
	 * @param zoneInfo
	 * @return
	 */
	public String[] createRebootZoneCommand(String zoneName)
	{
		List<String> cmdtokenslist = new ArrayList<String>();
		
		cmdtokenslist.add("/usr/sbin/zoneadm");
		cmdtokenslist.add("-z");
		cmdtokenslist.add( zoneName );
		cmdtokenslist.add( "reboot" );
		
		return cmdtokenslist.toArray( new String[]{}) ;
	}
	
	/**
	 * 
	 * @param zoneInfo
	 * @return
	 */
	public String[] createHaltZoneCommand(String zoneName)
	{
		List<String> cmdtokenslist = new ArrayList<String>();
		
		cmdtokenslist.add("/usr/sbin/zoneadm");
		cmdtokenslist.add("-z");
		cmdtokenslist.add( zoneName );
		cmdtokenslist.add( "halt" );
		
		return cmdtokenslist.toArray( new String[]{}) ;
	}
	
	/**
	 * 
	 * @param zoneInfo
	 * @return
	 */
	public String[] createReadyZoneCommand(String zoneName)
	{
		List<String> cmdtokenslist = new ArrayList<String>();
		
		cmdtokenslist.add("/usr/sbin/zoneadm");
		cmdtokenslist.add("-z");
		cmdtokenslist.add( zoneName );
		cmdtokenslist.add( "ready" );
		
		return cmdtokenslist.toArray( new String[]{}) ;
	}

	public String[] removeZoneCommand( String zoneName ) {

		List< String > cmdtokenslist = new ArrayList< String >();

		String scriptPath = prepareJimsScriptPath( DELETE_ZONE );

		cmdtokenslist.add( scriptPath );
		cmdtokenslist.add( zoneName );

		return cmdtokenslist.toArray( new String[]{} );

	}

	public String[] createAttachInterfacesCommand( String zoneName, List< String > ifaces ) {

		List< String > cmdtokenslist = new ArrayList< String >();

		String scriptPath = prepareJimsScriptPath( ATTACH_INTERFACES );

		cmdtokenslist.add( scriptPath );
		cmdtokenslist.add( "-z" );
		cmdtokenslist.add( zoneName );

		for ( String i : ifaces ) {
			cmdtokenslist.add( "-i" );
			cmdtokenslist.add( i );
		}

		return cmdtokenslist.toArray( new String[]{} );

	}

	public String[] createConfigureInterfacesCommand( String zoneName, List< String > ifaces, List< String > addresses ) {

		List< String > cmdtokenslist = new ArrayList< String >();

		String scriptPath = prepareJimsScriptPath( CONFIGURE_INTERFACES );

		cmdtokenslist.add( scriptPath );
		cmdtokenslist.add( "-z" );
		cmdtokenslist.add( zoneName );

		Iterator< String > ifaceIt = ifaces.iterator(), addressIt = addresses.iterator();

		while ( ifaceIt.hasNext() ) {
			cmdtokenslist.add( "-c" );
			cmdtokenslist.add( ifaceIt.next() + ":" + addressIt.next() );
		}

		return cmdtokenslist.toArray( new String[]{} );

	}

	public String[] createSetupForwardingCommand( String zoneName, boolean enabled ) {

		List< String > cmdtokenslist = new ArrayList< String >();

		String scriptPath = prepareJimsScriptPath( SETUP_FORWARDING );

		cmdtokenslist.add( scriptPath );
		cmdtokenslist.add( "-z" );
		cmdtokenslist.add( zoneName );
		cmdtokenslist.add( enabled ? "up" : "down" );

		return cmdtokenslist.toArray( new String[]{} );

	}

	public String[] createRouteAddCommand( String zoneName, List< String > dests, List< String > gateways ) {

		List< String > cmdtokenslist = new ArrayList< String >();

		cmdtokenslist.add( prepareJimsScriptPath( ROUTE_MANAGEMENT ) );

		cmdtokenslist.add( "-z" );
		cmdtokenslist.add( zoneName );

		Iterator< String > dit = dests.iterator();
		Iterator< String > git = gateways.iterator();

		while ( dit.hasNext() && git.hasNext() ) {
			cmdtokenslist.add( "-a" );
			cmdtokenslist.add( dit.next() + ":" + git.next() );
		}

		return cmdtokenslist.toArray( new String[]{} );

	}

}
