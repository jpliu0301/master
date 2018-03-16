package com.kingbase.db.deploy.bundle.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.pentaho.di.util.SWTUtil;
import org.pentaho.di.viewer.CTableTreeNode;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.kingbase.db.core.editorinput.DataBaseInput;
import com.kingbase.db.core.util.JschUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.DeployRoot;
import com.kingbase.db.deploy.bundle.model.tree.MasterStandbyEntity;
import com.kingbase.db.deploy.bundle.model.tree.NodeEntity;

/**
 * 节点编辑器
 * 
 * @author feng
 *
 */
public class CreateNodeEditor extends EditorPart {
    public CreateNodeEditor() {
    }

    private CNodeEntity oldEntity;
    private String type;
    private Text txtName;
    private Text txtIp;
    private Text txtPort;
    private Text txtRoot;
    private Text txtUser;
    private Text txtPath;
    private String netcard ;
    private String gateway ;
    @Override
    public void doSave(IProgressMonitor monitor) {
	// TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
	// TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
	setSite(site);
	setInput(input);
	setPartName(input.getName());
	if (input.getName().equals("新建节点")) {
	    type = "new";
	} else {
	    type = "update";
	    this.oldEntity = (CNodeEntity) ((DataBaseInput) getEditorInput()).getNode();
	}
    }

