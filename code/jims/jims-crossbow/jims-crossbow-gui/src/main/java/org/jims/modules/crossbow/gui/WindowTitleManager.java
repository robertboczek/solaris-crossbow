package org.jims.modules.crossbow.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jims.modules.crossbow.gui.threads.ConnectionTester;


public class WindowTitleManager implements ConnectionTester.ConnectionStatusListener {
	
	public static enum ControlType {
		NORMAL,
		DISCONNECT_ONLY
	}
	
	public WindowTitleManager( Shell window, Display display ) {
		this( window, new HashMap< Control, ControlType >(), display );
	}
	
	public WindowTitleManager( Shell window, Map< Control, ControlType > controls, Display display ) {
		this.window = window;
		this.display = display;
		this.controls = controls;
	}
	

	@Override
	public void connected( final String server ) {
		
		display.asyncExec( new Runnable() {
			public void run() {
				
				window.setText( "Connected to " + server );
				
				for ( Map.Entry< Control, ControlType > entry : controls.entrySet() ) {
					if ( ControlType.NORMAL.equals( entry.getValue() ) ) {
						entry.getKey().setEnabled( true );
					}
				}
					
			}
		} );
	
	}


	@Override
	public void disconnected( String server ) {
		
		display.asyncExec( new Runnable() {
			public void run() {
				
				window.setText( "Disconnected" );
				
				for ( Control control : controls.keySet() ) {
					control.setEnabled( false );
				}
					
			}
		} );
		
	}
	
	
	public void setControls( Map< Control, ControlType > controls ) {
		this.controls = controls;
	}
	
	
	private Map< Control, ControlType > controls;
	private Shell window;
	private Display display;
	
}
