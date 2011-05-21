package org.jims.modules.crossbow.infrastructure.validation.impl;

import java.security.Policy;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jims.modules.crossbow.infrastructure.validation.EntityValidatorFactory;
import org.jims.modules.crossbow.objectmodel.resources.Appliance;
import org.jims.modules.crossbow.objectmodel.resources.Interface;
import org.jims.modules.crossbow.objectmodel.resources.Switch;
import org.jims.modules.crossbow.util.validation.Validator;


/**
 *
 * @author cieplik
 */
public class SolarisEntityValidatorFactory implements EntityValidatorFactory {

	static class NameValidator implements Validator< String > {

		public NameValidator( String regex ) {
			this.pattern = Pattern.compile( regex );
		}

		@Override
		public boolean isValid( String object ) {
			return pattern.matcher( object ).matches();
		}

		Pattern pattern;

	}


	@Override
	public Validator< String > createEntityNameValidator( Class< ? > klass ) {
		return nameValidators.get( klass );
	}


	private static final Map< Class< ? >, Validator< String > > nameValidators
		= new HashMap< Class< ? >, Validator< String > >();

	static {
		nameValidators.put( Appliance.class, new NameValidator( "[a-zA-Z][.a-zA-Z0-9]*" ) );
		nameValidators.put( Switch.class, new NameValidator( "[a-zA-Z][.a-zA-Z0-9]*\\d" ) );
		nameValidators.put( Interface.class, new NameValidator( "[a-zA-Z][.a-zA-Z0-9]*\\d" ) );
		nameValidators.put( Policy.class, new NameValidator( "[a-zA-Z][.a-zA-Z0-9]*" ) );
	}

}
