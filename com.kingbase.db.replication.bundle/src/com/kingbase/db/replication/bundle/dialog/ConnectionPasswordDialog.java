package com.kingbase.db.replication.bundle.dialog;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

public class ConnectionPasswordDialog extends Dialog {
	private Shell shell;
	private Object object = null;
	private Connection sourceCon = null;
	private ReleaseDataSource releaseSource;
	private SubscribeDataSource subscribeSource;
	private String dbName = ""; //$NON-NLS-1$
	private Text usernameT;
	private Text passwordT;
	private Button btnIsSave;
	private FormToolkit toolkit;

	public ConnectionPasswordDialog(Shell shell, ReleaseDataSource releaseSource, SubscribeDataSource subscribeSource) {
		super(shell);
		this.shell = shell;
		this.subscribeSource = subscribeSource;
		this.releaseSource = releaseSource;
		if(releaseSource!=null){
			dbName = releaseSource.getDbUser();
		}
		if(subscribeSource!=null){
			dbName = subscribeSource.getDbUser();
		}
		createContents();
	}

	public Object open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return object;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText("\"" + dbName + Messages.ConnectionPasswordDialog_Authentication); //$NON-NLS-1$ 
		shell.setImage(ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.replication));
		final GridLayout gridLayout_31 = new GridLayout();

		gridLayout_31.marginLeft = 10;
		shell.setLayout(gridLayout_31);
		shell.setSize(310,200);
		if (shell != null) {// 居中
			Monitor[] monitorArray = Display.getCurrent().getMonitors();
			Rectangle rectangle = monitorArray[0].getBounds();
			Point size = shell.getSize();
			shell.setLocation((rectangle.width - size.x) / 2, (rectangle.height - size.y) * 2 / 5);
		}
		shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		IManagedForm managedForm = new ManagedForm(shell);
		toolkit = managedForm.getToolkit();
		final ScrolledForm form = managedForm.getForm();
		GridData data1 = new GridData(GridData.FILL_BOTH);
		form.setLayoutData(data1);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		toolkit.decorateFormHeading(form.getForm());

		Group group = new Group(form.getBody(), SWT.NONE);
		GridLayout layout1 = new GridLayout(2,false);
		layout1.marginLeft = 10;
		layout1.marginRight = 7;
		layout1.marginTop = 5;
		group.setLayout(layout1);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setText(Messages.ConnectionPasswordDialog_User_Credentials);
		group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Label label = toolkit.createLabel(group,Messages.ConnectionPasswordDialog_Username, SWT.NONE);
		GridData label_gl = new GridData();
		label.setLayoutData(label_gl);
		
		usernameT = toolkit.createText(group, dbName, SWT.BORDER);
		usernameT.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      		
		Label label1 = toolkit.createLabel(group,Messages.ConnectionPasswordDialog_Password, SWT.NONE);
		GridData label_gl1 = new GridData();
		label1.setLayoutData(label_gl1);
		
		passwordT = toolkit.createText(group, "", SWT.BORDER|SWT.PASSWORD);
		passwordT.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		passwordT.setFocus();
		
		btnIsSave = toolkit.createButton(form.getBody(), Messages.ConnectionPasswordDialog_Save_password, SWT.CHECK);
		btnIsSave.setSelection(true);

		Composite compOpera = new Composite(form.getBody(), SWT.NONE);
		compOpera.setLayout(new GridLayout(3, false));
		GridData data11 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data11);
		Label label11 = toolkit.createLabel(compOpera, "", SWT.None);
		data11 = new GridData(GridData.FILL_HORIZONTAL);
		data11.horizontalSpan = 1;
		label11.setLayoutData(data11);
		compOpera.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		final Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText(Messages.ConnectionPasswordDialog_btnConfirm);
		data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data11);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (releaseSource != null) {
					releaseSource.setDbUser(usernameT.getText());
					releaseSource.setDbPasswrod(passwordT.getText());
					releaseSource.setSaveP(btnIsSave.getSelection());
					sourceCon = DatabaseUtil.getConnection(releaseSource, UIUtils.getDatabase()); //$NON-NLS-1$
					object = releaseSource;
				}
				if (subscribeSource != null) {
					subscribeSource.setDbUser(usernameT.getText());
					subscribeSource.setDbPasswrod(passwordT.getText());
					subscribeSource.setSaveP(btnIsSave.getSelection());
					sourceCon = DatabaseUtil.getConnection(subscribeSource, UIUtils.getDatabase()); //$NON-NLS-1$
					object = subscribeSource;
				}
				if (sourceCon == null) {
					MessageDialog.openError(UIUtils.getActiveShell(), "Error", Messages.ConnectionPasswordDialog_user_and_password_is_not_match); //$NON-NLS-1$
					return;
				}
				if (sourceCon != null) {
					try {
						sourceCon.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				shell.dispose();
			}
		});

		final Button btnCanel = new Button(compOpera, SWT.PUSH);
		btnCanel.setText(Messages.ConnectionPasswordDialog_btnCanel);
		data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCanel.setLayoutData(data11);
		((GridData) btnCanel.getLayoutData()).widthHint = 61;

		btnCanel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				if (sourceCon != null) {
//					try {
//						sourceCon.close();
//					} catch (SQLException e1) {
//						e1.printStackTrace();
//					}
//				}
				shell.dispose();
			}
		});
	}

}
