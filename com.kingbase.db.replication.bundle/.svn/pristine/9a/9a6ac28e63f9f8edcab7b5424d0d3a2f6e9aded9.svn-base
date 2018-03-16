package com.kingbase.db.replication.bundle.dialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.model.tree.SubscribeTable;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

public class ThisSubscribeDialog extends Dialog{
	private Shell shell;
	private int returnCode = IDialogConstants.CANCEL_ID;
	private Connection sourceCon = null;
	private SubscribeDataInfo sourceMeta;
	private SubscribeTable tableMeta;
	private String type;
	private Button btnClear;

	public ThisSubscribeDialog(Shell shell, SubscribeDataInfo sourceMeta, String type) {
		super(shell);
		this.shell = shell;
		this.sourceMeta = sourceMeta;
		this.type = type;
		// 组件布局
		sourceCon = DatabaseUtil.getConnection((SubscribeDataSource) sourceMeta.getParentModel().getParentModel(),
				((SubscribeDataBase) sourceMeta.getParentModel()).getDatabaseName());
		createContents();
	}

	public int open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return returnCode;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout_31 = new GridLayout();

		gridLayout_31.verticalSpacing = 0;
		gridLayout_31.marginWidth = 0;
		gridLayout_31.marginHeight = 0;
		gridLayout_31.horizontalSpacing = 0;
		shell.setLayout(gridLayout_31);
		shell.setSize(350, 180);
		if (shell != null) {// 居中
			Monitor[] monitorArray = Display.getCurrent().getMonitors();
			Rectangle rectangle = monitorArray[0].getBounds();
			Point size = shell.getSize();
			shell.setLocation((rectangle.width - size.x) / 2, (rectangle.height - size.y) * 2 / 5);
		}

		Composite parent = new Composite(shell, SWT.NONE);
		GridLayout layout1 = new GridLayout();
		layout1.marginLeft = 40;
		layout1.marginRight = 7;
		layout1.marginBottom = 11;
		layout1.horizontalSpacing = 20;
		layout1.verticalSpacing = 10;
		layout1.marginTop = 30;
		parent.setLayout(layout1);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(parent, SWT.NONE);
		GridData label_gl = new GridData();
		label.setLayoutData(label_gl);
		if (type.equals("sync")) { //$NON-NLS-1$

			shell.setText(Messages.ThisSubscribeDialog_sync);
			shell.setImage(ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync));
			label.setText(Messages.ThisSubscribeDialog_sysc_other);

			btnClear = new Button(parent, SWT.CHECK);
			btnClear.setText(Messages.ThisSubscribeDialog_Clear_existing_data);
			GridData btnDelete_gl = new GridData();
			btnClear.setLayoutData(btnDelete_gl);
			btnClear.setSelection(true);
		} 

		Composite compOpera = new Composite(shell, SWT.NONE);
		compOpera.setLayout(new GridLayout(3, false));
		GridData data11 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data11);
		Label label11 = new Label(compOpera, SWT.None);
		label11.setText(""); //$NON-NLS-1$
		data11 = new GridData(GridData.FILL_HORIZONTAL);
		data11.horizontalSpan = 1;
		label11.setLayoutData(data11);

		final Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText(Messages.ThisSubscribeDialog_confirm);
		data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data11);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;
		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				confirm();
				returnCode = IDialogConstants.OK_ID;
				shell.dispose();
			}
		});

		final Button btnCanel = new Button(compOpera, SWT.PUSH);
		btnCanel.setText(Messages.ThisSubscribeDialog_cancel);
		data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCanel.setLayoutData(data11);
		((GridData) btnCanel.getLayoutData()).widthHint = 61;

		btnCanel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}

	protected boolean confirm() {
		String sql = ""; //$NON-NLS-1$
		if (type.equals("sync") && btnClear != null) { //$NON-NLS-1$
			String str = (btnClear.getSelection() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$
			sql = " SELECT syslogical.alter_subscription_synchronize('" + sourceMeta.getSubscribeName() + "'," + str //$NON-NLS-1$ //$NON-NLS-2$
					+ ")"; //$NON-NLS-1$
		}

		if (sourceCon == null) {
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", "此数据库未连接成功！请检查"); //$NON-NLS-1$
			return false;
		}
		try {
			DatabaseMetaData metaData = sourceCon.getMetaData();
			String schemaTerm = metaData.getSchemaTerm();
			if (!schemaTerm.equals("")) { //$NON-NLS-1$
				Statement stm = sourceCon.createStatement();
				stm.executeQuery(sql);
				stm.close();
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", e1.getMessage()); //$NON-NLS-1$
		}
		return true;
	}

}
