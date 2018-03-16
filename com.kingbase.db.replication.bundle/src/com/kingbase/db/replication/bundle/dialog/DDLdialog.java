package com.kingbase.db.replication.bundle.dialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.pentaho.di.viewer.CCheckboxTreeViewer;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

public class DDLdialog extends Dialog {

	private Shell shell;
	private Tree releaseTree;
	private CCheckboxTreeViewer cTreeView;
	private StyledText txtSyscDDL;
	private ReleaseDataBase database;
	private List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();

	public DDLdialog(Shell parent, ReleaseDataBase database) {
		super(parent);
		list.clear();
		this.database = database;
		createContents();
	}

	public void open() {
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(Messages.DDLdialog_sysc_ddl);
		shell.setImage(ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.right_sync));
		final GridLayout gridLayout_31 = new GridLayout();
		gridLayout_31.verticalSpacing = 0;
		gridLayout_31.marginWidth = 0;
		gridLayout_31.marginHeight = 0;
		gridLayout_31.horizontalSpacing = 0;
		shell.setLayout(gridLayout_31);
		shell.setSize(835, 712);
		if (shell != null) {// 居中
			Monitor[] monitorArray = Display.getCurrent().getMonitors();
			Rectangle rectangle = monitorArray[0].getBounds();
			Point size = shell.getSize();
			shell.setLocation((rectangle.width - size.x) / 2, (rectangle.height - size.y) * 2 / 5);
		}

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout composite_gl = new GridLayout(2, true);
		composite_gl.horizontalSpacing = 0;
		composite_gl.verticalSpacing = 0;
		composite.setLayout(composite_gl);

		Group group1 = new Group(composite, SWT.NONE);
		group1.setText(Messages.DDLdialog_release_change);
		group1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout group_gl = new GridLayout();
		group_gl.horizontalSpacing = 10;
		group_gl.marginLeft = 5;
		group_gl.marginTop = 5;
		group1.setLayout(group_gl);