    @Override
    public boolean isDirty() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public void createPartControl(final Composite parent) {
	parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
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

	TabFolder tabFolder = new TabFolder(container, SWT.NONE);
	tabFolder.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

	TabItem baseItem = new TabItem(tabFolder, SWT.NONE);
	baseItem.setText("基本属性");
	Composite composite = new Composite(tabFolder, SWT.NONE);
	composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	baseItem.setControl(composite);

	GridLayout layout1 = new GridLayout(2, false);
	layout1.marginTop = 12;
	layout1.marginLeft = 13;
	layout1.marginRight = 7;
	layout1.marginBottom = 11;
	layout1.verticalSpacing = 15;
	composite.setLayout(layout1);
	composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

	Label lbName = new Label(composite, SWT.NONE);
	lbName.setText("显示名称:");
	lbName.setLayoutData(new GridData());
	txtName = new Text(composite, SWT.BORDER);
	GridData dataText = new GridData();
	dataText.widthHint = 250;
	txtName.setLayoutData(dataText);
	lbName.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbIP = new Label(composite, SWT.NONE);
	lbIP.setText("IP地址:");
	lbIP.setLayoutData(new GridData());
	txtIp = new Text(composite, SWT.BORDER);
	txtIp.setLayoutData(dataText);
	txtIp.setText("127.0.0.1");
	lbIP.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbPort = new Label(composite, SWT.NONE);
	lbPort.setText("端口:");
	lbPort.setLayoutData(new GridData());
	txtPort = new Text(composite, SWT.BORDER);
	txtPort.setLayoutData(dataText);
	txtPort.setText("22");
	lbPort.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbRoot = new Label(composite, SWT.NONE);
	lbRoot.setText("Root密码:");
	lbRoot.setLayoutData(new GridData());
	txtRoot = new Text(composite, SWT.PASSWORD | SWT.BORDER);
	txtRoot.setLayoutData(dataText);
	lbRoot.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbUser = new Label(composite, SWT.NONE);
	lbUser.setText("常规用户:");
	lbUser.setLayoutData(new GridData());
	txtUser = new Text(composite, SWT.BORDER);
	txtUser.setLayoutData(dataText);
	txtUser.setText("kingbase");
	txtUser.addModifyListener(new ModifyListener() {

	    @Override
			public void modifyText(ModifyEvent e) {
				if (!txtUser.getText().trim().equals("") && txtPath != null) {
					String[] split = txtPath.getText().split("/");
					if (split.length > 3) {
						split[2] = txtUser.getText();
					}
					StringBuffer buf = new StringBuffer("/");
					for (String str : split) {
						if (!str.trim().equals("")) {
							buf.append(str).append("/");
						}
					}
					txtPath.setText(buf.toString());
				}
			}
	});
	lbUser.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

	Label lbPath = new Label(composite, SWT.NONE);
	lbPath.setText("默认路径:");
	lbPath.setLayoutData(new GridData());
	txtPath = new Text(composite, SWT.BORDER);
	txtPath.setLayoutData(dataText);
	txtPath.setText("/home/kingbase/cluster/");
	lbPath.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
	txtPath.addListener(SWT.Verify, new Listener() {
	    public void handleEvent(Event e) {
		String string = e.text;
		if (string.equals(";")) {
		    e.doit = false;
		    return;
		}
	    }
	});

	if (oldEntity != null) {
	    txtName.setText(oldEntity.getName());
	    txtIp.setText(oldEntity.getIp());
	    txtPort.setText(oldEntity.getPort());
	    txtRoot.setText(oldEntity.getRootPass());
	    txtUser.setText(oldEntity.getUser());
	    String replaceAll = oldEntity.getNodePath().replaceAll("\\\\", "");
	    txtPath.setText(replaceAll);
	}

	if (!type.equals("new")) {
	    txtIp.setEnabled(false);
	    txtPort.setEnabled(false);
	    txtRoot.setEnabled(false);
	    txtUser.setEnabled(false);
	    txtPath.setEnabled(false);
	}
	UIUtils.verifyText(txtName);
	UIUtils.verifyTextNotSpace(txtIp);
	UIUtils.verifyTextNotSpace(txtPort);
	UIUtils.verifyTextNotSpace(txtUser);
	UIUtils.verifyTextNotSpace(txtPath);
	UIUtils.verifyTextNumber(txtPort);
	txtPort.setTextLimit(63);

	Composite compOpera = new Composite(parent, SWT.None);
	compOpera.setLayout(new GridLayout(5, false));
	GridData data = new GridData(GridData.FILL_HORIZONTAL);
	compOpera.setLayoutData(data);
	Label label = new Label(compOpera, SWT.None);
	data = new GridData(GridData.FILL_HORIZONTAL);
	data.horizontalSpan = 2;
	label.setLayoutData(data);

	Button btnCheck = new Button(compOpera, SWT.PUSH);
	btnCheck.setText("检查");
	data = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnCheck.setLayoutData(data);
	((GridData) btnCheck.getLayoutData()).widthHint = 61;
	btnCheck.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		CNodeEntity entity = new CNodeEntity();
		entity.setName(txtName.getText());
		entity.setIp(txtIp.getText());
		entity.setPort(txtPort.getText());
		entity.setRootPass(txtRoot.getText());
		entity.setUser(txtUser.getText());
		entity.setdPath(txtPath.getText().trim());
		entity.setNodePath(txtPath.getText().trim());

		boolean check = check(entity);
		boolean checkName = checkName(entity);
		if (!check || !checkName) {
		    return;
		}
		String ip = txtIp.getText();
		String port = txtPort.getText();
		String rootMiss = txtRoot.getText();

		if (ip.equals("0.0.0.0") || ip.equals("1.1.1.1") || ip.equals("255.255.255.255")) {
		    MessageDialog.openWarning(parent.getShell(), "提示", "不应该配置此类非设备IP");
		    return;
		}
		try {
		    Session session = JschUtil.connect(ip, Integer.parseInt(port), "root", rootMiss);
		    if (session != null) {
		    	
		    	 ChannelExec exec = JschUtil.execCommand(session, "ifconfig | grep -B1 '"+ entity.getIp() +"' | head -n 1 | awk '{print $1}'");
				     netcard = JschUtil.returnInputStream(exec);
				    if (netcard != null && netcard.trim().endsWith(":")) {
					   netcard = (netcard.trim().split(":"))[0];
				    }
				    
				    ChannelExec exec1 = JschUtil.execCommand(session, "route -n | grep  'UG' | grep '"+netcard+"'|awk '{print $2}'");//查询网关
					gateway = JschUtil.returnInputStream(exec1);
		    	
			session.disconnect();
			MessageDialog.openInformation(parent.getShell(), "提示", "节点测试连接成功！");
		    }
		} catch (JSchException e1) {
		    MessageDialog.openWarning(parent.getShell(), "提示", "节点测试连接错误！\n" + e1.getMessage());
		}
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {

	    }
	});

	Button btnApply = new Button(compOpera, SWT.PUSH);
	btnApply.setText("确定");
	data = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnApply.setLayoutData(data);
	((GridData) btnApply.getLayoutData()).widthHint = 61;
	btnApply.addSelectionListener(new SelectionListener() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {

		CNodeEntity entity = new CNodeEntity();
		String ip = txtIp.getText();
		entity.setName(txtName.getText());
		entity.setIp(ip);
		entity.setPort(txtPort.getText());
		entity.setRootPass(txtRoot.getText());
		entity.setUser(txtUser.getText());
		entity.setdPath(txtPath.getText().trim());
		entity.setNodePath(txtPath.getText().trim());
		boolean check = check(entity);
		boolean checkName = checkName(entity);
		entity.setdPath(entity.getdPath().replaceAll(" ", "\\\\ "));// 将空格转义为“\
									    // ”;
		entity.setNodePath(entity.getdPath());
		if (ip.equals("0.0.0.0") || ip.equals("1.1.1.1") || ip.equals("255.255.255.255")) {
		    if (ip.equals("0.0.0.0") || ip.equals("1.1.1.1") || ip.equals("255.255.255.255")) {
			MessageDialog.openWarning(parent.getShell(), "提示", "不应该配置此类非设备IP");
			return;
		    }
		}
		if (!check || !checkName) {
		    return;
		}
		String port = txtPort.getText();
		String rootMiss = txtRoot.getText();
		String user = txtUser.getText();
		String path = entity.getdPath();
		if (type.equals("new")) {
		try {
		    Session session = JschUtil.connect(ip, Integer.parseInt(port), "root", rootMiss);
		    Boolean flag1 = validExist(entity, session, "useradd -m " + user);
		    Boolean flag3 = updatePathOwner(session, user, path);// 修改目录的属主
			if (netcard == null && gateway == null){
		    	
		    ChannelExec exec = JschUtil.execCommand(session, "ifconfig | grep -B1 '"+ entity.getIp() +"' | head -n 1 | awk '{print $1}'");
		    String netcard = JschUtil.returnInputStream(exec);
		    if (netcard != null && netcard.trim().endsWith(":")) {
			netcard = (netcard.trim().split(":"))[0];
		    }
		    entity.setNetcard(netcard == null ? "" : netcard);
		    
		    ChannelExec exec1 = JschUtil.execCommand(session, "route -n | grep  'UG' | grep '"+entity.getNetcard()+"'|awk '{print $2}'");//查询网关
			String gateway = JschUtil.returnInputStream(exec1);
			if (gateway != null) {
				entity.setGateway(gateway);
			}
		    }else{
		    	entity.setNetcard(netcard == null ? "" : netcard);
		    	entity.setGateway(gateway == null ? "" : gateway);
		    }
//			boolean ping = ping(gateway);
//			if(!ping){
//				MessageDialog.openWarning(UIUtils.getActiveShell(), "此节点的网关无法ping通", "请检查对应网关的防火墙");
//			}
		    session.disconnect();
		    if (!(flag1 && flag3)) {
			return;
		    }
		    try {
				Session session1 = JschUtil.connect(ip, Integer.parseInt(port), "root", rootMiss);// root用户
				ChannelExec openChannel = JschUtil.execCommand(session1, "cd  /root/.ssh/; ls id_rsa");
				String value = JschUtil.returnInputStream1(openChannel);
				if (value.equals("")) {
				    JschUtil.execCommand1(session1, "ssh-keygen -t rsa -N "+"\"\""+" -f /root/.ssh/id_rsa;" + "chmod 600 /root/.ssh/id_rsa");
				}
				ChannelExec openChannel1 = JschUtil.execCommand(session1, "cd  /home/" + user + "/.ssh/; ls id_rsa");
				String value1 = JschUtil.returnInputStream1(openChannel1);
				if (value1.equals("")) {
				    JschUtil.execCommand1(session1, "su - " + user + " -c\"ssh-keygen -t rsa -N "+"\\\"\\\""+" -f /home/" + user + "/.ssh/id_rsa;" + "chmod 600 /home/" + user + "/.ssh/id_rsa\"");
				}
				openChannel1.disconnect();
				session1.disconnect();

			    } catch (NumberFormatException | JSchException e1) {
				e1.printStackTrace();
			    }
		} catch (JSchException e1) {
		    MessageDialog.openWarning(parent.getShell(), "提示", "节点连接错误！\n" + e1.getMessage());
		    return;
		}
		    CTableTreeNode node = ((DataBaseInput) getEditorInput()).getNode();
		    IFolder folder = ((NodeEntity) node).getFolder();
		    IFile file = (IFile) folder.findMember("cnode.xml");
		    ((NodeEntity) node).addChild(entity);

		    SWTUtil.asyncExecThread(new Runnable() {
			public void run() {
			    SAXReader reader = new SAXReader();
			    try {
				File fileLocal = file.getLocation().toFile();
				Document document = reader.read(fileLocal);
				Element root = document.getRootElement();
				Element eleConnection = root.addElement("cnode");

				Element element = eleConnection.addElement("name");
				element.addText(entity.getName());

				element = eleConnection.addElement("IP");
				element.addText(entity.getIp());

				element = eleConnection.addElement("port");
				element.addText(entity.getPort());

				element = eleConnection.addElement("rootPassword");
				element.addText(entity.getRootPass());

				element = eleConnection.addElement("user");
				element.addText(entity.getUser());

				element = eleConnection.addElement("defaultPath");
				element.addText(entity.getdPath());

				element = eleConnection.addElement("nodePath");
				element.addText(entity.getNodePath());
				
				element = eleConnection.addElement("netcard");
				element.addText(entity.getNetcard());
				
				element = eleConnection.addElement("gateway");
				element.addText(entity.getGateway());

				OutputFormat xmlFormat = UIUtils.xmlFormat();
				XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
				output.write(document);
				output.close();

				((NodeEntity) node).refresh();
				((DataBaseInput) getEditorInput()).getTreeView().refresh();
				((DataBaseInput) getEditorInput()).getTreeView().expandToLevel(3);
				MessageDialog.openInformation(parent.getShell(), "提示", "新建节点成功!");
				UIUtils.closeEditor(CreateNodeEditor.this);
			    } catch (DocumentException e) {
				e.printStackTrace();
			    } catch (IOException e) {
				e.printStackTrace();
			    }
			}
		    });
		} else {
		    NodeEntity tfFolder = (NodeEntity) oldEntity.getParentModel();
		    tfFolder.removeChild(oldEntity);
		    tfFolder.addChild(entity);
		    ((DataBaseInput) getEditorInput()).getTreeView().refresh();
			DeployRoot rootFolder = (DeployRoot) oldEntity.getParentModel().getParentModel();
		    SWTUtil.asyncExecThread(new Runnable() {
				
				@Override
				public void run() {
					 updateNodeXml(rootFolder,entity);
					 updateNodeXml1(rootFolder,entity);					
				}
			});
		   
		    MessageDialog.openInformation(parent.getShell(), "提示", "修改节点成功!");
		    UIUtils.closeEditor(CreateNodeEditor.this);
		}
	    }

	    @Override
	    public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	    }
	});

	Button btnCancel = new Button(compOpera, SWT.PUSH);
	btnCancel.setText("取消");
	data = new GridData(GridData.HORIZONTAL_ALIGN_END);
	btnCancel.setLayoutData(data);
	((GridData) btnCancel.getLayoutData()).widthHint = 61;

	btnCancel.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent e) {
		UIUtils.closeEditor(CreateNodeEditor.this);
	    }
	});

    }

    protected boolean check(CNodeEntity entity) {
	if (entity.getName() == null || entity.getName().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "名称不可为空");
	    txtName.setFocus();
	    return false;
	}
	if (entity.getIp() == null || entity.getIp().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "IP地址不可为空");
	    txtIp.setFocus();
	    return false;
	}
	if (entity.getUser() == null || entity.getUser().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "常规用户不可为空");
	    txtUser.setFocus();
	    return false;
	}

	boolean verifyIP = UIUtils.verifyIP(txtIp);
	if (!verifyIP) {
	    return false;
	}

	if (entity.getRootPass() == null || entity.getRootPass().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "root密码不可为空");
	    txtRoot.setFocus();
	    return false;
	}
	if (entity.getPort() == null || entity.getPort().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "端口不可为空");
	    txtPort.setFocus();
	    return false;
	}
	if (Integer.parseInt(entity.getPort()) > 65535) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "端口号不可大于65535");
	    txtPort.setFocus();
	    return false;
	}
	if (entity.getdPath() == null || entity.getdPath().trim().equals("")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "默认路径不可为空");
	    txtIp.setFocus();
	    return false;
	}
	if (!entity.getdPath().endsWith("/")) {// 给路径后添加“/”
	    entity.setNodePath(entity.getdPath() + "/");
	    entity.setdPath(entity.getdPath() + "/");
	}
	String path = entity.getdPath();
	if (!path.startsWith("/home/" + txtUser.getText() + "/")) {
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "默认路径必须以/home/" + txtUser.getText() + "/ 为起始路径名");
	    txtPath.setFocus();
	    return false;
	}
	return true;
    }

	private boolean validExist(CNodeEntity entity, Session session, String command2) {
		ChannelExec openChannel = JschUtil.execCommand(session, command2);
		int exitCode = 0;
		exitCode = JschUtil.returnSuccess(openChannel, exitCode);
		openChannel.disconnect();
		return true;
	}
    public static boolean ping(String ip) {
		int timeOut = 3000; // 超时应该在3钞以上
		boolean status = true;
		try {
			status = InetAddress.getByName(ip).isReachable(timeOut);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return status;
	}

    private Boolean updatePathOwner(Session session, String user, String path) throws JSchException {

	String[] split = path.split("/");
	StringBuffer buffer = new StringBuffer();
	if (split.length > 3) {
	    String str = "";
	    for (int i = 3; i < split.length; i++) {
		str += split[i] + "/";
		buffer.append("chown -R " + user + "." + user + " /home/" + user + "/" + str + ";");// 循环给文件夹更新属主
	    }
	}
	JschUtil.execCommand1(session, "mkdir -p " + path);
	JschUtil.execCommand1(session, buffer.toString());

	return true;
    }

    private void updateNodeXml(DeployRoot rootFolder,CNodeEntity newSource) {

	IFolder folder = ((NodeEntity) (rootFolder.getChildren())[0]).getFolder();
	IFile file = (IFile) folder.findMember("cnode.xml");
	File fileLocal = file.getLocation().toFile();
	SAXReader reader = new SAXReader();
	List<Element> listEle = null;
	Document document;
	try {
	    document = reader.read(file.getLocation().toFile());
	    Element root = document.getRootElement();
	    listEle = root.elements();
	    for (int i = 0, n = listEle.size(); i < n; i++) {
		Element element = listEle.get(i);
		if (element.element("name").getStringValue().equals(oldEntity.getName())) {
		    element.element("name").setText(newSource.getName());// 只能修改名字
		}
	    }
	    OutputFormat xmlFormat = UIUtils.xmlFormat();
	    XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
	    output.write(document);
	    output.close();

	} catch (DocumentException e) {
	    e.printStackTrace();
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", e.getMessage());
	} catch (IOException e) {
	    e.printStackTrace();
	    MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", e.getMessage());
	}
    }

	/**
	 * 修改主备同步的xml
	 * */
	private void updateNodeXml1(DeployRoot rootFolder,CNodeEntity newSource) {

		IFolder folder = ((MasterStandbyEntity) (rootFolder.getChildren())[1]).getFolder();
		IFile file = (IFile) folder.findMember("master.xml");
		File fileLocal = file.getLocation().toFile();
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document;
		try {
			document = reader.read(file.getLocation().toFile());
			Element root = document.getRootElement();
			listEle = root.elements();
			for (int i = 0, n = listEle.size(); i < n; i++) {
				Element element = listEle.get(i);
				if (element.element("master") != null) {
					continue;
				}
				Element ele = element.element("dbNodes");
				if(ele.getStringValue()==null){
					return ;
				}
				List<Element> listEleNode = ele.elements("dbNode");
				for (int j = 0; j < listEleNode.size(); j++) {
					if (listEleNode.get(j).attributeValue("physicalMachine").equals(oldEntity.getName())) {
						listEleNode.get(j).addAttribute("physicalMachine", newSource.getName());
					}

					List<Element> cnodeList = listEleNode.get(j).elements("cnode");
					for (Element element2 : cnodeList) {
						if (element2.element("name").getStringValue().equals(oldEntity.getName())) {
							element2.element("name").setText(newSource.getName());// 只能修改名字
						}
					}
				}
				Element ele1 = element.element("poolNodes");
				List<Element> listEleNode1 = ele1.elements("poolNode");
				for (int j = 0; j < listEleNode1.size(); j++) {
					if (listEleNode1.get(j).attributeValue("physicalMachine").equals(oldEntity.getName())) {
						listEleNode1.get(j).addAttribute("physicalMachine", newSource.getName());// 只能修改名字
					}

					List<Element> cnodeList = listEleNode1.get(j).elements("cnode");
					for (Element element2 : cnodeList) {
						if (element2.element("name").getStringValue().equals(oldEntity.getName())) {
							element2.element("name").setText(newSource.getName());// 只能修改名字
						}
					}
				}
			}
			OutputFormat xmlFormat = UIUtils.xmlFormat();
			XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
			output.write(document);
			output.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static String ReturnValue(ChannelExec channel) {
    	InputStream in;
    	String line = null;
    	try {
    	    in = channel.getInputStream();
    	    InputStreamReader isr = new InputStreamReader(in);
    	    BufferedReader br = new BufferedReader(isr);
    	    line = br.readLine();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
		return line;
    }
    private boolean checkName(CNodeEntity entity) {
	if (type.equals("new")) {
	    CTableTreeNode node = ((DataBaseInput) getEditorInput()).getNode();
	    Collection childrenList = node.getChildrenList();
	    if (childrenList != null && childrenList.size() > 0) {
		for (Object object : childrenList) {
		    CNodeEntity obj = (CNodeEntity) object;
		    if (obj.getName().equals(txtName.getText())) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "已存在此名称节点，请重命名");
			txtName.setFocus();
			return false;
		    }
		    if (obj.getIp().equals(entity.getIp()) && obj.getPort().equals(entity.getPort()) && obj.getRootPass().equals(entity.getRootPass()) && obj.getUser().equals(entity.getUser())
			    && entity.getdPath().startsWith(obj.getNodePath())) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "相同ip、端口、用户、默认路径的节点，只能存在一次");
			txtName.setFocus();
			return false;
		    }
		}
	    }
	} else {
	    CTableTreeNode node = ((DataBaseInput) getEditorInput()).getNode();
	    Collection childrenList = ((CTableTreeNode) (node.getParentModel())).getChildrenList();
	    if (childrenList != null && childrenList.size() > 0) {
		for (Object object : childrenList) {
		    if (node == object) {
			continue;
		    }
		    CNodeEntity obj = (CNodeEntity) object;
		    if (obj.getName().equals(txtName.getText())) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "已存在此名称节点，请重命名");
			txtName.setFocus();
			return false;
		    }
		}
	    }
	}
	return true;
    }

    @Override
    public void setFocus() {
	// TODO Auto-generated method stub

    }

}
