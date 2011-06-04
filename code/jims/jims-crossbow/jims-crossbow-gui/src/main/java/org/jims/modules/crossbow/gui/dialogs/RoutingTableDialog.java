package org.jims.modules.crossbow.gui.dialogs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jims.modules.crossbow.util.struct.Pair;


public class RoutingTableDialog extends TitleAreaDialog {
	
	class RTBridge {
		
		public void setEntries( List< Pair< String, String > > targets ) {
			for ( Pair< String, String > target : targets ) {
				TableItem item = new TableItem( table, SWT.NONE );
				item.setText( 0, target.first );
				item.setText( 1, target.second );
			}
		}
		
		public List< Pair< String, String > > getEntries() {
			
			List< Pair< String, String > > res = new LinkedList< Pair< String, String > >();
			for ( TableItem item : table.getItems() ) {
				res.add( new Pair< String, String >( item.getText( 0 ), item.getText( 1 ) ) );
			}
			
			return res;
			
		}
		
	}
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public RoutingTableDialog( List< Pair< String, String > > entries, Shell parentShell ) {
		
		super( parentShell );
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		
		this.entries = entries;
		
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {
		setTitle("Manage routing table");
		
		Composite container = ( Composite ) super.createDialogArea( parent );
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.verticalSpacing = 5;
		gridLayout.marginTop = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginLeft = 10;
		
		Composite composite_1 = new Composite( container, SWT.NONE);
		GridLayout gl_composite_1 = new GridLayout(5, false);
		gl_composite_1.marginHeight = 0;
		gl_composite_1.marginWidth = 0;
		composite_1.setLayout(gl_composite_1);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblTarget = new Label(composite_1, SWT.NONE);
		lblTarget.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTarget.setText("Target");
		
		target = new Combo(composite_1, SWT.NONE);
		target.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblGateway = new Label(composite_1, SWT.NONE);
		lblGateway.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblGateway.setText("Gateway");
		
		gateway = new Combo(composite_1, SWT.NONE);
		gateway.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnAdd = new Button(composite_1, SWT.NONE);
		btnAdd.setText("ADD");
		
		table = new Table( container, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnTarget = new TableColumn(table, SWT.NONE);
		tblclmnTarget.setWidth(260);
		tblclmnTarget.setText("Target");
		
		TableColumn tblclmnGateway = new TableColumn(table, SWT.NONE);
		tblclmnGateway.setWidth(100);
		tblclmnGateway.setText("Gateway");
		
		weave();

		return container;
	}
	

	private void weave() {
		
		table.addSelectionListener( new SelectionListener() {
			
			@Override
			public void widgetSelected( SelectionEvent e ) {
				
				String targetText = "", gatewayText = "";
				
				if ( 1 == table.getSelectionCount() ) {
					targetText = table.getSelection()[ 0 ].getText( 0 );
					gatewayText = table.getSelection()[ 0 ].getText( 1 );
				}
				
				target.setText( targetText );
				gateway.setText( gatewayText );
				
			}
			
			@Override
			public void widgetDefaultSelected( SelectionEvent e ) {}
			
		} );
		
		table.addKeyListener( new KeyListener() {
			
			@Override
			public void keyReleased( KeyEvent e ) {
				if ( SWT.DEL == e.keyCode ) {
					table.remove( table.getSelectionIndices() );
				}
			}
			
			@Override
			public void keyPressed( KeyEvent e ) {}
			
		} );
		
		getShell().addDisposeListener( new DisposeListener() {
			@Override
			public void widgetDisposed( DisposeEvent e ) {
				entries.clear();
				entries.addAll( rtBridge.getEntries() );
			}
		} );
		
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				TableItem item = new TableItem( table, SWT.NONE );
				item.setText( new String[]{ target.getText(), gateway.getText() } );
			}
		});
		
		rtBridge.setEntries( entries );
		
  }

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar( Composite parent ) {
		createButton( parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
		    true );
		createButton( parent, IDialogConstants.CANCEL_ID,
		    IDialogConstants.CANCEL_LABEL, false );
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point( 540, 411 );
	}
	
	
	private List< Pair< String, String > > entries;
	private RTBridge rtBridge = new RTBridge();
	
	private Button btnAdd;
	private Combo target;
	private Combo gateway;
	private Table table;

}
