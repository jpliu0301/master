package com.kingbase.db.replication.bundle.dialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CCheckboxTreeViewer;
import org.pentaho.di.viewer.CTableTreeNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReleaseTable;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

public class CreateReleaseEditor extends EditorPart {
	public static final String ID = "com.kingbase.db.replication.bundle.dialog.CreateReleaseEditor";
	private ReleaseDataInfo releaseDataInfo;
	private Text releaseNameT;
	private Button insertB;
	private Button updateB;
	private Button deleteB;
	private Button truncateB;
	private Tree tableTree;
	private CCheckboxTreeViewer cTreeView;
	private StyledText txtContent;
	private Connection sourceCon = null;
	private List<List<String>> tableList;
	private List<ReleaseTable> dataList;// update时存放之前的表对应的节点数据
	private List<List<String>> filterList;// update时存放之前的表对应的节点数据
	private String type;
	private DataBaseInput input;
	private ReleaseDataBase database;
	private CBasicTreeViewer dbReplicationTree;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		this.input = (DataBaseInput) input;
		this.dbReplicationTree = this.input.getTreeView();
		CTableTreeNode node = this.input.getNode();
		if (node instanceof ReleaseDataInfo) {// 更新发布
			this.releaseDataInfo = (ReleaseDataInfo) node;

			this.database = (ReleaseDataBase) releaseDataInfo.getParentModel();
			this.type = "update";
		} else if (node instanceof ReleaseDataBase) {// 创建订阅
			this.database = (ReleaseDataBase) node;
			this.type = "create";
		}

