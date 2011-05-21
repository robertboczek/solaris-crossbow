package org.jims.modules.crossbow.gui.validation;

import org.eclipse.swt.SWT;

import com.richclientgui.toolbox.validation.string.StringValidationToolkit;


public class ValidationToolkitFactory {
	
	public StringValidationToolkit createStringValidationToolkit() {
		return new StringValidationToolkit( DECORATOR_POSITION, DECORATOR_MARGIN_WIDTH, true );
	}
	
	
	private static final int DECORATOR_POSITION = SWT.TOP | SWT.LEFT;
	private static final int DECORATOR_MARGIN_WIDTH = 2;

}
