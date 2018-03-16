package com.kingbase.db.replication.bundle.dialog;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.dom4j.Element;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.pentaho.di.util.SWTUtil;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CCheckboxTreeViewer;
import org.pentaho.di.viewer.CTableTreeNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReplicationFile;
import com.kingbase.db.replication.bundle.model.tree.ReplicationRoot;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataBase;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;
import com.kingbase.db.replication.bundle.views.ReplicationView;

public class CreateSubscribeEditor extends EditorPart {

    public static final String ID = "com.kingbase.db.replication.bundle.dialog.CreateSubscribeEditor";
    private String type;
    private Text subscribeNameT;
    private Combo releaseServerC;
    private Combo databaseC;
    private Tree tableTree;
    private CCheckboxTreeViewer cTreeView;
    private ReleaseDataBase releaseDB;
    private SubscribeDataInfo subscribeDataInfo;
    private ReleaseDataSource CurrSourceMeta;// 目前选择的服务器
    private List<ReleaseDataSource> serverList = new ArrayList<ReleaseDataSource>();
    private List<ReleaseDataInfo> treeList = new ArrayList<ReleaseDataInfo>();
    private List<Element> listEle;
    private Connection sourceCon;
    private String databaseText = ""; //$NON-NLS-1$
    private String sourceText = ""; //$NON-NLS-1$
    private String[] release;// 更新的时候获取订阅的发布
    private List<ReleaseDataInfo> releaseList = new ArrayList<ReleaseDataInfo>();// 更新的时候获取订阅的发布
    private List<List<String>> notExistList = new ArrayList<List<String>>();// 不存在的表
//    private List<DataTypeEntity> listTypeEntity;
    private DataBaseInput input;
    private CBasicTreeViewer dbReplicationTree;
    private SubscribeDataBase database;// 订阅上一节点属于的数据库

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	setSite(site);
	setInput(input);
	setPartName(input.getName());
	this.input = (DataBaseInput) input;
	this.dbReplicationTree = this.input.getTreeView();
	CTableTreeNode node = this.input.getNode();
//	this.listTypeEntity = DataTypeEntity.getInstence();
	if (node instanceof SubscribeDataInfo) {// 更新订阅
	    this.subscribeDataInfo = (SubscribeDataInfo) node;

	    this.database = (SubscribeDataBase) subscribeDataInfo.getParentModel();
	    getNeedInfo(subscribeDataInfo);
	    this.type = "update";
	    if (releaseDB == null) {
		MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "查询订阅信息失败，请注册相关发布服务器");
		UIUtils.closeEditor(CreateSubscribeEditor.this);
	    }
	} else if (node instanceof SubscribeDataBase) {// 创建订阅
	    this.database = (SubscribeDataBase) node;
	    ReplicationRoot root = (ReplicationRoot) database.getParentModel().getParentModel().getParentModel();
	    ReplicationFile ifile = (ReplicationFile) (root.getChildren())[0];
	    this.listEle = ReplicationView.getlistEle(ifile);
	    this.type = "create";
	}
	this.sourceCon = DatabaseUtil.getConnection((SubscribeDataSource) database.getParentModel(), database.getDatabaseName());

    }

    private void getNeedInfo(SubscribeDataInfo dataInfo) {
	SubscribeDataBase datebase = (SubscribeDataBase) dataInfo.getParentModel();
	SubscribeDataSource source = (SubscribeDataSource) datebase.getParentModel();

	ReplicationRoot root = (ReplicationRoot) source.getParentModel().getParentModel();
	ReplicationFile ifile = (ReplicationFile) root.getChildren()[0];
	listEle = ReplicationView.getlistEle(ifile);
	if (listEle == null || listEle.size() == 0) {
	    return;
	}
	List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
	for (int i = 0, n = listEle.size(); i < n; i++) {
	    Element element = listEle.get(i);

	    Element nodesElm = element.element("nodes");
	    List<Element> nodeElm = nodesElm.elements("node");
	    for (Element node : nodeElm) {
		ReleaseDataBase databaseMeta = new ReleaseDataBase();
		databaseMeta.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$
		databaseMeta.setNodeId(node.element("nodeId").getStringValue()); //$NON-NLS-1$
		databaseMeta.setDatabaseName(node.element("dbname").getStringValue()); //$NON-NLS-1$
		list.add(databaseMeta);
	    }
	}

	Connection sourceCon = DatabaseUtil.getConnection(source, datebase.getDatabaseName());
	if (sourceCon == null) {
	    return;
	}
	ReleaseDataBase releaseDataBase = new ReleaseDataBase();
	String sets = null;
	try {
	    ResultSet subscribeSet = sourceCon.createStatement().executeQuery("SELECT SUB_ORIGIN, ARRAY_TO_STRING(SUB_REPLICATION_SETS, ',') AS SETS FROM SYSLOGICAL.SUBSCRIPTION WHERE SUB_NAME = '" //$NON-NLS-1$
		    + dataInfo.getSubscribeName() + "'"); //$NON-NLS-1$
	    while (subscribeSet.next()) {
		String id = subscribeSet.getString("SUB_ORIGIN"); //$NON-NLS-1$
		sets = subscribeSet.getString("SETS"); //$NON-NLS-1$
		for (ReleaseDataBase dataBase : list) {
		    if (dataBase.getNodeId().equals(id)) {
			releaseDataBase = dataBase;
			break;
		    }
		}
	    }
	    sourceCon.createStatement().close();
	    subscribeSet.close();
	    // if (sourceCon != null) {
	    // sourceCon.close();
	    // }
	} catch (Exception e) {
	    MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
	}
	String[] split = null;
	if (sets != null) {
	    split = sets.split(","); //$NON-NLS-1$
	}
	this.release = split;
	this.releaseDB = releaseDataBase;
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
	group1.setText(Messages.CreateSubscribeDialog_Sub_Option);
	GridLayout layout1 = new GridLayout(2, false);
	layout1.marginHeight = 15;
	layout1.marginWidth = 25;
	layout1.verticalSpacing = 15;
	group1.setLayout(layout1);
	GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	group1.setLayoutData(data);

	Label label = new Label(group1, SWT.NONE);
	label.setText(Messages.CreateSubscribeDialog_name);
	label.setLayoutData(new GridData());

	subscribeNameT = new Text(group1, SWT.BORDER);
	data = new GridData(GridData.FILL_HORIZONTAL);
	subscribeNameT.setLayoutData(data);
	UIUtils.verifyText(subscribeNameT);
	subscribeNameT.setTextLimit(63);

	Label label1 = new Label(group1, SWT.NONE);
	label1.setText(Messages.CreateSubscribeDialog_release_server_name);
	label1.setLayoutData(new GridData());

	releaseServerC = new Combo(group1, SWT.READ_ONLY);
	data = new GridData(GridData.FILL_HORIZONTAL);
	releaseServerC.setLayoutData(data);
	releaseServerC.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {

		if (!releaseServerC.getText().equals(sourceText)) {
		    sourceText = releaseServerC.getText();
		} else {
		    return;
		}

		if (!releaseServerC.getText().equals("")) { //$NON-NLS-1$
		    databaseC.removeAll();
		    cTreeView.setInput(null);
		    databaseText = "";
		    for (ReleaseDataSource subscribe : serverList) {
			if (releaseServerC.getText().equals(subscribe.getDbName())) {
			    CurrSourceMeta = subscribe;
			    for (int i = 0; i < subscribe.getChildren().length; i++) {
				SubscribeDataBase view = (SubscribeDataBase) subscribe.getChildren()[i];
				databaseC.add(view.getDatabaseName());
			    }
			}
		    }
		}
	    }
	});
	;

	Label label11 = new Label(group1, SWT.NONE);
	label11.setText(Messages.CreateSubscribeDialog_database);
	label11.setLayoutData(new GridData());

	databaseC = new Combo(group1, SWT.READ_ONLY);
	data = new GridData(GridData.FILL_HORIZONTAL);
	databaseC.setLayoutData(data);
	Group group2 = new Group(form, SWT.WRAP);
	group2.setText(Messages.CreateSubscribeDialog_release_selection);
	GridLayout layout2 = new GridLayout();
	layout2.marginHeight = 15;
	layout2.marginWidth = 25;
	group2.setLayout(layout2);
	GridData data2 = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
	group2.setLayoutData(data2);
	databaseC.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (!databaseC.getText().equals(databaseText)) {
		    databaseText = databaseC.getText();
		    iniTableViewer();
		}
	    }
	});

	tableTree = new Tree(group2, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	data = new GridData(GridData.FILL_BOTH);
	tableTree.setLayoutData(data);

	cTreeView = new CCheckboxTreeViewer(tableTree);
	cTreeView.addCheckStateListener(new CheckStateListenerAdapter());

	Composite compOpera = new Composite(form, SWT.None);
	GridLayout layout11 = new GridLayout(4, false);
	layout11.marginRight = 20;
	compOpera.setLayout(layout11);
	GridData data1 = new GridData(SWT.FILL, SWT.FILL, true, true);
	compOpera.setLayoutData(data1);
	Label labell = new Label(compOpera, SWT.None);
	data1 = new GridData(GridData.FILL_HORIZONTAL);
	data1.horizontalSpan = 2;
	labell.setLayoutData(data1);

	Button btnConfirm = new Button(compOpera, SWT.PUSH);
	btnConfirm.setText(Messages.CreateSubscribeDialog_confirm);
	data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnConfirm.setLayoutData(data1);
	((GridData) btnConfirm.getLayoutData()).widthHint = 61;

	btnConfirm.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		boolean checkName = checkName();
		if (!checkName) {
		    return;
		}
		boolean checkRepeat = checkRepeat();
		if (!checkRepeat) {
		    return;
		}
		Object[] objs = cTreeView.getCheckedElements();
		if (objs.length == 0) {
		    MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
			    Messages.CreateSubscribeDialog_please_change_release);
		    return;
		}
		if (type.equals("update")) { //$NON-NLS-1$

		    boolean updateSubscribe = updateSubscribe();
		    if (!updateSubscribe) {
			return;
		    }
		    MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateSubscribeDialog_Prompt, Messages.CreateSubscribeDialog_update_subscription_success);
		    if (subscribeDataInfo != null) {

			subscribeDataInfo.removeAll();
			subscribeDataInfo.refresh();
			dbReplicationTree.refresh();
		    }
		} else {

		    boolean createSubscribe = createSubscribe();
		    if (!createSubscribe) {
			return;
		    }
		    MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateSubscribeDialog_Prompt, Messages.CreateSubscribeDialog_create_subscription_success);
		}
		String subscribeName = subscribeNameT.getText();
		UIUtils.closeEditor(CreateSubscribeEditor.this);
		if (notExistList != null && notExistList.size() > 0) {
		    SWTUtil.asyncExecThread(new Runnable() {
			@Override
			public void run() {
			    for (int i = 0; i < notExistList.size(); i++) {
				List<String> list = notExistList.get(i);
				String schema = list.get(0);
				for (int j = 1; j < list.size(); j++) {
				    String tableName = schema + "." + list.get(j); //$NON-NLS-1$
				    String sql = "SELECT syslogical.alter_subscription_resynchronize_table('" //$NON-NLS-1$
					    + subscribeName + "','" + tableName + "'::regclass)"; //$NON-NLS-1$ //$NON-NLS-2$
				    try {
					DatabaseMetaData metaData = sourceCon.getMetaData();
					String schemaTerm = metaData.getSchemaTerm();
					if (!schemaTerm.equals("")) { //$NON-NLS-1$
					    Statement stm = sourceCon.createStatement();
					    stm.executeQuery(sql);
					    stm.close();
					}
					// if (sourceCon != null) {
					// sourceCon.close();
					// }
				    } catch (SQLException e) {
					e.printStackTrace();
					MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
				    }
				}
			    }
			}
		    });
		}
	    }
	});

	Button btnCancel = new Button(compOpera, SWT.PUSH);
	btnCancel.setText(Messages.CreateSubscribeDialog_cancel);
	data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnCancel.setLayoutData(data1);
	((GridData) btnCancel.getLayoutData()).widthHint = 61;

	btnCancel.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		// if (sourceCon != null) {
		// try {
		// sourceCon.close();
		// } catch (SQLException e1) {
		// e1.printStackTrace();
		// }
		// }
		UIUtils.closeEditor(CreateSubscribeEditor.this);
	    }
	});

	form.setWeights(new int[] { 24, 68, 8 });
	if (type.equals("update") && releaseDB != null) { //$NON-NLS-1$
	    subscribeNameT.setEnabled(false);
	    releaseServerC.setEnabled(false);
	    databaseC.setEnabled(false);
	    subscribeNameT.setText(subscribeDataInfo.getSubscribeName());
	    releaseServerC.add(releaseDB.getDbName() != null ? releaseDB.getDbName() : "");
	    databaseC.add(releaseDB.getDatabaseName() != null ? releaseDB.getDatabaseName() : "");
	    releaseServerC.select(0);
	    databaseC.select(0);
	    initUpdateSubscribe();
	} else {

	    initCreateViewData();
	}

    }

    /**
     * update初始化订阅
     */
    private void initUpdateSubscribe() {
	CurrSourceMeta = new ReleaseDataSource();
	CurrSourceMeta.setDbName(releaseDB.getDbName());
	CurrSourceMeta.setDbServer(releaseDB.getDbServer());
	CurrSourceMeta.setDbPort(releaseDB.getDbPort());
	CurrSourceMeta.setDbUser(releaseDB.getDbUser());
	CurrSourceMeta.setDbPasswrod(releaseDB.getDbPasswrod());
	CurrSourceMeta.setDriverName(releaseDB.getDriverName());
	CurrSourceMeta.setDriverPath(releaseDB.getDriverPath());

	iniTableViewer();
	TreeItem[] items = cTreeView.getTree().getItems();
	for (TreeItem item : items) {
	    ReleaseDataInfo data = (ReleaseDataInfo) item.getData();
	    for (String str : release) {
		if (data.getReleaseName().equals(str)) {
		    item.setChecked(true);
		    releaseList.add(data);
		}
	    }
	}
    }

    protected boolean updateSubscribe() {

	Object[] objs = cTreeView.getCheckedElements();
	List<ReleaseDataInfo> addList = new ArrayList<ReleaseDataInfo>();// 新增的表
	List<ReleaseDataInfo> retainList = new ArrayList<ReleaseDataInfo>();// 保留下来的表
	for (int i = 0; i < objs.length; i++) {
	    Object objecj = objs[i];
	    TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
	    ReleaseDataInfo dataObj = (ReleaseDataInfo) ti.getData();
	    boolean flag = false;
	    for (ReleaseDataInfo meta : releaseList) {
		if (dataObj.getReleaseName().equals(meta.getReleaseName())) {
		    flag = true;// 说明在更新之后还保留下来的
		    retainList.add(meta);
		    break;
		}
	    }
	    if (!flag) {
		addList.add(dataObj);
	    }
	}
	releaseList.removeAll(retainList);// 去除保留的就只剩下删除的数据,

	if (sourceCon != null) {
	    try {
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		sourceCon.setAutoCommit(false);
		if (addList.size() > 0) {
		    checkTableExist();
		}
		if (!schemaTerm.equals("")) { //$NON-NLS-1$
		    Statement stm = sourceCon.createStatement();
		    for (int i = 0; i < addList.size(); i++) {// 新增的表数据
			ReleaseDataInfo meta = addList.get(i);
			String addSQL = "SELECT SYSLOGICAL.ALTER_SUBSCRIPTION_ADD_REPLICATION_SET('" //$NON-NLS-1$
				+ subscribeNameT.getText() + "','" + meta.getReleaseName() + "')"; //$NON-NLS-1$//$NON-NLS-2$
			stm.executeQuery(addSQL);
		    }
		    for (int i = 0; i < releaseList.size(); i++) {// 删除的表数据
			ReleaseDataInfo meta = releaseList.get(i);
			String deleteSQL = "SELECT SYSLOGICAL.ALTER_SUBSCRIPTION_REMOVE_REPLICATION_SET('" //$NON-NLS-1$
				+ subscribeNameT.getText() + "','" + meta.getReleaseName() + "')"; //$NON-NLS-1$//$NON-NLS-2$
			stm.executeQuery(deleteSQL);
		    }
		    sourceCon.commit();
		    stm.close();
		}
		sourceCon.setAutoCommit(true);
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
	    dbReplicationTree.refresh();
	}
	return true;
    }

    /**
     * 创建订阅
     */
    protected boolean createSubscribe() {

	StringBuffer provider_dsn = new StringBuffer();
	provider_dsn.append("host="); //$NON-NLS-1$
	for (ReleaseDataSource meta : serverList) {
	    if (releaseServerC.getText().equals(meta.getDbName())) {
		provider_dsn.append(meta.getDbServer());
		provider_dsn.append(" port="); //$NON-NLS-1$
		provider_dsn.append(meta.getDbPort());
		provider_dsn.append(" user="); //$NON-NLS-1$
		provider_dsn.append(meta.getDbUser());
		provider_dsn.append(" password="); //$NON-NLS-1$
		provider_dsn.append(meta.getDbPasswrod());
		provider_dsn.append(" dbname="); //$NON-NLS-1$
		provider_dsn.append(databaseC.getText());
		break;
	    }
	}
	Object[] objs = cTreeView.getCheckedElements();
	StringBuffer str = new StringBuffer();

	for (int i = 0; i < objs.length; i++) {
	    Object objecj = objs[i];
	    TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
	    ReleaseDataInfo dataObj = (ReleaseDataInfo) ti.getData();
	    if (dataObj.getReleaseName() != null) {
		str.append(dataObj.getReleaseName());
		if (i != objs.length - 1) {
		    str.append(","); //$NON-NLS-1$
		}
	    }
	}


	String sql = "SELECT syslogical.create_subscription('" + subscribeNameT.getText() + "','" //$NON-NLS-1$//$NON-NLS-2$
		+ provider_dsn.toString() + "','{" + str.toString() + "}',false,true,'{all}')"; //$NON-NLS-1$//$NON-NLS-2$

	if (sourceCon != null) {
	    try {
		sourceCon.setAutoCommit(false);
		boolean checkTableExist = checkTableExist();
		if (!checkTableExist) {
		    return false;
		}
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) { //$NON-NLS-1$
		    Statement stm = sourceCon.createStatement();
		    stm.executeQuery(sql);
		    stm.close();
		    sourceCon.commit();
		}
		sourceCon.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
		try {
		    sourceCon.rollback();
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}
		MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSubscribeDialog_Prompt, e.getMessage());
		return false;
	    }

	    SubscribeDataInfo metaChild = new SubscribeDataInfo();
	    metaChild = SubscribeDataBase.setDataSourceMetaInfo(metaChild, database);
	    metaChild.setSubscribeName(subscribeNameT.getText());
	    metaChild.setSubscribeEnable("t"); //$NON-NLS-1$
	    metaChild.setDatabaseName(database.getDatabaseName());
	    metaChild.setDatabaseOid(database.getDatabaseOid());
	    database.addChild(metaChild);
	    dbReplicationTree.expandToLevel(5);
	    dbReplicationTree.refresh();
	}
	return true;
    }

    /**
     * 验证订阅名称
     */
    private boolean checkName() {
	if (subscribeNameT.getText().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
		    Messages.CreateSubscribeDialog_Subscription_name_cannot_be_empty);
	    subscribeNameT.setFocus();
	    return false;
	}
	return true;
    }

    private boolean checkTableExist() {
	notExistList = getTableNotExist();
	if (notExistList != null && notExistList.size() > 0) {
	    StringBuffer buffer = new StringBuffer();
	    for (int j = 0; j < notExistList.size(); j++) {

		List<String> tableList = notExistList.get(j);
		if (tableList.size() > 1) {
		    for (int i = 1; i < tableList.size(); i++) {
			String schema = tableList.get(0);
			buffer.append(schema).append(".").append(tableList.get(i)); //$NON-NLS-1$
			if (!(j == (notExistList.size() - 1) && i == (tableList.size() - 1))) {
			    buffer.append(","); //$NON-NLS-1$
			}
		    }
		}
	    }
	    if (!buffer.toString().equals("")) {

		boolean flag = MessageDialog.openConfirm(UIUtils.getActiveShell(), Messages.CreateSubscribeDialog_Prompt, // $NON-NLS-1$
			Messages.CreateSubscribeDialog_table_is_not_exist + buffer.toString());
		if (flag) {
		    boolean createSchema = createSchema(notExistList);
		    boolean createTable = createTable(notExistList);
		    if (!createSchema || !createTable) {
			return false;
		    }
		}
	    }
	}
	return true;
    }

    /**
     * 在本订阅库上创建模式；因为有可能不存在对应模式
     */
    private boolean createSchema(List<List<String>> list) {
	try {
	    Statement create = sourceCon.createStatement();
	    for (List<String> schemaList : list) {
		String schema = schemaList.get(0);
		ResultSet rs = create.executeQuery("Select count(1) from sys_namespace where nspname = '" + schema + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		int count = 0;
		while (rs.next()) {
		    count = rs.getInt("count"); //$NON-NLS-1$
		}
		if (count == 0) {
		    create.execute("CREATE SCHEMA " + schema); //$NON-NLS-1$
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	    try {
		    sourceCon.rollback();
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}
	    return false;
	}
	return true;
    }

    /**
     * 在本订阅库上建表
     */
    private boolean createTable(List<List<String>> list) {
	// 首先在发布库上得到表的列、外键、约束等信息
	Connection connection = DatabaseUtil.getConnection(CurrSourceMeta, databaseC.getText());
	if (connection == null) {
	    return false;
	}
	try {
	    DatabaseMetaData metaData = connection.getMetaData();
	    Statement stm = connection.createStatement();
	    for (List<String> tableList : list) {
		String schema = tableList.get(0);
		for (int k = 1; k < tableList.size(); k++) {
		    String tableName = tableList.get(k);

		    StringBuffer buffer = new StringBuffer("CREATE TABLE"); //$NON-NLS-1$
		    buffer.append(" ").append("\"" + schema + "\"." + tableName).append("\n").append('(').append("\n");

		    ResultSet rs = metaData.getColumns(null, schema, tableName, "%"); //$NON-NLS-1$
		    ResultSet pk = metaData.getPrimaryKeys(null, schema, tableName);
		    List<String> pkList = new ArrayList<String>();

		    while (pk.next()) {
			String pk_column = pk.getString("COLUMN_NAME");// 主键所在的列 //$NON-NLS-1$
			pkList.add(pk_column);
		    }

		    String sql = "SELECT A.ATTNAME AS ATTNAME, SYS_CATALOG.FORMAT_TYPE(A.ATTTYPID, A.ATTTYPMOD) AS FORMAT_TYPE, A.ATTNOTNULL AS ATTNOTNULL, A.ATTNUM AS ATTNUM"
			    + " FROM SYS_CATALOG.SYS_ATTRIBUTE A " + " LEFT JOIN SYS_CLASS C ON C.OID = A.ATTRELID" + " LEFT JOIN SYS_NAMESPACE N ON C.RELNAMESPACE = N.OID" + " WHERE N.NSPNAME = '"
			    + schema + "'" + " AND C.RELNAME = '" + tableName + "'" + " AND A.ATTNUM > 0" + " AND NOT A.ATTISDROPPED" + " ORDER BY A.ATTNUM;";
		    ResultSet def = stm.executeQuery(sql);
		    while (def.next()) {
			String attname = def.getString("ATTNAME");
			String formatType = def.getString("FORMAT_TYPE");
			String attnotnull = def.getString("ATTNOTNULL");
			// String attnum = def.getString("ATTNUM");
			buffer.append(attname).append("  ");
			buffer.append(formatType).append("  ");
			if (attnotnull.equals("true")) {
			    attnotnull = " NOT NULL"; //$NON-NLS-1$
			    buffer.append(attnotnull).append("  ");
			}
			for (String primary : pkList) {
			    if (primary.equalsIgnoreCase(attname)) {
				buffer.append(" primary key ");
				break;
			    }
			}
			buffer.append(',').append("\n");
		    }
		    String sqlq = buffer.toString().substring(0, buffer.toString().length() - 2) + "\n)"; //$NON-NLS-1$

		    rs.close();
		    pk.close();

		    String schemaTerm = sourceCon.getMetaData().getSchemaTerm();
		    if (!schemaTerm.equals("")) { //$NON-NLS-1$
			Statement stmCon = sourceCon.createStatement();
			stmCon.execute(sqlq);
		    }
		}
	    }
	    // if (connection != null) {
	    // connection.close();
	    // }
	} catch (SQLException e) {
	    e.printStackTrace();
	    try {
		    sourceCon.rollback();
		} catch (SQLException e1) {
		    e1.printStackTrace();
		}
	    MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage());
	    return false;
	}
	return true;
    }

    /**
     * 得到所有不存在的表以及模式
     */
    private List<List<String>> getTableNotExist() {

	Object[] objs = cTreeView.getCheckedElements();
	List<String> tableList = new ArrayList<String>();// 所有不重复的表
	List<List<String>> list = new ArrayList<List<String>>();// 每个模式都拥有一个list，构成全部集合
	for (int i = 0; i < objs.length; i++) {
	    Object objecj = objs[i];
	    TreeItem ti = (TreeItem) cTreeView.testFindItem(objecj);
	    ReleaseDataInfo dataObj = (ReleaseDataInfo) ti.getData();
	    if (dataObj.getReleaseName() != null) {
		List<String> tableNameList = dataObj.getTableNameList();
		for (String str : tableNameList) {
		    if (!tableList.contains(str)) {
			tableList.add(str);
		    }
		}
	    }
	}
	// 第一是模式，第二是表名
	Collections.sort(tableList);
	if (tableList.size() == 0) {
	    return null;
	}
	String flag = (tableList.get(0).split("/"))[0]; //$NON-NLS-1$
	List<String> table = new ArrayList<String>();
	table.add(flag);
	for (String str : tableList) {
	    String[] split = str.split("/"); //$NON-NLS-1$

	    if (!flag.equals(split[0])) {

		flag = (split[0]);
		list.add(table);
		table = new ArrayList<String>();
		table.add(split[0]);
	    }
	    table.add(split[1]);
	}
	list.add(table);
	if (sourceCon != null) {
	    try {
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) { //$NON-NLS-1$
		    Statement stm = sourceCon.createStatement();
		    ResultSet resultSet = null;
		    for (List<String> strList : list) {
			String schema = strList.get(0);
			StringBuffer tableName = new StringBuffer();
			for (int i = 1; i < strList.size(); i++) {
			    tableName.append("'").append(strList.get(i)).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
			    if (i != (strList.size() - 1)) {
				tableName.append(","); //$NON-NLS-1$
			    }
			}
			resultSet = stm.executeQuery("SELECT TABLENAME FROM SYS_TABLES WHERE TABLENAME IN (" + tableName //$NON-NLS-1$
				+ ") AND SCHEMANAME= '" + schema + "'"); //$NON-NLS-1$ //$NON-NLS-2$
			while (resultSet.next()) {
			    strList.remove(resultSet.getString("TABLENAME"));// 直接将查到的表去掉，剩下的就是不存在的表，模式 //$NON-NLS-1$
			}
		    }
		    if (resultSet != null) {
			resultSet.close();
		    }
		    stm.close();
		}
	    } catch (SQLException e) {
		e.printStackTrace();
		MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		return null;
	    }
	}
	return list;
    }

    /**
     * 验证订阅名称是否重复
     */
    private boolean checkRepeat() {
	if (!subscribeNameT.getText().equals("") && sourceCon != null) { //$NON-NLS-1$
	    String count = "0"; //$NON-NLS-1$
	    try {
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) { //$NON-NLS-1$
		    Statement stm = sourceCon.createStatement();
		    ResultSet resultSet = null;
		    resultSet = stm.executeQuery("select count(*) from syslogical.subscription where sub_name = \'" //$NON-NLS-1$
			    + subscribeNameT.getText() + "\'"); //$NON-NLS-1$
		    while (resultSet.next()) {
			count = resultSet.getString("count"); //$NON-NLS-1$
		    }
		    resultSet.close();
		    stm.close();
		    if (resultSet != null) {
			resultSet.close();
		    }
		}
	    } catch (SQLException e) {
		e.printStackTrace();
		MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		return false;
	    }
	    if (type.equals("create") && !count.equals("0")) { //$NON-NLS-1$//$NON-NLS-2$
		MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
			subscribeNameT.getText() + Messages.CreateSubscribeDialog_please_rename);
		subscribeNameT.setFocus();
		return false;
	    }
	}
	return true;
    }

    private void iniTableViewer() {

	Connection connection = DatabaseUtil.getConnection(CurrSourceMeta, databaseC.getText());
	treeList.clear();
	if (connection != null) {
	    try {
		DatabaseMetaData metaData = connection.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) { //$NON-NLS-1$
		    Statement stm1 = connection.createStatement();
		    Statement stmTable = connection.createStatement();// 查找发布的表
		    ResultSet rs0 = null;
		    ResultSet rsTable = null;
		    rs0 = stm1.executeQuery("SELECT SET_NAME FROM SYSLOGICAL.REPLICATION_SET"); //$NON-NLS-1$
		    while (rs0.next()) {
			ReleaseDataInfo sourceMetaChild = new ReleaseDataInfo();
			String release = rs0.getString("SET_NAME"); //$NON-NLS-1$
			rsTable = stmTable.executeQuery("SELECT (NSPNAME || '/' || RELNAME) AS TALENAME,RELID FROM SYSLOGICAL.TABLES WHERE SET_NAME IN ('" //$NON-NLS-1$
				+ release + "') ORDER BY TALENAME"); //$NON-NLS-1$
			while (rsTable.next()) {
			    sourceMetaChild.getTableNameList().add(rsTable.getString("TALENAME")); //$NON-NLS-1$
			}
			sourceMetaChild.setReleaseName(release);
			treeList.add(sourceMetaChild);
		    }
		    stm1.close();
		    stmTable.close();
		    if (rs0 != null) {
			rs0.close();
		    }
		}
		// if (connection != null) {
		// connection.close();
		// }
	    } catch (SQLException e) {
		e.printStackTrace();
		MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
	    }
	}
	cTreeView.doSetInput(treeList);
	cTreeView.refresh();
	cTreeView.expandAll();
    }

    private void initCreateViewData() {
	for (int i = 0, n = listEle.size(); i < n; i++) {
	    ReleaseDataSource releaseDataSource = new ReleaseDataSource();
	    Element element = listEle.get(i);
	    if (element.element("nodeId") != null) { //$NON-NLS-1$
		continue;
	    }
	    releaseDataSource.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
	    releaseDataSource.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$

	    releaseServerC.add(releaseDataSource.getDbName());
	    serverList.add(releaseDataSource);
	}
	if (serverList.size() > 0) {
	    for (ReleaseDataSource subscribe : serverList) {
		Connection connection = DatabaseUtil.getConnection(subscribe, UIUtils.getDatabase()); //$NON-NLS-1$
		if (connection != null) {
		    try {
			DatabaseMetaData metaData = connection.getMetaData();
			String schemaTerm = metaData.getSchemaTerm();
			if (!schemaTerm.equals("")) { //$NON-NLS-1$
			    Statement stm1 = connection.createStatement();
			    ResultSet rs0 = null;
			    rs0 = stm1.executeQuery("SELECT DATNAME,OID FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN"); //$NON-NLS-1$
			    while (rs0.next()) {
				SubscribeDataBase sourceMetaChild = new SubscribeDataBase();
				String datename = rs0.getString("DATNAME"); //$NON-NLS-1$
				String oid = rs0.getString("OID"); //$NON-NLS-1$
				sourceMetaChild.setDatabaseName(datename);
				sourceMetaChild.setDatabaseOid(oid);
				subscribe.addChild(sourceMetaChild);
			    }
			    stm1.close();
			    if (rs0 != null) {
				rs0.close();
			    }
			}
			// if (connection != null) {
			// connection.close();
			// }
		    } catch (SQLException e) {
			e.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		    }
		}
	    }
	} else {
	    return;
	}
	CurrSourceMeta = serverList.get(0);
    }

    public ReleaseDataSource getCurrSourceMeta() {
	return CurrSourceMeta;
    }

    public void setCurrSourceMeta(ReleaseDataSource currSourceMeta) {
	CurrSourceMeta = currSourceMeta;
    }

    public List<ReleaseDataInfo> getTreeList() {
	return treeList;
    }

    public void setTreeList(List<ReleaseDataInfo> treeList) {
	this.treeList = treeList;
    }

    public String getDatabaseText() {
	return databaseText;
    }

    public void setDatabaseText(String databaseText) {
	this.databaseText = databaseText;
    }

    public CCheckboxTreeViewer getcTreeView() {
	return cTreeView;
    }

    public void setcTreeView(CCheckboxTreeViewer cTreeView) {
	this.cTreeView = cTreeView;
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
    public void doSave(IProgressMonitor monitor) {
	// TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
	// TODO Auto-generated method stub

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
    public void setFocus() {
    }
}
