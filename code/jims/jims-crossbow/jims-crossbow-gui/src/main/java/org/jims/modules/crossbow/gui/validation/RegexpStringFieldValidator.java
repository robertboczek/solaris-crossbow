package org.jims.modules.crossbow.gui.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.richclientgui.toolbox.validation.validator.IFieldValidator;


public class RegexpStringFieldValidator implements IFieldValidator< String > {
	
	public static class Descriptor {
		
		public Descriptor( String pattern, String warn, String error ) {
			this.pattern = pattern;
			this.warn = warn;
			this.error = error;
		}
		
		private String pattern, warn, error;
		
	}
	
	
	public RegexpStringFieldValidator( Descriptor descriptor ) {
		this( Pattern.compile( descriptor.pattern ), descriptor.warn, descriptor.error );
	}
	
	public RegexpStringFieldValidator( String pattern, String warn, String error ) {
		this( Pattern.compile( pattern ), warn, error );
	}
	
	public RegexpStringFieldValidator( Pattern pattern, String warn, String error ) {
		this.pattern = pattern;
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
		Matcher matcher = pattern.matcher( input );
		return matcher.matches();
  }

	@Override
  public boolean warningExist( String arg0 ) {
	  // TODO Auto-generated method stub
	  return false;
  }
	
	
	Pattern pattern;
	
	private String warn;
	private String error;

}
