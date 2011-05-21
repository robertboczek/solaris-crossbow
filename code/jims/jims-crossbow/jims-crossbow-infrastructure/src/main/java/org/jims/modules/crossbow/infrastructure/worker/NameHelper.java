package org.jims.modules.crossbow.infrastructure.worker;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jims.modules.crossbow.objectmodel.policy.Policy;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.ApplianceType;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.util.struct.Pair;


/**
 *
 * @author cieplik
 */
public class NameHelper {

	public enum NamePart {
		ALL,
		PROJECT,
		APPLIANCE_TYPE,
		APPLIANCE,
		SWITCH,
		INTERFACE,
		POLICY
	}


	public static String interfaceName( Interface iface ) {
		return ( ApplianceType.ROUTER.equals( iface.getAppliance().getType() )
		         ? routerName( iface.getAppliance() )
		         : machineName( iface.getAppliance() ) )
		       + SEP + iface.getResourceId();
	}

	public static String policyName( Policy p ) {
		return interfaceName( p.getInterface() ) + SEP + p.getName();
	}

	public static String switchName( Switch s ) {
		return s.getProjectId() + SEP + SWITCH + s.getResourceId();
	}

	public static String machineName( Appliance a ) {
		return a.getProjectId() + SEP + MACHINE + a.getResourceId();
	}

	public static String routerName( Appliance a ) {
		return a.getProjectId() + SEP + ROUTER + a.getResourceId();
	}


	public static Map< NamePart, String > splitAppliance( String name ) {
		return split( name,
		              APPLIANCE_PATTERN_CG,
		              Arrays.asList( Pair.create( NamePart.PROJECT, 1 ),
		                             Pair.create( NamePart.APPLIANCE_TYPE, 2 ),
		                             Pair.create( NamePart.APPLIANCE, 3 ) ) );
	}

	Map< NamePart, String > splitSwitch( String name ) {
		return split( name,
		              SWITCH_PATTERN_CG,
		              Arrays.asList( Pair.create( NamePart.PROJECT, 1 ),
		                             Pair.create( NamePart.SWITCH, 2 ) ) );
	}

	Map< NamePart, String > splitPolicy( String name ) {
		return split( name,
		              POLICY_PATTERN_CG,
		              Arrays.asList( Pair.create( NamePart.PROJECT, 1 ),
		                             Pair.create( NamePart.APPLIANCE_TYPE, 2 ),
		                             Pair.create( NamePart.APPLIANCE, 3 ),
		                             Pair.create( NamePart.INTERFACE, 4 ),
		                             Pair.create( NamePart.POLICY, 5 ) ) );
	}

	Map< NamePart, String > splitInterface( String name ) {
		return split( name,
		              INTERFACE_PATTERN_CG,
		              Arrays.asList( Pair.create(  NamePart.PROJECT, 1 ),
		                             Pair.create(  NamePart.APPLIANCE_TYPE, 2 ),
		                             Pair.create(  NamePart.APPLIANCE, 3 ),
		                             Pair.create(  NamePart.INTERFACE, 4 ) ) );
	}




	private static Map< NamePart, String > split( String name, Pattern pattern, List< Pair< NamePart, Integer > > mappings ) {

		Map< NamePart, String > res = null;

		Matcher matcher = pattern.matcher( name );

		if ( matcher.matches() ) {
			res = new EnumMap< NamePart, String >( NamePart.class );
			res.put( NamePart.ALL, matcher.group() );

			for ( Pair< NamePart, Integer > mapping : mappings ) {
				res.put( mapping.first, matcher.group( mapping.second ) );
			}
		}

		return res;

	}


	public static String extractAppliance( String s ) {

		logger.debug( "Extracting appliance (input string: " + s + ")" );

		Pattern p = Pattern.compile( cg( REG_APPLIANCE_NAME ) + REG_SEP + REG_ANY );
		Matcher m = p.matcher( s );

		return ( m.matches() ? m.group( 1 ) : "" );

	}


