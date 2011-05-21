package org.jims.modules.crossbow.gui.validation;

import org.jims.modules.crossbow.util.validation.Validator;

import com.richclientgui.toolbox.validation.validator.IFieldValidator;


public class ValidatorStringFieldValidator implements IFieldValidator< String > {
	
	public static class Descriptor {
		
		public Descriptor( Validator< String > validator, String warn, String error ) {
			this.validator = validator;
			this.warn = warn;
			this.error = error;
		}
		
		private Validator< String > validator;
		private String warn, error;
		
	}
	
	
	public ValidatorStringFieldValidator( Descriptor descriptor ) {
		this( descriptor.validator, descriptor.warn, descriptor.error );
	}
	
	public ValidatorStringFieldValidator( Validator< String > validator, String warn, String error ) {
		this.validator = validator;
		this.warn = warn;
		this.error = error;
  }


	@Override
  public String getErrorMessage() {
		return error;
  }

	@Override
  public String getWarningMessage() {
		return warn;
  }

	@Override
  public boolean isValid( String input ) {
		return validator.isValid( input );
  }

	@Override
  public boolean warningExist( String arg0 ) {
	  // TODO Auto-generated method stub
	  return false;
  }
	
	
	Validator< String > validator;
	
	private String warn;
	private String error;

}
