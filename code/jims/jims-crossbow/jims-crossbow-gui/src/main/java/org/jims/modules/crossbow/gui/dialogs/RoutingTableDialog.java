package org.jims.modules.crossbow.gui.dialogs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jims.modules.crossbow.util.struct.Pair;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionAdapter;


public class RoutingTableDialog extends Dialog {
	
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

	protected Object result;
	protected Shell shlManageRoutingTable;
	private Table table;
	private RTBridge rtBridge = new RTBridge();
	private List< Pair< String, String > > entries;
	private Combo target;
	private Combo gateway;
	private Button btnOk;
	private Button btnAdd;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RoutingTableDialog( Shell parent, int style, List< Pair< String, String > > entries ) {
		super(parent, style);
		setText("SWT Dialog");
		
		this.entries = entries;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		init();
		shlManageRoutingTable.open();
		shlManageRoutingTable.layout();
		Display display = getParent().getDisplay();
		while (!shlManageRoutingTable.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void init() {
		
		rtBridge.setEntries( entries );
		
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
		
		shlManageRoutingTable.addDisposeListener( new DisposeListener() {
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
		
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlManageRoutingTable.close();
			}
		});
		
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlManageRoutingTable = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shlManageRoutingTable.setSize(357, 286);
		shlManageRoutingTable.setText("Manage routing table");
		shlManageRoutingTable.setLayout(new FormLayout());
		
		Composite composite = new Composite(shlManageRoutingTable, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		FormData fd_composite = new FormData();
		fd_composite.top = new FormAttachment(0, 10);
		fd_composite.left = new FormAttachment(0, 8);
		fd_composite.right = new FormAttachment(100, -10);
		composite.setLayoutData(fd_composite);
		
		btnOk = new Button(shlManageRoutingTable, SWT.NONE);
		fd_composite.bottom = new FormAttachment(btnOk, -10);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
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
		
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnTarget = new TableColumn(table, SWT.NONE);
		tblclmnTarget.setWidth(165);
		tblclmnTarget.setText("Target");
		
		TableColumn tblclmnGateway = new TableColumn(table, SWT.NONE);
		tblclmnGateway.setWidth(100);
		tblclmnGateway.setText("Gateway");
		FormData fd_btnOk = new FormData();
		fd_btnOk.width = 75;
		fd_btnOk.right = new FormAttachment(100, -10);
		fd_btnOk.bottom = new FormAttachment(100, -10);
		btnOk.setLayoutData(fd_btnOk);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(shlManageRoutingTable, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		fd_btnCancel.width = 75;
		fd_btnCancel.bottom = new FormAttachment(btnOk, 0, SWT.BOTTOM);
		fd_btnCancel.right = new FormAttachment(btnOk, -6);
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");

	}
}
