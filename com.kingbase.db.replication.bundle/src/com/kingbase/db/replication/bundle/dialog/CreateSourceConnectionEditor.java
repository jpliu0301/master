package com.kingbase.db.replication.bundle.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;
import org.pentaho.di.util.SWTUtil;
import org.pentaho.di.viewer.CBasicTreeViewer;
import org.pentaho.di.viewer.CTableTreeNode;

import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataBase;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.ReplicationFile;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;
import com.kingbase.db.replication.bundle.views.ReplicationView;

public class CreateSourceConnectionEditor extends EditorPart {

    public static final String ID = "com.kingbase.db.replication.bundle.dialog.CreateSourceConnectionEditor";
    private Text txtName;
    private Text txtAddress;
    private Text txtPort;
    private Text txtUserName;
    private Text txtPassword;
    private String txtNameStr = ""; //$NON-NLS-1$
    private String txtAddressStr = ""; //$NON-NLS-1$
    private String txtPortStr = ""; //$NON-NLS-1$
    private String txtUserNameStr = ""; //$NON-NLS-1$
    private String txtPasswordStr = ""; //$NON-NLS-1$
    private Button btnIsSave;
    private boolean isSaveP = true;// 是否保存密码
    private ReleaseDataSource sourceMeta;
    private Text txtDriverName;
    private Text txtDriverPath;
    private String driverName = "com.kingbase8.Driver";
    private String driverPath = "";
    private String type;
    private File file;
    private ReleaseDataSource releaseSource;
    private SubscribeDataSource subscribeSource;
    private DataBaseInput input;
    private ReplicationFile tfFolder;
    private CBasicTreeViewer dbReplicationTree;
    private FormToolkit toolkit;

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	setSite(site);
	setInput(input);
	setPartName(input.getName());
	this.input = (DataBaseInput) input;
	CTableTreeNode node = this.input.getNode();
	if (node instanceof ReleaseDataSource) {// 更新发布数据库
	    this.releaseSource = (ReleaseDataSource) node;

	    this.tfFolder = (ReplicationFile) releaseSource.getParentModel();
	    this.file = tfFolder.getFile(); //$NON-NLS-1$
	    this.type = "release";
	} else if (node instanceof SubscribeDataSource) {// 更新订阅数据库
	    this.subscribeSource = (SubscribeDataSource) node;
	    this.tfFolder = (ReplicationFile) subscribeSource.getParentModel();
	    this.file = tfFolder.getFile(); //$NON-NLS-1$
	    this.type = "subscribe";
	} else if (node instanceof ReplicationFile) {
	    this.tfFolder = (ReplicationFile) node;
	    boolean isRelease = tfFolder.isRelease();
	    if (isRelease) {// 创建发布数据库
		this.type = "release";
		this.file = tfFolder.getFile(); //$NON-NLS-1$
	    } else {// 创建订阅数据库
		this.type = "subscribe";
		this.file = tfFolder.getFile(); //$NON-NLS-1$
	    }
	}
	this.dbReplicationTree = this.input.getTreeView();

	sourceMeta = new ReleaseDataSource();
	if (releaseSource != null) {

	    txtNameStr = releaseSource.getDbName();
	    txtAddressStr = releaseSource.getDbServer();
	    txtPortStr = releaseSource.getDbPort();
	    txtUserNameStr = releaseSource.getDbUser();
	    txtPasswordStr = releaseSource.getDbPasswrod();
	    isSaveP = releaseSource.isSaveP();
	    driverName = releaseSource.getDriverName();
	    driverPath = releaseSource.getDriverPath();
	} else if (subscribeSource != null) {

	    txtNameStr = subscribeSource.getDbName();
	    txtAddressStr = subscribeSource.getDbServer();
	    txtPortStr = subscribeSource.getDbPort();
	    txtUserNameStr = subscribeSource.getDbUser();
	    txtPasswordStr = subscribeSource.getDbPasswrod();
	    isSaveP = subscribeSource.isSaveP();
	    driverName = subscribeSource.getDriverName();
	    driverPath = subscribeSource.getDriverPath();
	} else {
	    driverPath = getFileInBundle("driver/kingbase8-8.2.0.jar").toString(); //$NON-NLS-1$
	}
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
	parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	IManagedForm managedForm = new ManagedForm(parent);
	toolkit = managedForm.getToolkit();
	final ScrolledForm form = managedForm.getForm();
	GridData data1 = new GridData(GridData.FILL_BOTH);
	form.setLayoutData(data1);
	GridLayout layout = new GridLayout();
	form.getBody().setLayout(layout);
	toolkit.decorateFormHeading(form.getForm());