	public static Object extractInterface( String s ) {

		logger.debug( "Extracting interface (input string: " + s + ")" );

		Pattern p = Pattern.compile( cg( REG_INTERFACE_NAME ) + REG_SEP + REG_ANY );
		Matcher m = p.matcher( s );

		return ( m.matches() ? m.group( 1 ) : "" );

	}


	private static String cg( String s ) {
		return "(" + s + ")";
	}

	private static String g( String s ) {
		return "(?:" + s + ")";
	}


	public final static char SEP = '_';
	public final static String ROUTER = "R";
	private final static String MACHINE = "M";
	private final static String SWITCH = "S";

	private final static String REG_SEP = Pattern.quote( Character.toString( SEP ) );
	private final static String REG_PROJECT_ID = "[a-zA-Z][_.a-zA-Z0-9&&[^" + SEP + "]]*";
	private final static String REG_RESOURCE_ID = "[a-zA-Z][_.a-zA-Z0-9&&[^" + SEP + "]]*";
	private final static String REG_SWITCH_ID = "[a-zA-Z][_.a-zA-Z0-9&&[^" + SEP + "]]*\\d";
	private final static String REG_ANY = ".*";

	private final static String REG_SWITCH_NAME
		= g( REG_PROJECT_ID + REG_SEP + SWITCH + REG_SWITCH_ID );
	private final static String REG_SWITCH_NAME_CG
		= g( cg( REG_PROJECT_ID ) + REG_SEP + SWITCH + cg( REG_SWITCH_ID ) );

	private final static String REG_MACHINE_NAME
		= g( REG_PROJECT_ID + REG_SEP + MACHINE + REG_RESOURCE_ID );
	private final static String REG_MACHINE_NAME_CG
		= g( cg( REG_PROJECT_ID ) + REG_SEP + cg( MACHINE ) + cg( REG_RESOURCE_ID ) );

	private final static String REG_ROUTER_NAME
		= g( REG_PROJECT_ID + REG_SEP + ROUTER + REG_RESOURCE_ID );
	private final static String REG_ROUTER_NAME_CG
		= g( cg( REG_PROJECT_ID ) + REG_SEP + cg( ROUTER ) + cg( REG_RESOURCE_ID ) );

	private final static String REG_APPLIANCE_NAME
		= g( REG_MACHINE_NAME + "|" + REG_ROUTER_NAME );
	private final static String REG_APPLIANCE_NAME_CG
		= g( cg( REG_PROJECT_ID ) + REG_SEP
		  + cg( ROUTER + "|" + MACHINE ) + cg( REG_RESOURCE_ID ) );

	private final static String REG_INTERFACE_NAME
		= g( REG_APPLIANCE_NAME + REG_SEP + REG_RESOURCE_ID );
	private final static String REG_INTERFACE_NAME_CG
		= g( cg( REG_PROJECT_ID ) + REG_SEP + cg( ROUTER + "|" + MACHINE )
		  + cg( REG_RESOURCE_ID ) + REG_SEP + cg( REG_RESOURCE_ID ) );

	private final static String REG_POLICY_NAME_CG
		= g( REG_INTERFACE_NAME_CG + REG_SEP + cg( REG_RESOURCE_ID ) );

	private static final Pattern APPLIANCE_PATTERN_CG = Pattern.compile( REG_APPLIANCE_NAME_CG );
	private static final Pattern SWITCH_PATTERN_CG = Pattern.compile( REG_SWITCH_NAME_CG );
	private static final Pattern INTERFACE_PATTERN_CG = Pattern.compile( REG_INTERFACE_NAME_CG );
	private static final Pattern POLICY_PATTERN_CG = Pattern.compile( REG_POLICY_NAME_CG );

	private static final Logger logger = Logger.getLogger( NameHelper.class );

}