		releaseTree = new Tree(group1, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		releaseTree.setLayoutData(data);

		cTreeView = new CCheckboxTreeViewer(releaseTree);
		cTreeView.addCheckStateListener(new CheckStateListenerAdapter());

//		Collection childrenList = database.getChildrenList();
//		if (childrenList != null && childrenList.size() > 0) { 
//			list.add(database);
//		}
//		if (list.size() == 0) {
			list = getReleaseInfo();
//		}
		cTreeView.doSetInput(list);
		cTreeView.refresh();
		cTreeView.expandAll();

		Group group2 = new Group(composite, SWT.WRAP);
		group2.setText(Messages.DDLdialog_sysc_ddl);
		group2.setLayout(group_gl);
		GridData data3 = new GridData(SWT.FILL, SWT.FILL, true, false);
		group2.setLayoutData(data3);

		txtSyscDDL = new StyledText(group2, SWT.V_SCROLL | SWT.BORDER);
		txtSyscDDL.setText(""); //$NON-NLS-1$
		txtSyscDDL.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		txtSyscDDL.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		Composite compOpera = new Composite(shell, SWT.None);
		GridLayout layout11 = new GridLayout(4, false);
		layout11.marginRight = 20;
		compOpera.setLayout(layout11);
		GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, false);
		compOpera.setLayoutData(data1);
		Label labell = new Label(compOpera, SWT.None);
		data1 = new GridData(GridData.FILL_HORIZONTAL);
		data1.horizontalSpan = 2;
		labell.setLayoutData(data1);

		Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText(Messages.CreateReleaseDialog_btnConfirm);
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data1);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				Object[] objs = cTreeView.getCheckedElements();
				if (objs.length == 0) {
					MessageDialog.openError(UIUtils.getActiveShell(), "错误", Messages.DDLdialog_Please_select_the_required_publication); //$NON-NLS-1$
					return;
				}
				StringBuffer str = new StringBuffer();
				for (int i = 0; i < objs.length; i++) {
					Object objecj = objs[i];
					TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
					if (ti.getData() instanceof ReleaseDataInfo) {

						ReleaseDataInfo dataObj = (ReleaseDataInfo) ti.getData();
						if (dataObj.getReleaseName() != null) {
							str.append(dataObj.getReleaseName());
							if (i != objs.length - 1) {
								str.append(","); //$NON-NLS-1$
							}
						}
					}
				}
				
				Connection sourceCon = DatabaseUtil.getConnection((ReleaseDataSource)database.getParentModel(),database.getDatabaseName());
				String sql = "SELECT syslogical.replicate_ddl_command('" + txtSyscDDL.getText() + "','{" //$NON-NLS-1$ //$NON-NLS-2$
						+ str.toString() + "}')"; //$NON-NLS-1$
				if (sourceCon != null) {
					try {
						DatabaseMetaData metaData = sourceCon.getMetaData();
						String schemaTerm = metaData.getSchemaTerm();
						if (!schemaTerm.equals("")) {  //$NON-NLS-1$
							Statement stm = sourceCon.createStatement();
							
							stm.executeQuery(sql);
							stm.close();
						}
//						if (sourceCon != null) {
//							sourceCon.close();
//						}
					} catch (SQLException e1) {
						e1.printStackTrace();
						MessageDialog.openError(UIUtils.getActiveShell(), "错误", e1.getMessage());  //$NON-NLS-1$
						return;
					}
					MessageDialog.openInformation(UIUtils.getActiveShell(), "提示", Messages.DDLdialog_sysc_success); //$NON-NLS-1$
				}
				shell.dispose();
			}
		});

		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText(Messages.CreateReleaseDialog_btnCancel);
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data1);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				shell.dispose();
			}
		});
	}

	private List<ReleaseDataBase> getReleaseInfo() {

		List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
		ReleaseDataSource parent = (ReleaseDataSource) database.getParentModel();
		Connection sourceCon = DatabaseUtil.getConnection(parent, database.getDatabaseName());
		if (sourceCon != null) {
			DatabaseMetaData metaData;
			try {
				metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					Statement stm1 = sourceCon.createStatement();
					ResultSet rs0 = null;
					rs0 = stm1.executeQuery("SELECT SET_NAME FROM SYSLOGICAL.REPLICATION_SET"); //$NON-NLS-1$
					ReleaseDataBase database1 = new ReleaseDataBase();
					database1.setDatabaseName(database.getDatabaseName());
					database1.setDatabaseOid(database.getDatabaseOid());
					while (rs0.next()) {
						ReleaseDataInfo metaChild = new ReleaseDataInfo();
						String release = rs0.getString("SET_NAME"); //$NON-NLS-1$
						metaChild.setReleaseName(release);
						metaChild.setDatabaseName(database1.getDatabaseName());
						metaChild.setDatabaseOid(database1.getDatabaseOid());
						metaChild = ReleaseDataBase.setDataSourceMetaInfo(metaChild, database1);
						database1.addChild(metaChild);
					}
					list.add(database1);
					stm1.close();
					if (rs0 != null) {
						rs0.close();
					}
				}
//				if(sourceCon!=null){
//					sourceCon.close();
//				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
			}
		}
		return list;
	}

	class CheckStateListenerAdapter implements ICheckStateListener {

		@Override
		public void checkStateChanged(CheckStateChangedEvent arg0) {
			CheckboxTreeViewer checkboxTreeViewer = (CheckboxTreeViewer) arg0.getSource();
			boolean checked = arg0.getChecked();
			checkboxTreeViewer.setSubtreeChecked(arg0.getElement(), checked);

			TreeItem ti = (TreeItem) checkboxTreeViewer.testFindItem(arg0.getElement());
			TreeItem parent = ti.getParentItem();
			if (parent == null) {
				return;
			}
			TreeItem[] items = parent.getItems();
			int checkItems = 0;
			for (TreeItem treeItem : items) {
				if (treeItem.getChecked() && !treeItem.getGrayed()) {
					checkItems = checkItems + 1;
				}
			}
			ti.setChecked(checked);
			if (checkItems == 0) {
				parent.setChecked(false);
				return;
			}
			if (checkItems == items.length) {
				parent.setGrayed(false);
				parent.setChecked(true);
				return;
			}
			if (checkItems != items.length) {
				parent.setChecked(true);
				parent.setGrayed(true);
				return;
			}
		}
	}
}