		if (type.equals("update")) { //$NON-NLS-1$
			tableList = new ArrayList<List<String>>();
			dataList = new ArrayList<ReleaseTable>();
			filterList = new ArrayList<List<String>>();
		}
		sourceCon = DatabaseUtil.getConnection((ReleaseDataSource) database.getParentModel(),
				database.getDatabaseName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {

		GridData parent_gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(parent_gd);
		parent.setLayout(new GridLayout());
		Composite container = new Composite(parent, SWT.NONE);
		container.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

		GridLayout gl = new GridLayout(1, false);
		gl.verticalSpacing = 5;
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		container.setLayout(gl);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		SashForm form = new SashForm(container, SWT.VERTICAL);
		form.setLayout(new GridLayout());
		form.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Group group1 = new Group(form, SWT.WRAP);
		group1.setText(Messages.CreateReleaseDialog_release_options);
		GridLayout layout1 = new GridLayout(2, false);
		layout1.marginHeight = 15;
		layout1.marginWidth = 25;
		group1.setLayout(layout1);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		group1.setLayoutData(data);

		Label label = new Label(group1, SWT.NONE);
		label.setText(Messages.CreateReleaseDialog_release_name);
		label.setLayoutData(new GridData());

		releaseNameT = new Text(group1, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		releaseNameT.setLayoutData(data);
		UIUtils.verifyTextNotSpace(releaseNameT);
		UIUtils.verifyText(releaseNameT);
		releaseNameT.setTextLimit(63);

		Label label1 = new Label(group1, SWT.NONE);
		label1.setText("  "); //$NON-NLS-1$
		Composite compositeBtn = new Composite(group1, SWT.NONE);
		GridLayout layoutbtn = new GridLayout(4, true);
		compositeBtn.setLayout(layoutbtn);
		GridData databtn = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		compositeBtn.setLayoutData(databtn);

		insertB = new Button(compositeBtn, SWT.CHECK);
		updateB = new Button(compositeBtn, SWT.CHECK);
		deleteB = new Button(compositeBtn, SWT.CHECK);
		truncateB = new Button(compositeBtn, SWT.CHECK);

		insertB.setText("INSERT"); //$NON-NLS-1$
		updateB.setText("UPDATE"); //$NON-NLS-1$
		deleteB.setText("DELETE"); //$NON-NLS-1$
		truncateB.setText("TRUNCATE"); //$NON-NLS-1$

		updateB.setLayoutData(new GridData());
		deleteB.setLayoutData(new GridData());
		insertB.setLayoutData(new GridData());
		truncateB.setLayoutData(new GridData());
		insertB.setSelection(true);
		updateB.setSelection(true); // $NON-NLS-1$
		deleteB.setSelection(true); // $NON-NLS-1$
		truncateB.setSelection(true); // $NON-NLS-1$

		Group group2 = new Group(form, SWT.WRAP);
		group2.setText(Messages.CreateReleaseDialog_release_table_selection);
		GridLayout layout2 = new GridLayout();
		layout2.marginHeight = 15;
		layout2.marginWidth = 25;
		group2.setLayout(layout2);
		GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		group2.setLayoutData(data2);

		tableTree = new Tree(group2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		tableTree.setLayoutData(data);

		cTreeView = new CCheckboxTreeViewer(tableTree);
		cTreeView.addCheckStateListener(new CheckStateListenerAdapter());
		tableTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = tableTree.getSelection();
				
				if(type.equals("update")){
					if (selection.length > 0) {
						ReleaseTable data = (ReleaseTable) selection[0].getData();
						if (data.getTableName() != null&&data.getSchemaName()!=null) {
							String filter = ""; //$NON-NLS-1$
							boolean flag = false;
								for (List<String> list : filterList) {
									if (data.getSchemaName().equals(list.get(2))
											&& data.getTableName().equals(list.get(1))) {
										filter = list.get(0);
									}
								}
								for (int i = 0; i < tableList.size(); i++) {//更新时，已经选中的表不能再修改过滤条件
									List<String> list = tableList.get(i);
									if (data.getSchemaName().equals(list.get(0))
											&& data.getTableName().equals(list.get(1))) {
										flag = true;
										break;
									}
								}
						    txtContent.setEnabled(!flag);
							txtContent.setText(data.getTxtContent() == null ? (filter==null?"":filter) : data.getTxtContent());
						} else {
							txtContent.setEnabled(false);
						}
					}
				}else{
					if (selection.length > 0) {
						ReleaseTable data = (ReleaseTable) selection[0].getData();
						if (data.getTableName() != null&&data.getSchemaName()!=null) {
							txtContent.setEnabled(true);
							txtContent.setText(data.getTxtContent() == null ? "" : data.getTxtContent());
						} else {
							txtContent.setEnabled(false);
						}
					} else {
						txtContent.setEnabled(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		Group group3 = new Group(form, SWT.WRAP);
		group3.setText(Messages.CreateReleaseDialog_Filter_conditions);
		GridLayout layout3 = new GridLayout();
		layout3.marginHeight = 15;
		layout3.marginWidth = 25;
		group3.setLayout(layout2);
		GridData data3 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		group3.setLayoutData(data3);

		txtContent = new StyledText(group3, SWT.V_SCROLL | SWT.BORDER);
		txtContent.setText(""); //$NON-NLS-1$
		txtContent.setEnabled(false);
		txtContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtContent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		txtContent.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				TreeItem[] selection = tableTree.getSelection();
				ReleaseTable data = (ReleaseTable) selection[0].getData();
				data.setTxtContent(txtContent.getText().trim().equals("") ? null : txtContent.getText()); //$NON-NLS-1$
			}
		});

		Composite compOpera = new Composite(form, SWT.None);
		GridLayout layout11 = new GridLayout(5, false);
		layout11.marginRight = 20;
		compOpera.setLayout(layout11);
		GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, true);
		compOpera.setLayoutData(data1);
		Label labell = new Label(compOpera, SWT.None);
		data1 = new GridData(GridData.FILL_HORIZONTAL);
		data1.horizontalSpan = 2;
		labell.setLayoutData(data1);

		Button btnCheck = new Button(compOpera, SWT.PUSH);
		btnCheck.setText(Messages.CreateReleaseDialog_btnCheck);
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCheck.setLayoutData(data1);
		((GridData) btnCheck.getLayoutData()).widthHint = 61;

		btnCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean checkName = checkName();
				if (!checkName) {
					return;
				}
				boolean checkTables = checkTables();
				if (!checkTables) {
					return;
				}
				boolean checkRepeat = checkRepeat();
				if (!checkRepeat) {
					return;
				}
				MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateReleaseDialog_Prompt,
						Messages.CreateReleaseDialog_Test_success);
			}
		});

		Button btnConfirm = new Button(compOpera, SWT.PUSH);
		btnConfirm.setText(Messages.CreateReleaseDialog_btnConfirm);
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnConfirm.setLayoutData(data1);
		((GridData) btnConfirm.getLayoutData()).widthHint = 61;

		btnConfirm.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				boolean checkName = checkName();
				if (!checkName) {
					return;
				}
				boolean checkTables = checkTables();
				if (!checkTables) {
					return;
				}
				boolean checkRepeat = checkRepeat();
				if (!checkRepeat) {
					return;
				}
				if (type.equals("update")) { //$NON-NLS-1$

					boolean updateRelease = updateRelease();
					if (!updateRelease) {
						return;
					}
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateReleaseDialog_Prompt,
							Messages.CreateReleaseDialog_release_update_success);
					if (releaseDataInfo != null) {
						
						releaseDataInfo.removeAll();
						releaseDataInfo.refresh();
						dbReplicationTree.refresh();
					}
				} else {

					boolean createRelease = createRelease();
					if (!createRelease) {
						return;
					}
					MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateReleaseDialog_Prompt,
							Messages.CreateReleaseDialog_release_create_success);
				}
