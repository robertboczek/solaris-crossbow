package org.jims.modules.crossbow.infrastructure.validation;

import org.jims.modules.crossbow.util.validation.Validator;


/**
 *
 * @author cieplik
 */
public interface EntityValidatorFactory {

	Validator< String > createEntityNameValidator( Class< ? > klass );

}
