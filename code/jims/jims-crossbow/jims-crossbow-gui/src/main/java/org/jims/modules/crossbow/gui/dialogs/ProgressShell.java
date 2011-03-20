package org.jims.modules.crossbow.gui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ProgressShell extends Shell {
	
	private static final int MAX_VALUE = 100;

	/**
	 * Create the shell.
	 * @param display
	 */
	public ProgressShell(Display display) {
		super(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Deploying progress");
		
		ProgressBar progressBar = new ProgressBar(this, SWT.SMOOTH);
		progressBar.setMinimum(0);
		progressBar.setMaximum(MAX_VALUE);
		progressBar.setSelection(40);
		progressBar.setLocation(30, 50);
		progressBar.setSize(200, 30);
		
		Label logLabel = new Label(this, SWT.NONE);
		logLabel.setText("Szczegó³y: ");
		logLabel.setLocation(30, 95);
		logLabel.setSize(50, 15);
		
		Text logsText = new Text(this, SWT.V_SCROLL | SWT.VERTICAL | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL);
		logsText.setText("dsfasdf\n fsdsdf\ndsfasdf\n fsdsdf\ndsfasdf\n fsdsdf\ndsfasdf\n fsdsdf\ndsfasdf\n fsdsdf\ndsfasdf\n fsdsdf\n");
		logsText.setLocation(25, 120);
		logsText.setSize(250, 80);

	}

	@Override
	protected void checkSubclass() {
		
	}

}
