package org.jims.modules.crossbow.gui.validation;

import org.jims.modules.crossbow.infrastructure.validation.EntityValidatorFactory;

import com.richclientgui.toolbox.validation.validator.IFieldValidator;


public class FieldValidatorFactory {
	
	public FieldValidatorFactory( EntityValidatorFactory entityValidatorFactory ) {
		this.entityValidatorFactory = entityValidatorFactory;
	}
	
	
	public IFieldValidator< String > createNameValidator( Class< ? > entity ) {
		return createNameValidator( entity, "", "" );
	}
	
	
	public IFieldValidator< String > createNameValidator( Class< ? > entity, String warn, String error ) {
		return new ValidatorStringFieldValidator(
			entityValidatorFactory.createEntityNameValidator( entity ),
			warn, error
		);
	}
	
	
	private EntityValidatorFactory entityValidatorFactory;

}