	Composite compositeInfo = new Composite(form.getBody(), SWT.NONE);
	GridLayout layout1 = new GridLayout(2, false);
	layout1.marginTop = 20;
	layout1.marginLeft = 30;
	layout1.marginRight = 30;
	layout1.marginBottom = 20;
	layout1.horizontalSpacing = 20;
	layout1.verticalSpacing = 20;
	compositeInfo.setLayout(layout1);
	GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
	compositeInfo.setLayoutData(data);
	compositeInfo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbName1 = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_Drive_name, SWT.NONE);
	lbName1.setLayoutData(new GridData());
	txtDriverName = new Text(compositeInfo, SWT.BORDER);
	data = new GridData(GridData.FILL_HORIZONTAL);
	txtDriverName.setLayoutData(data);
	txtDriverName.setText(driverName); // $NON-NLS-1$
	txtDriverName.setTextLimit(63);
	UIUtils.verifyTextNotSpace(txtDriverName);

	Label lbDriverPath = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_Drive_path, SWT.NONE);
	lbDriverPath.setLayoutData(new GridData());

	Composite compositePath = new Composite(compositeInfo, SWT.None);
	GridLayout layout21 = new GridLayout(2, false);
	layout21.horizontalSpacing = 2;
	layout21.verticalSpacing = 0;
	layout21.marginTop = 0;
	compositePath.setLayout(layout21);
	GridData dataw = new GridData(GridData.FILL_HORIZONTAL);
	compositePath.setLayoutData(dataw);
	compositePath.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	txtDriverPath = new Text(compositePath, SWT.BORDER | SWT.READ_ONLY);
	dataw = new GridData(GridData.FILL_HORIZONTAL);
	txtDriverPath.setLayoutData(dataw);
	txtDriverPath.setText(driverPath);
	Button btn1 = new Button(compositePath, SWT.NONE);
	btn1.setText(Messages.CreateSourceConnectionDialog_browse);
	btn1.setLayoutData(new GridData());

	btn1.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FileDialog dialog = new FileDialog(compositePath.getShell());
		dialog.setFilterExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
		String path = dialog.open();
		String orderT = txtDriverPath.getText();
		txtDriverPath.setText(path == null ? (orderT == null ? "" : orderT) : path); //$NON-NLS-1$
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	Label lbName = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_name, SWT.NONE);
	lbName.setLayoutData(new GridData());
	txtName = new Text(compositeInfo, SWT.BORDER);
	data = new GridData(GridData.FILL_HORIZONTAL);
	txtName.setLayoutData(data);
	txtName.setTextLimit(63);
	txtName.setText(txtNameStr);
	UIUtils.verifyTextNotSpace(txtName);
	UIUtils.verifyText(txtName);

	Label lbAddress = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_address, SWT.NONE);
	lbAddress.setLayoutData(new GridData());
	txtAddress = new Text(compositeInfo, SWT.BORDER);
	txtAddress.setLayoutData(data);
	txtAddress.setTextLimit(63);
	txtAddress.setText(txtAddressStr);
	UIUtils.verifyTextNotSpace(txtAddress);

	Label lbPort = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_port, SWT.NONE);
	lbPort.setLayoutData(new GridData());
	txtPort = new Text(compositeInfo, SWT.BORDER);
	txtPort.setLayoutData(data);
	txtPort.setText(txtPortStr);
	txtPort.setTextLimit(63);
	UIUtils.verifyTextNumber(txtPort);

	Label lbUserName = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_user, SWT.NONE);
	lbUserName.setLayoutData(new GridData());
	txtUserName = new Text(compositeInfo, SWT.BORDER);
	txtUserName.setLayoutData(data);
	txtUserName.setText(txtUserNameStr);
	txtUserName.setTextLimit(63);
	UIUtils.verifyTextNotSpace(txtUserName);

	Label lbPassword = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_password, SWT.NONE);
	lbPassword.setLayoutData(new GridData());
	txtPassword = new Text(compositeInfo, SWT.BORDER | SWT.PASSWORD);
	txtPassword.setLayoutData(data);
	txtPassword.setText(txtPasswordStr);
	txtPassword.setTextLimit(63);

	Label lbIsSave = toolkit.createLabel(compositeInfo, Messages.CreateSourceConnectionDialog_save_password, SWT.NONE);
	lbIsSave.setLayoutData(new GridData());
	btnIsSave = toolkit.createButton(compositeInfo, "", SWT.CHECK);
	btnIsSave.setSelection(isSaveP);

	Composite compOpera = new Composite(form.getBody(), SWT.NONE);
	GridLayout layout11 = new GridLayout(5, false);
	layout11.marginBottom = 17;
	layout11.marginRight = 20;
	compOpera.setLayout(layout11);
	GridData data11 = new GridData(SWT.FILL, SWT.FILL, true, false);
	compOpera.setLayoutData(data11);
	Label label = toolkit.createLabel(compOpera, "", SWT.None);
	data11 = new GridData(GridData.FILL_HORIZONTAL);
	data11.horizontalSpan = 2;
	label.setLayoutData(data11);
	compOpera.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Button btnCheck = new Button(compOpera, SWT.PUSH);
	btnCheck.setText(Messages.CreateSourceConnectionDialog_check);
	data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnCheck.setLayoutData(data11);
	((GridData) btnCheck.getLayoutData()).widthHint = 61;

	btnCheck.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		isCheck(false);
	    }
	});

	Button btnConfirm = new Button(compOpera, SWT.PUSH);
	btnConfirm.setText(Messages.CreateSourceConnectionDialog_confirm);
	data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnConfirm.setLayoutData(data11);
	((GridData) btnConfirm.getLayoutData()).widthHint = 61;

	btnConfirm.addSelectionListener(new SelectionAdapter() {

	    public void widgetSelected(SelectionEvent e) {
		boolean check = isCheck(true);
		if (check) {
		    setDataSourceMeta();

		    if (releaseSource != null) {
			updateReleaseServer();
		    } else if (subscribeSource != null) {
			updateSubscribeServer();
		    } else if (tfFolder.isRelease()) {
			createReleaseServer();
		    } else if (!tfFolder.isRelease()) {
			createSubscribeServer();
		    }
		    UIUtils.closeEditor(CreateSourceConnectionEditor.this);
		}
	    }
	});

	Button btnCancel = new Button(compOpera, SWT.PUSH);
	btnCancel.setText(Messages.CreateSourceConnectionDialog_cancel);
	data11 = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnCancel.setLayoutData(data11);
	((GridData) btnCancel.getLayoutData()).widthHint = 61;

	btnCancel.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		UIUtils.closeEditor(CreateSourceConnectionEditor.this);
	    }
	});
    }

    protected void updateSubscribeServer() {
	SubscribeDataSource subscribeMeta = new SubscribeDataSource();

	subscribeMeta.setDbName(sourceMeta.getDbName());
	subscribeMeta.setDbServer(sourceMeta.getDbServer());
	subscribeMeta.setDbPort(sourceMeta.getDbPort());
	subscribeMeta.setDbUser(sourceMeta.getDbUser());
	subscribeMeta.setDbPasswrod(sourceMeta.getDbPasswrod());
	subscribeMeta.setDriverName(sourceMeta.getDriverName());
	subscribeMeta.setDriverPath(sourceMeta.getDriverPath());
	subscribeMeta.setSaveP(sourceMeta.isSaveP());

	tfFolder.removeChild(subscribeSource);
	tfFolder.addChild(subscribeMeta);
	tfFolder.setHasInit(true);
	dbReplicationTree.refresh();
	ReplicationView.updateSubscribeXml(subscribeSource, subscribeMeta);
    }

    protected void updateReleaseServer() {
	tfFolder.removeChild(releaseSource);
	tfFolder.addChild(sourceMeta);
	tfFolder.setHasInit(true);
	dbReplicationTree.refresh();
	ReplicationView.updateReleaseXml(releaseSource, sourceMeta);
    }

    protected void createSubscribeServer() {
	SubscribeDataSource subscribeMeta = new SubscribeDataSource();

	subscribeMeta.setDbName(sourceMeta.getDbName());
	subscribeMeta.setDbServer(sourceMeta.getDbServer());
	subscribeMeta.setDbPort(sourceMeta.getDbPort());
	subscribeMeta.setDbUser(sourceMeta.getDbUser());
	subscribeMeta.setDbPasswrod(sourceMeta.getDbPasswrod());
	subscribeMeta.setDriverName(sourceMeta.getDriverName());
	subscribeMeta.setDriverPath(sourceMeta.getDriverPath());
	tfFolder.addChild(subscribeMeta);
	tfFolder.setHasInit(true);
	SWTUtil.asyncExecThread(new Runnable() {
	    public void run() {
		SAXReader reader = new SAXReader();
		try {
		    File fileLocal = file;
		    Document document = reader.read(fileLocal);
		    Element root = document.getRootElement();
		    Element eleConnection = root.addElement("subscribe"); //$NON-NLS-1$

		    Element element = eleConnection.addElement("name"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDbName());

		    element = eleConnection.addElement("server"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDbServer());

		    element = eleConnection.addElement("port"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDbPort());

		    element = eleConnection.addElement("username"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDbUser());

		    element = eleConnection.addElement("password"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDbPasswrod());

		    element = eleConnection.addElement("isSaveP"); //$NON-NLS-1$
		    element.addText(sourceMeta.isSaveP() ? "true" : "false"); //$NON-NLS-1$//$NON-NLS-2$

		    element = eleConnection.addElement("driverName"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDriverName());

		    element = eleConnection.addElement("driverPath"); //$NON-NLS-1$
		    element.addText(subscribeMeta.getDriverPath());

		    eleConnection.addElement("nodes");

		    OutputFormat xmlFormat = UIUtils.xmlFormat();
		    XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
		    output.write(document);
		    output.close();

		    dbReplicationTree.expandToLevel(3);
		    tfFolder.refresh();
		    dbReplicationTree.refresh();
		} catch (DocumentException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    protected void createReleaseServer() {

	tfFolder.addChild(sourceMeta);
	tfFolder.setHasInit(true);
	SWTUtil.asyncExecThread(new Runnable() {

	    public void run() {
		Connection sourceCon = DatabaseUtil.getConnection(sourceMeta, UIUtils.getDatabase());
		List<List<String>> list = getDatabaseInfo(sourceCon);
		SAXReader reader = new SAXReader();
		try {
		    File fileLocal = file;
		    Document document = reader.read(fileLocal);
		    Element root = document.getRootElement();
		    Element eleConnection = root.addElement("release"); //$NON-NLS-1$

		    Element element = eleConnection.addElement("name"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDbName());

		    element = eleConnection.addElement("server"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDbServer());

		    element = eleConnection.addElement("port"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDbPort());

		    element = eleConnection.addElement("username"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDbUser());

		    element = eleConnection.addElement("password"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDbPasswrod());

		    element = eleConnection.addElement("isSaveP"); //$NON-NLS-1$
		    element.addText(sourceMeta.isSaveP() ? "true" : "false"); //$NON-NLS-1$//$NON-NLS-2$

		    element = eleConnection.addElement("driverName"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDriverName());

		    element = eleConnection.addElement("driverPath"); //$NON-NLS-1$
		    element.addText(sourceMeta.getDriverPath());

		    element = eleConnection.addElement("nodes");
		    if (list.size() > 0) {
			for (List<String> list2 : list) {
			    if (list2.size() == 2) {
				Element addElement = element.addElement("node");

				Element eleNodeId = addElement.addElement("nodeId");
				eleNodeId.addText(list2.get(1));

				eleNodeId = addElement.addElement("dbname");
				eleNodeId.addText(list2.get(0));
			    }
			}
		    }
		    OutputFormat xmlFormat = UIUtils.xmlFormat();
		    XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
		    output.write(document);
		    output.close();

		    dbReplicationTree.expandToLevel(3);
		    tfFolder.refresh();
		    dbReplicationTree.refresh();
		} catch (DocumentException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	});
    }

    @Override
    public void setFocus() {

    }

    protected void setDataSourceMeta() {
	sourceMeta.setDbName(txtName.getText());
	sourceMeta.setDbServer(txtAddress.getText());
	sourceMeta.setDbPort(txtPort.getText());
	sourceMeta.setDbUser(txtUserName.getText());
	sourceMeta.setDbPasswrod(txtPassword.getText());
	sourceMeta.setSaveP(btnIsSave.getSelection());
	sourceMeta.setDriverName(txtDriverName.getText());
	sourceMeta.setDriverPath(txtDriverPath.getText());
    }

    private boolean isCheck(boolean flag) {

	if (txtName.getText().trim().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_name_is_null);
	    return false;
	}
	if (txtAddress.getText().trim().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_address_is_null);
	    return false;
	}
	boolean verifyIP = UIUtils.verifyIP(txtAddress);
	if (!verifyIP) {
	    return false;
	}
	if (txtPort.getText().trim().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_port_is_null);
	    return false;
	}
	if (txtUserName.getText().trim().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_user_is_null);
	    return false;
	}
	if (txtPassword.getText().trim().equals("")) { //$NON-NLS-1$
	    MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_password_is_null);
	    return false;
	}
	
	if ((releaseSource == null && subscribeSource == null) || (releaseSource != null && !txtName.getText().equals(releaseSource.getDbName()))
		|| (subscribeSource != null && !txtName.getText().equals(subscribeSource.getDbName()))) {

	    SAXReader reader = new SAXReader();
	    try {
		File fileLocal = file;
		Document document = reader.read(fileLocal);
		Element root = document.getRootElement();
		List<Element> listEle = root.elements();
		if (listEle.size() > 0) {
		    for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);
			String str = element.element("name").getStringValue();
			if (str.equals(txtName.getText())) { //$NON-NLS-1$
			    MessageDialog.openWarning(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_Please_Rename);
			    return false;
			}
			String Ip = element.element("server").getStringValue();
			String Port = element.element("port").getStringValue();
			String User = element.element("username").getStringValue();
			String Pwd = element.element("password").getStringValue();
			if (!txtNameStr.equals(str) && Ip.equals(txtAddress.getText())
				&& Port.equals(txtPort.getText())
				&& User.equals(txtUserName.getText())
				&& Pwd.equals(txtPassword.getText())
				) { //$NON-NLS-1$
			    MessageDialog.openWarning(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, "存在相同ip、port、user、password的服务器，请检查");
			    return false;
			}
		    }
		}
	    } catch (DocumentException e) {
		e.printStackTrace();
		MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage()); //$NON-NLS-1$
		return false;
	    }
	}

	Connection connection = null;
	if (type.equals("release")) { //$NON-NLS-1$
	    ReleaseDataSource source = new ReleaseDataSource();

	    source.setDbName(txtName.getText());
	    source.setDbServer(txtAddress.getText());
	    source.setDbPort(txtPort.getText());
	    source.setDbUser(txtUserName.getText());
	    source.setDbPasswrod(txtPassword.getText());
	    source.setSaveP(btnIsSave.getSelection());
	    source.setDriverName(txtDriverName.getText());
	    source.setDriverPath(txtDriverPath.getText());

	    connection = DatabaseUtil.getConnection(source, UIUtils.getDatabase()); //$NON-NLS-1$
	} else {
	    SubscribeDataSource source = new SubscribeDataSource();

	    source.setDbName(txtName.getText());
	    source.setDbServer(txtAddress.getText());
	    source.setDbPort(txtPort.getText());
	    source.setDbUser(txtUserName.getText());
	    source.setDbPasswrod(txtPassword.getText());
	    source.setSaveP(btnIsSave.getSelection());
	    source.setDriverName(txtDriverName.getText());
	    source.setDriverPath(txtDriverPath.getText());

	    connection = DatabaseUtil.getConnection(source, UIUtils.getDatabase()); //$NON-NLS-1$
	}
	if (flag) {
	    if (connection == null) {
		MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, "服务器连接失败");
		return false;
	    }
	} else {
	    if (connection == null) {
		MessageDialog.openError(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, "服务器连接失败");
		return false;
	    } else {
		MessageDialog.openInformation(UIUtils.getActiveShell(), Messages.CreateSourceConnectionDialog_Prompt, Messages.CreateSourceConnectionDialog_Server_test_connection_successful);
	    }
	}
	if (connection != null) {
	    try {
		connection.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return true;
    }

    public Text getTxtName() {
	return txtName;
    }

    public Text getTxtAddress() {
	return txtAddress;
    }

    public Text getTxtPort() {
	return txtPort;
    }

    public Text getTxtUserName() {
	return txtUserName;
    }

    public Text getTxtPassword() {
	return txtPassword;
    }

    public Button getBtnIsSave() {
	return btnIsSave;
    }

    public boolean isSaveP() {
	return isSaveP;
    }

    public ReleaseDataSource getSourceMeta() {
	return sourceMeta;
    }

    /**
	 *   
	 */
    public File getFileInBundle(String path) {
	return PlatformUtil.getConfigurationFile(KBReplicationCore.PLUGIN_ID, path);
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    }

    @Override
    public void doSaveAs() {
    }

    private List<List<String>> getDatabaseInfo(Connection sourceCon) {
	List<List<String>> list = new ArrayList<List<String>>();
	if (sourceCon != null) {
	    DatabaseMetaData metaData;
	    try {
		metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) {
		    Statement stm1 = sourceCon.createStatement();
		    ResultSet rs0 = null;
		    rs0 = stm1.executeQuery("SELECT DATNAME FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN ORDER BY DATNAME");
		    while (rs0.next()) {
			List<String> list1 = new ArrayList<String>();
			String datename = rs0.getString("DATNAME"); //$NON-NLS-1$
			list1.add(datename);
			try {
			    Connection con = DatabaseUtil.getConnection(sourceMeta, datename);
			    if (con != null) {
				DatabaseMetaData meta = con.getMetaData();
				String sch = meta.getSchemaTerm();
				if (!sch.equals("")) {
				    String nodeId = null;
				    Statement stm = con.createStatement();
				    ResultSet resultSet = stm.executeQuery("SELECT NODE_ID FROM SYSLOGICAL.LOCAL_NODE");
				    while (resultSet.next()) {
					nodeId = resultSet.getString("NODE_ID");
				    }
				    if (nodeId != null) {
					list1.add(nodeId);
				    }
				}
			    }
			} catch (SQLException e) {
			    e.printStackTrace();
			}
			list.add(list1);
		    }
		    stm1.close();
		    if (rs0 != null) {
			rs0.close();
		    }
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
	return list;
    }

    public static ReleaseDataBase setDataSourceMetaInfo(ReleaseDataBase metaChild, ReleaseDataSource meta) {
	metaChild.setDbName(meta.getDbName());
	metaChild.setDbServer(meta.getDbServer());
	metaChild.setDbPort(meta.getDbPort());
	metaChild.setDbUser(meta.getDbUser());
	metaChild.setDbPasswrod(meta.getDbPasswrod());
	metaChild.setDriverName(meta.getDriverName());
	metaChild.setDriverPath(meta.getDriverPath());
	return metaChild;
    }
}