//				if (sourceCon != null) {
//					try {
//						sourceCon.close();
//					} catch (SQLException e1) {
//						e1.printStackTrace();
//					}
//				}
				UIUtils.closeEditor(CreateReleaseEditor.this);
			}
		});

		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText(Messages.CreateReleaseDialog_btnCancel);
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data1);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;

		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				if (sourceCon != null) {
//					try {
//						sourceCon.close();
//					} catch (SQLException e1) {
//						e1.printStackTrace();
//					}
//				}
				UIUtils.closeEditor(CreateReleaseEditor.this);
			}
		});

		form.setWeights(new int[] { 20, 45, 27, 8 });
		initViewData();
		if (type.equals("update")) { //$NON-NLS-1$
			releaseNameT.setEnabled(false);
			txtContent.setEnabled(false);
			releaseNameT.setText(releaseDataInfo.getReleaseName());
			cTreeView.expandAll();
			initUpdateRelease(releaseDataInfo.getReleaseName());
		}
	}

	/**
	 * 更新时将发布的信息写入dialog中
	 */
	private void initUpdateRelease(String releaseName) {
		if (sourceCon != null) {
			String sqlBtn = "SELECT REPLICATE_INSERT, REPLICATE_UPDATE, REPLICATE_DELETE, REPLICATE_TRUNCATE FROM SYSLOGICAL.REPLICATION_SET WHERE SET_NAME = '" //$NON-NLS-1$
					+ releaseName + "';"; //$NON-NLS-1$
			String sqlTables = "SELECT NSPNAME, RELNAME FROM SYSLOGICAL.TABLES WHERE SET_NAME = '" + releaseName + "';"; //$NON-NLS-1$ //$NON-NLS-2$

			String sqlFilter = "SELECT RST.SET_RELOID,RST.SET_ROW_FILTER_STR FROM SYSLOGICAL.REPLICATION_SET RS, SYSLOGICAL.REPLICATION_SET_TABLE RST WHERE RS.SET_ID = RST.SET_ID AND RS.SET_NAME ='" //$NON-NLS-1$
					+ releaseDataInfo.getReleaseName() + "'"; //$NON-NLS-1$

			try {
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					Statement stm = sourceCon.createStatement();
					ResultSet btnSet = null;
					btnSet = stm.executeQuery(sqlBtn);

					while (btnSet.next()) {
						String insert = btnSet.getString("REPLICATE_INSERT"); //$NON-NLS-1$
						insertB.setSelection(insert.equals("t") ? true : false); //$NON-NLS-1$
						String update = btnSet.getString("REPLICATE_UPDATE"); //$NON-NLS-1$
						updateB.setSelection(update.equals("t") ? true : false); //$NON-NLS-1$
						String delete = btnSet.getString("REPLICATE_DELETE"); //$NON-NLS-1$
						deleteB.setSelection(delete.equals("t") ? true : false); //$NON-NLS-1$
						String truncate = btnSet.getString("REPLICATE_TRUNCATE"); //$NON-NLS-1$
						truncateB.setSelection(truncate.equals("t") ? true : false); //$NON-NLS-1$
					}
					ResultSet tableSet = null;
					tableSet = stm.executeQuery(sqlTables);
					while (tableSet.next()) {
						List<String> list = new ArrayList<String>();
						String schema = tableSet.getString("NSPNAME"); //$NON-NLS-1$
						String table = tableSet.getString("RELNAME"); //$NON-NLS-1$
						list.add(schema);
						list.add(table);
						tableList.add(list);
					}

					ResultSet rowFilterSet = null;
					rowFilterSet = stm.executeQuery(sqlFilter);
					String reloid = ""; //$NON-NLS-1$
					String rowFilter = ""; //$NON-NLS-1$
					while (rowFilterSet.next()) {
						List<String> strList = new ArrayList<String>();
						reloid = rowFilterSet.getString("SET_RELOID"); //$NON-NLS-1$
						rowFilter = rowFilterSet.getString("SET_ROW_FILTER_STR"); //$NON-NLS-1$
						strList.add(rowFilter);
						strList.add(reloid);
						filterList.add(strList);
					}

					stm.close();
					if (btnSet != null) {
						btnSet.close();
					}
					if (tableSet != null) {
						tableSet.close();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
			}

			if (tableList.size() > 0) {

				for (List<String> list : tableList) {
					String schema = list.get(0);
					String table = list.get(1);

					TreeItem[] items = cTreeView.getTree().getItems();
					for (int i = 0; i < items.length; i++) {
						ReleaseTable data = (ReleaseTable) items[i].getData();
						if (data.getSchemaName().equals(schema)) {
							TreeItem[] itemsChild = items[i].getItems();
							for (int j = 0; j < itemsChild.length; j++) {
								ReleaseTable dataChild = (ReleaseTable) itemsChild[j].getData();
								if (dataChild.getTableName().equals(table)) {
									cTreeView.setChecked(dataChild, true);
									dataList.add(dataChild);

									if (filterList.size() > 0) {// 更新的时候讲过滤条件加载进来
										for (int k = 0; k < filterList.size(); k++) {
											List<String> updateList = filterList.get(k);
											String reloid = updateList.get(1);
											if (reloid.equals(table)) {
												updateList.add(schema);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 创建发布
	 */
	private boolean createRelease() {
		boolean insert = insertB.getSelection();
		boolean update = updateB.getSelection();
		boolean delete = deleteB.getSelection();
		boolean truncate = truncateB.getSelection();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT syslogical.create_replication_set('") //$NON-NLS-1$
				.append(releaseNameT.getText() + "'," + insert + "," + update + "," + delete + "," + truncate + "); "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		Object[] objs = cTreeView.getCheckedElements();
		List<ReleaseTable> treeList = new ArrayList<ReleaseTable>();
		for (int i = 0; i < objs.length; i++) {
			Object objecj = objs[i];
			TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
			ReleaseTable dataObj = (ReleaseTable) ti.getData();
			if (dataObj.getTableName() != null) {

				treeList.add(dataObj);
			}
		}

		if (sourceCon != null) {
			try {
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					sourceCon.setAutoCommit(false);
					Statement stm = sourceCon.createStatement();
					stm.executeQuery(sql.toString());
					for (int i = 0; i < treeList.size(); i++) {
						ReleaseTable meta = treeList.get(i);
						StringBuffer buffer = new StringBuffer();
						buffer.append(" SELECT syslogical.replication_set_add_table('").append(releaseNameT.getText())
								.append("',").append(meta.getTableOid()).append(",false,null,") ;
						if (meta.getTxtContent() != null) {

							buffer.append("'").append(meta.getTxtContent()).append("'").append(");"); //$NON-NLS-1$
						}else{
							buffer.append(meta.getTxtContent()).append(");");
						}
						stm.executeQuery(buffer.toString());
					}
					stm.close();
					sourceCon.commit();
					sourceCon.setAutoCommit(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
				try {
					sourceCon.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return false;
			}

			ReleaseDataInfo metaChild = new ReleaseDataInfo();
			metaChild.setReleaseName(releaseNameT.getText());
			metaChild.setDatabaseName(database.getDatabaseName());
			metaChild.setDatabaseOid(database.getDatabaseOid());
			ReleaseDataBase.setDataSourceMetaInfo(metaChild, database);
			database.addChild(metaChild);
			dbReplicationTree.expandToLevel(5);
			dbReplicationTree.refresh();
		}
		return true;
	}

	/**
	 * 更新发布
	 */
	private boolean updateRelease() {
		boolean insert = insertB.getSelection();
		boolean update = updateB.getSelection();
		boolean delete = deleteB.getSelection();
		boolean truncate = truncateB.getSelection();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT syslogical.alter_replication_set('") //$NON-NLS-1$
				.append(releaseNameT.getText() + "'," + insert + "," + update + "," + delete + "," + truncate + "); "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

		Object[] objs = cTreeView.getCheckedElements();
		List<ReleaseTable> addList = new ArrayList<ReleaseTable>();// 新增的表
		List<ReleaseTable> retainList = new ArrayList<ReleaseTable>();// 保留下来的表
		for (int i = 0; i < objs.length; i++) {
			Object objecj = objs[i];
			TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
			ReleaseTable dataObj = (ReleaseTable) ti.getData();
			if (dataObj.getTableName() != null) {
				boolean flag = false;
				for (ReleaseTable meta : dataList) {
					if (dataObj.getTableOid().equals(meta.getTableOid())) {
						flag = true;// 说明在更新之后还保留下来的
						retainList.add(meta);
						break;
					}
				}
				if (!flag) {
					addList.add(dataObj);
				}
			}
		}
		dataList.removeAll(retainList);// 去除保留的就只剩下删除的数据
		if (sourceCon != null) {
			try {
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					sourceCon.setAutoCommit(false);
					Statement stm = sourceCon.createStatement();
					stm.executeQuery(sql.toString());
					for (int i = 0; i < addList.size(); i++) {// 新增的表数据
						ReleaseTable meta = addList.get(i);
						StringBuffer buffer = new StringBuffer();
						buffer.append(" SELECT syslogical.replication_set_add_table('").append(releaseNameT.getText()) //$NON-NLS-1$
								.append("',").append(meta.getTableOid()).append(",false,null ,'") //$NON-NLS-1$ //$NON-NLS-2$
								.append(meta.getTxtContent()).append("');"); //$NON-NLS-1$
						stm.executeQuery(buffer.toString());
					}
					for (int i = 0; i < dataList.size(); i++) {// 删除的表数据
						ReleaseTable meta = dataList.get(i);
						StringBuffer buffer = new StringBuffer();
						buffer.append("SELECT syslogical.replication_set_remove_table('").append(releaseNameT.getText()) //$NON-NLS-1$
								.append("',").append(meta.getTableOid() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
						stm.executeQuery(buffer.toString());
					}
					stm.close();
					sourceCon.commit();
					sourceCon.setAutoCommit(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
				try {
					sourceCon.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				return false ;
			}

			dbReplicationTree.refresh();
		}
		return true;
	}

	/**
	 * 验证发布名称
	 */
	private boolean checkName() {
		if (releaseNameT.getText().equals("")) { //$NON-NLS-1$
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
					Messages.CreateReleaseDialog_release_name_is_null);
			releaseNameT.setFocus();
			return false;
		}
		return true;
	}

	/**
	 * 验证发布的表数据选择是否正常
	 */
	private boolean checkTables() {
		Object[] objs = cTreeView.getCheckedElements();
		if (objs.length == 0) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", Messages.CreateReleaseDialog_select_tables); //$NON-NLS-1$
			return false;
		}
		List<String> treeList = new ArrayList<String>();
		for (int i = 0; i < objs.length; i++) {
			Object objecj = objs[i];
			TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
			ReleaseTable dataObj = (ReleaseTable) ti.getData();
			if (dataObj.getTableName() != null) {
				treeList.add(dataObj.getTableOid());
			}
		}
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < treeList.size(); i++) {
			String tableOid = treeList.get(i);
			buffer.append("'").append(tableOid).append("'");
			if (i != treeList.size() - 1) {
				buffer.append(",");
			}
		}
		List<List<String>> list = new ArrayList<List<String>>();
		if (sourceCon != null) {
			try {
				
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					Statement stm = sourceCon.createStatement();
					ResultSet resultSet = null;
					resultSet = stm.executeQuery(
							" select n.nspname || '.' || cla.relname AS DESC,cla.OID from sys_constraint con LEFT JOIN SYS_CLASS cla ON cla.oid = con.CONFRELID LEFT JOIN SYS_NAMESPACE n ON n.oid = cla.relnamespace where contype = 'f' AND CONRELID IN ( "
									+ buffer.toString() + ")AND cla.OID NOT IN (" + buffer.toString() + ")") ; 
					while (resultSet.next()) {
						List<String> str = new ArrayList<String>();
						String oid = resultSet.getString("OID"); //$NON-NLS-1$
						String desc = resultSet.getString("DESC"); //$NON-NLS-1$
						str.add(oid);
						str.add(desc);
						list.add(str);
					}
					resultSet.close();
					stm.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
				try {
					sourceCon.rollback();
					return false;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		boolean flag = true;
		StringBuffer desc = new StringBuffer(); 
		for (List<String> foreignKey : list) {
			String oid = foreignKey.get(0);// 外键表oid
			desc.append(foreignKey.get(1)+"\n");// 外键表描述
			for (int j = 0; j < treeList.size(); j++) {
				if (oid.equals(treeList.get(j))) {
					flag = false;
				}
			}
		}
		if (!flag) {
			AddTableDialog dialog = new AddTableDialog(UIUtils.getActiveShell(), "还依赖如下的表:"+desc.toString());
			int open = dialog.open();
			if (open == 3) {
				for (List<String> foreignKey : list) {
					String oid = foreignKey.get(0);// 外键表oid
					String tableName = foreignKey.get(1);// 外键表desc
					ReleaseTable table = new ReleaseTable();
					table.setTableOid(oid);
					
					TreeItem[] items = cTreeView.getTree().getItems();
					for (TreeItem schemaItem : items) {
						TreeItem[] tableItems = schemaItem.getItems();
						for (TreeItem tableItem : tableItems) {
							ReleaseTable data = (ReleaseTable) tableItem.getData();
							if(data.getTableOid().equals(oid)){
								tableItem.setChecked(true);
							}
						}
						
					}

				}
			}
			return false;
		}
		return true;
	}

	/**
	 * 验证发布名称是否重复
	 */
	private boolean checkRepeat() {
		if (!releaseNameT.getText().equals("") && sourceCon != null) { //$NON-NLS-1$
			String count = "0"; //$NON-NLS-1$
			try {
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					Statement stm = sourceCon.createStatement();
					ResultSet resultSet = null;
					resultSet = stm.executeQuery("select count(*) from syslogical.replication_set where set_name = \'" //$NON-NLS-1$
							+ releaseNameT.getText() + "\'"); //$NON-NLS-1$
					while (resultSet.next()) {
						count = resultSet.getString("count"); //$NON-NLS-1$
					}
					resultSet.close();
					stm.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
			}
			if (type.equals("create") && !count.equals("0")) { //$NON-NLS-1$ //$NON-NLS-2$
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
						releaseNameT.getText() + Messages.CreateReleaseDialog_release_Already_exist);
				releaseNameT.setFocus();
				return false;
			}
		}
		return true;
	}

	/**
	 * 初始化发布数据
	 */
	private void initViewData() {

		List<ReleaseTable> listMeta = new ArrayList<ReleaseTable>();

		if (sourceCon != null) {
			try {
				DatabaseMetaData metaData = sourceCon.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) { //$NON-NLS-1$
					Statement stm = sourceCon.createStatement();
					ResultSet resultSet = null;
					resultSet = stm.executeQuery(
							"SELECT C.OID, N.NSPNAME AS SCHEMANAME,C.RELNAME AS TABLENAME FROM SYS_CLASS C LEFT JOIN SYS_NAMESPACE N ON N.OID = C.RELNAMESPACE "
									+ " WHERE C.RELKIND = 'r' "
									+ " AND N.NSPNAME NOT LIKE 'SYS_%'"
									+ " AND N.NSPNAME NOT IN ('SYSLOGICAL', 'INFORMATION_SCHEMA')" //$NON-NLS-1$
									+ "ORDER BY SCHEMANAME , TABLENAME;");
					
					while (resultSet.next()) {
						String schemaName = resultSet.getString("SCHEMANAME"); //$NON-NLS-1$
						String tableName = resultSet.getString("TABLENAME"); //$NON-NLS-1$
						String oid = resultSet.getString("OID"); //$NON-NLS-1$
						ReleaseTable table = new ReleaseTable();
						table.setSchemaName(schemaName);
						table.setTableName(tableName);
						table.setTableOid(oid);
						listMeta.add(table);
					}
					if (resultSet != null) {
						resultSet.close();
					}
					if (stm != null) {
						stm.close();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
			}
		}
		if(listMeta.size()==0){
			return;
		}
		String schemaName = listMeta.get(0).getSchemaName();
		ReleaseTable parent = new ReleaseTable();
		parent.setSchemaName(schemaName);
		
		List<ReleaseTable> list = new ArrayList<ReleaseTable>();
		list.add(parent);
		for (ReleaseTable schema : listMeta) {// 在每个不同的模式下找到子表
			if(schemaName.equals(schema.getSchemaName())){
				parent.addChild(schema);
			}else{
				schemaName = schema.getSchemaName();
				parent = new ReleaseTable();
				parent.setSchemaName(schemaName);
				parent.addChild(schema);
				list.add(parent);
			}
		}
		cTreeView.setInput(list);
		cTreeView.refresh();
//		cTreeView.expandAll();
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

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	class AddTableDialog extends Dialog{
		private Shell shell;
		private int returnCode = Window.CANCEL;
        private String text;  
		public AddTableDialog(Shell shell, String text) {
			super(shell);
			this.shell = shell;
			this.text= text;
			// 组件布局
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
			shell.setText("警告");
			final GridLayout gridLayout_31 = new GridLayout();
			
			gridLayout_31.verticalSpacing = 0;
			gridLayout_31.marginWidth = 0;
			gridLayout_31.marginHeight = 0;
			gridLayout_31.horizontalSpacing = 0;
			shell.setLayout(gridLayout_31);
			shell.setSize(350, 200);
			if (shell != null) {//居中
				Monitor[] monitorArray = Display.getCurrent().getMonitors();
				Rectangle rectangle = monitorArray[0].getBounds();
				Point size = shell.getSize();
				shell.setLocation((rectangle.width - size.x) / 2, (rectangle.height - size.y) / 2);
			}

			Composite parent = new Composite(shell, SWT.NONE);
			GridLayout layout1 = new GridLayout();
			layout1.marginLeft = 40;
			layout1.marginRight = 7;
			layout1.marginBottom = 11;
			layout1.horizontalSpacing = 20;
			layout1.verticalSpacing = 20;
			layout1.marginTop = 30;
			parent.setLayout(layout1);
			
			Label label = new Label(parent, SWT.NONE);
			label.setText(text);
			GridData label_gl = new GridData();
			label.setLayoutData(label_gl);
			
		    final Button btnAdd = new Button(parent, SWT.CHECK);
			btnAdd.setText("是否自动增加！？");
			GridData btnDelete_gl = new GridData();
			btnAdd.setLayoutData(btnDelete_gl);
			
			Composite compOpera = new Composite(shell, SWT.NONE);
			compOpera.setLayout(new GridLayout(3, false));
			GridData data11 = new GridData(GridData.FILL_HORIZONTAL);
			compOpera.setLayoutData(data11);
			Label label11 = new Label(compOpera, SWT.None);
			label11.setText("");
			data11 = new GridData(GridData.FILL_HORIZONTAL);
			data11.horizontalSpan = 1;
			label11.setLayoutData(data11);

			final Button btnConfirm = new Button(compOpera, SWT.PUSH);
			btnConfirm.setText("确定");
			data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
			btnConfirm.setLayoutData(data11);
			((GridData) btnConfirm.getLayoutData()).widthHint = 61;
			btnConfirm.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					returnCode = Window.OK;
					if(btnAdd.getSelection()){
						returnCode = 3;
					}
	                shell.dispose();
				}
			});
			
			final Button btnCanel = new Button(compOpera, SWT.PUSH);
			btnCanel.setText("取消");
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
		
	}
}
