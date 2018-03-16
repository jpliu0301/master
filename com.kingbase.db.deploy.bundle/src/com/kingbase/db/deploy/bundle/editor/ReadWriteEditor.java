package com.kingbase.db.deploy.bundle.editor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
import com.kingbase.db.core.util.FileUtil;
import com.kingbase.db.core.util.JschUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.deploy.bundle.editor.page.ReadWriteFirstPage;
import com.kingbase.db.deploy.bundle.editor.page.ReadWriteSecondPage;
import com.kingbase.db.deploy.bundle.editor.page.ReadWriteThreePage;
import com.kingbase.db.deploy.bundle.model.tree.CNodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.CReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.DeployRoot;
import com.kingbase.db.deploy.bundle.model.tree.KeyValueEntity;
import com.kingbase.db.deploy.bundle.model.tree.NodeEntity;
import com.kingbase.db.deploy.bundle.model.tree.PoolEntity;
import com.kingbase.db.deploy.bundle.model.tree.PosEntity;
import com.kingbase.db.deploy.bundle.model.tree.ReadWriteEntity;
import com.kingbase.db.deploy.bundle.model.tree.TableNodeEntity;

public class ReadWriteEditor extends EditorPart {

	private CReadWriteEntity oldReadEntity;

	private CNodeEntity[] models;
	private String[] nodeName = new String[] {};
	private ReadWriteEntity masterEntity;
	private String type;

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
		if (input.getName().equals("新建读写分离")) {
			this.type = "new";
			this.masterEntity = (ReadWriteEntity) ((DataBaseInput) input).getNode();
			models = getNodes();
		} else {
			type = "update";
			this.oldReadEntity = (CReadWriteEntity) ((DataBaseInput) getEditorInput()).getNode();
			this.masterEntity = (ReadWriteEntity) oldReadEntity.getParentModel();
			models = getNodes();
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
	public void createPartControl(Composite parent) {
		parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		parent.setLayout(new FillLayout());


		GridLayout layout1 = new GridLayout(1, false);
		layout1.marginTop = 1;
		layout1.marginLeft = 14;
		layout1.marginRight = 7;
		layout1.marginBottom = 11;
		layout1.verticalSpacing = 15;

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent,  SWT.H_SCROLL|SWT.V_SCROLL);
	    
	    Composite composite = new Composite(scrolledComposite,SWT.NONE);
	    scrolledComposite.setContent(composite);
	    composite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));// White color
	    composite.setLayout(layout1);
	    GridData data = new GridData(GridData.FILL_BOTH);
	    composite.setLayoutData(data);
		
		Composite composite1 = new Composite(composite, SWT.BORDER);
		composite1.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		composite1.setLayout(layout1);
		composite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ReadWriteFirstPage dbConfigurePage = new ReadWriteFirstPage();
		ReadWriteSecondPage poolConfigurePage = new ReadWriteSecondPage();
		ReadWriteThreePage showConfigurePage = new ReadWriteThreePage();

		// 第一步
		Composite dbConfigureCom = dbConfigurePage.getCom(composite1, nodeName, models, oldReadEntity,type);
		// 第二步
		Composite poolConfigureCom = poolConfigurePage.getCom1(composite1, nodeName, models, oldReadEntity,type);
		// 第三步
		Composite showConfigureCom = showConfigurePage.getCom2(composite1,type);

		Composite compOpera = new Composite(composite, SWT.None);
		compOpera.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		compOpera.setLayout(new GridLayout(8, false));
		GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
		compOpera.setLayoutData(data1);
		Label label = new Label(compOpera, SWT.None);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		data1 = new GridData(GridData.FILL_HORIZONTAL);
		data1.horizontalSpan = 2;
		label.setLayoutData(data1);

		Button btnAb = new Button(compOpera, SWT.PUSH);
		btnAb.setText("上一步");
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnAb.setLayoutData(data1);
		((GridData) btnAb.getLayoutData()).widthHint = 61;
		btnAb.setEnabled(false);
		
		Button btnNext = new Button(compOpera, SWT.PUSH);
		btnNext.setText("下一步");
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnNext.setLayoutData(data1);
		((GridData) btnNext.getLayoutData()).widthHint = 61;
		btnNext.setEnabled(true);
		
		Button btnApply = new Button(compOpera, SWT.PUSH);
		btnApply.setText("确定");
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnApply.setLayoutData(data1);
		((GridData) btnApply.getLayoutData()).widthHint = 61;
		btnApply.setEnabled(false);

		btnAb.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (poolConfigureCom.getVisible()) {
//					if (!checkPool(poolConfigurePage.getEntity())) {//上一步时不验证
//						return;
//					}

					poolConfigureCom.setVisible(false);
					GridData gd3 = (GridData) poolConfigureCom.getLayoutData();
					gd3.exclude = true;
					poolConfigureCom.getParent().layout();

					showConfigureCom.setVisible(false);
					GridData gd2 = (GridData) showConfigureCom.getLayoutData();
					gd2.exclude = true;
					showConfigureCom.getParent().layout();
					
					dbConfigureCom.setVisible(true);
					GridData gd21 = (GridData) dbConfigureCom.getLayoutData();
					gd21.exclude = false;
					dbConfigureCom.getParent().layout();

					btnApply.setEnabled(false);
					btnAb.setEnabled(false);
					btnNext.setEnabled(true);
				} else if (showConfigureCom.getVisible()) {
					showConfigureCom.setVisible(false);
					GridData gd3 = (GridData) showConfigureCom.getLayoutData();
					gd3.exclude = true;
					showConfigureCom.getParent().layout();

					dbConfigureCom.setVisible(false);
					GridData gd2 = (GridData) dbConfigureCom.getLayoutData();
					gd2.exclude = true;
					dbConfigureCom.getParent().layout();

					poolConfigureCom.setVisible(true);
					GridData gd21 = (GridData) poolConfigureCom.getLayoutData();
					gd21.exclude = false;
					poolConfigureCom.getParent().layout();
					
					btnApply.setEnabled(false);
					btnAb.setEnabled(true);
					btnNext.setEnabled(true);
					if (type.equals("update")) {
						btnApply.setEnabled(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnNext.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dbConfigureCom.getVisible()) {
					if (!checkDB(dbConfigurePage.getEntity())) {
						return;
					}
					dbConfigureCom.setVisible(false);
					GridData gd3 = (GridData) dbConfigureCom.getLayoutData();
					gd3.exclude = true;
					dbConfigureCom.getParent().layout();
					
					poolConfigureCom.setVisible(true);
					GridData gd31 = (GridData) poolConfigureCom.getLayoutData();
					gd31.exclude = false;
					poolConfigureCom.getParent().layout();

					showConfigureCom.setVisible(false);
					GridData gd21 = (GridData) showConfigureCom.getLayoutData();
					gd21.exclude = true;
					showConfigureCom.getParent().layout();
					
					btnApply.setEnabled(false);
					btnAb.setEnabled(true);
					btnNext.setEnabled(true);
				} else if (poolConfigureCom.getVisible()) {
					if (!checkPool(poolConfigurePage.getEntity())) {
						return;
					}
					poolConfigureCom.setVisible(false);
					GridData gd3 = (GridData) poolConfigureCom.getLayoutData();
					gd3.exclude = true;
					poolConfigureCom.getParent().layout();
					

					dbConfigureCom.setVisible(false);
					GridData gd31 = (GridData) dbConfigureCom.getLayoutData();
					gd31.exclude = true;
					dbConfigureCom.getParent().layout();
					
					showConfigureCom.setVisible(true);
					GridData gd311 = (GridData) showConfigureCom.getLayoutData();
					gd311.exclude = false;
					showConfigureCom.getParent().layout();
					
					showConfigurePage.setText(dbConfigurePage.getEntity(), poolConfigurePage.getEntity(), models);
					btnApply.setEnabled(true);
					btnAb.setEnabled(true);
					btnNext.setEnabled(false);
					if (type.equals("update")) {
						btnApply.setEnabled(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnApply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				boolean checkDB = checkDB(dbConfigurePage.getEntity());
				boolean checkPool = checkPool(poolConfigurePage.getEntity());
				boolean checkPath = checkPath(dbConfigurePage.getEntity(), poolConfigurePage.getEntity());
				if (!checkDB || !checkPool || !checkPath) {
					return;
				}

				if (type.equals("new")) {
					CReadWriteEntity entity = new CReadWriteEntity();
					entity.setPosEntity(dbConfigurePage.getEntity());
					entity.setPoolEntity(poolConfigurePage.getEntity());
					CTableTreeNode node = ((DataBaseInput) getEditorInput()).getNode();
					IFolder folder = ((ReadWriteEntity) node).getFolder();
					IFile file = (IFile) folder.findMember("read.xml");
					SWTUtil.asyncExecThread(new Runnable() {
						public void run() {
							SAXReader reader = new SAXReader();
							ReadWriteDialog dialog = new ReadWriteDialog(compOpera.getShell(), entity);
							int open = dialog.open();
							if (open==2) {
								writeNodeXml(entity, file, reader);
								((ReadWriteEntity) node).addChild(entity);
								((ReadWriteEntity) node).refresh();
								((DataBaseInput) getEditorInput()).getTreeView().refresh();

								UIUtils.closeEditor(ReadWriteEditor.this);
							}
						}
					});
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		Button btnCancel = new Button(compOpera, SWT.PUSH);
		btnCancel.setText("取消");
		data1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		btnCancel.setLayoutData(data1);
		((GridData) btnCancel.getLayoutData()).widthHint = 61;
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				UIUtils.closeEditor(ReadWriteEditor.this);
			}
		});
		
		scrolledComposite.setExpandHorizontal(true);
	    scrolledComposite.setExpandVertical(true);
	    Point size = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    scrolledComposite.setMinSize(size.x,size.y-250);
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
	/**
	 * 
	 * 在集群下面的每个节点加上 /集群作为子目录 
	 * 
	 */
	protected boolean checkPath(PosEntity entityDB, PoolEntity entityPool) {
		List<TableNodeEntity> listDB = entityDB.getListDb();
		List<TableNodeEntity> listPool = entityPool.getListPool();

		for (int i = 0; i < listDB.size(); i++) {
			Session session = null;
			try {
				session = JschUtil.connect(listDB.get(i).getNodeEntity().getIp(),
						Integer.parseInt(listDB.get(i).getNodeEntity().getPort()), "root",
						listDB.get(i).getNodeEntity().getRootPass());

				TableNodeEntity entity = listDB.get(i);
			    entity.getNodeEntity().setdPath(entity.getNodeEntity().getNodePath() + entityDB.getName() + "/");
				ChannelExec openChannel = JschUtil.execCommand(session,
						"ls " + entity.getNodeEntity().getdPath() + " | wc -l");
				String value = ReturnValue(openChannel);
				if (value != null && Integer.valueOf(value) > 0) {
					MessageDialog.openInformation(UIUtils.getActiveShell(), "提示", entity.getNodeEntity().getName()
							+ "的部署路径" + entity.getNodeEntity().getdPath() + "不为空，建议重新换集群名称");
					entity.getNodeEntity().setdPath(entity.getNodeEntity().getdPath());
					return false;
				} else {
					boolean flag = updatePathOwner(session, entity.getNodeEntity().getUser(), entity.getNodeEntity().getdPath());
					if (!flag) {
						return false;
					}
				}
				openChannel.disconnect();
				session.disconnect();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}

		for (TableNodeEntity pool : listPool) {
			CNodeEntity poolEntity = pool.getNodeEntity();
			boolean flag = false;
			for (TableNodeEntity DB : listDB) {
				CNodeEntity DBEntity = DB.getNodeEntity();
				if (poolEntity.getName().equals(DBEntity.getName())) {
					flag = true;
				}
			}
			if (!flag) {
				poolEntity.setdPath(poolEntity.getNodePath() + entityDB.getName() + "/");
				Session session = null;
				try {
					session = JschUtil.connect(poolEntity.getIp(), Integer.parseInt(poolEntity.getPort()), "root",
							poolEntity.getRootPass());
					ChannelExec openChannel = JschUtil.execCommand(session, "ls " + poolEntity.getdPath() + " | wc -l");
					String value = ReturnValue(openChannel);
					if (value != null && Integer.valueOf(value) > 0) {
						MessageDialog.openInformation(UIUtils.getActiveShell(), "提示",
								poolEntity.getName() + "的部署路径" + poolEntity.getdPath() + "不为空，建议重新换集群名称");
						poolEntity.setdPath(poolEntity.getdPath());
						return false;
					} else {
						boolean flag1 = updatePathOwner(session, poolEntity.getUser(), poolEntity.getdPath());
						if (!flag1) {
							return false;
						}
					}
					openChannel.disconnect();
					session.disconnect();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSchException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	private Boolean updatePathOwner(Session session, String user, String path) throws JSchException {
		ChannelExec openChannel = JschUtil.execCommand(session, "mkdir -p " + path+";"+"chown -R " + user + "." + user + " " + path);
		int exitCode = 0;
		exitCode = JschUtil.returnSuccess(openChannel, exitCode);
		if (exitCode != 0) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "错误", "更改文件 " + path + " 属性失败");
		}
		openChannel.disconnect();
		return true;
	}

	/**
	 * 
	 * 检查前两个页面数据的准确性
	 * 
	 */
	protected boolean checkDB(PosEntity entityDB) {
		List<TableNodeEntity> listDB = entityDB.getListDb();
		int k = 0;
		String cluster = entityDB.getName();
		if (cluster == null || cluster.trim().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "集群名称必须填写！！！");
			return false;
		}
		for (int i = 0; i < listDB.size(); i++) {
			TableNodeEntity entity = listDB.get(i);
			if (entity.getLibrary().equals("主库")) {
				k++;
			}
			if (entity.getListenerPost() == null || entity.getListenerPost().equals("")) {
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "数据库配置中" + entity.getLibrary() + "的监听端口号不能为空");
				return false;
			}
			for (int j = 0; j < listDB.size(); j++) {
				TableNodeEntity entity1 = listDB.get(j);
				if (!entity.equals(entity1)
						&& entity.getNodeEntity().getName().equals(entity1.getNodeEntity().getName())) {
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示",
							"数据库配置中,节点" + entity1.getNodeEntity().getName() + "不能重复配置");
					return false;
				}
			}
		}
		if (k == 0 || k > 1) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "主库节点有且只能有一个，请检查");
			return false;
		}
		if (entityDB.getDbzipPath().trim().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请选择数据库zip包");
			return false;
		}else{
			boolean exist = FileUtil.exist(entityDB.getDbzipPath());
			if(!exist){
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "zip包所在路径不存在，请检查");
				return false;
			}
			boolean checkDBZip = checkDBZip(entityDB.getDbzipPath());
			if(!checkDBZip){
				return false;
			}
		}
		String ip = entityDB.getDelegate_IP();
		String rex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		if (!ip.matches(rex)) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请输入正确的IP地址");
			return false;
		}
		if (entityDB.getDbUser().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请输入数据库用户");
			return false;
		}
		if (entityDB.getDbPassword().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请输入数据库用户密码");
			return false;
		}
		return true;
	}

	protected boolean checkPool(PoolEntity entityPool) {
		List<TableNodeEntity> listPool = entityPool.getListPool();

		int n = 0;
		int m = 0;
		for (int i = 0; i < listPool.size(); i++) {
			TableNodeEntity entity = listPool.get(i);
			if (entity.getLibrary().equals("主分发")) {
				m++;
			}
			if (entity.getLibrary().equals("备分发")) {
				n++;
			}
			for (int j = 0; j < listPool.size(); j++) {
				TableNodeEntity entity1 = listPool.get(j);
				if (!entity.equals(entity1)
						&& entity.getNodeEntity().getName().equals(entity1.getNodeEntity().getName())) {
					MessageDialog.openWarning(UIUtils.getActiveShell(), "提示",
							"pool配置中,节点" + entity1.getNodeEntity().getName() + "不能重复配置");
					return false;
				}
			}
		}
		if (m == 0 || m > 1) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "主pool节点有且只能有一个，请检查");
			return false;
		}
		if (n > 1) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "备pool节点只能有一个，请检查");
			return false;
		}
		if (entityPool.getPort().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "pool配置中port端口不能为空");
			return false;
		}
		if (entityPool.getDelegate_IP().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "pool配置中delegate_ip不能为空");
			return false;
		}
		String ip = entityPool.getDelegate_IP();
		String rex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		if (!ip.matches(rex)) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请输入正确的IP地址");
			return false;
		}
		if (entityPool.getPoolzipPath().trim().equals("")) {
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "请选择kingbaseCluster zip包");
			return false;
		}else{
			boolean exist = FileUtil.exist(entityPool.getPoolzipPath());
			if(!exist){
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "zip包所在路径不存在，请检查");
				return false;
			}
			boolean checkClusterZip = checkClusterZip(entityPool.getPoolzipPath());
			if(!checkClusterZip){
				return false;
			}
		}
		return true;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * 获取节点信息
	 */
	public CNodeEntity[] getNodes() {
		DeployRoot rootFolder = (DeployRoot) masterEntity.getParentModel();
		IFolder folder = ((NodeEntity) (rootFolder.getChildren())[0]).getFolder();
		IFile file = (IFile) folder.findMember("cnode.xml");
		File fileLocal = file.getLocation().toFile();
		SAXReader reader = new SAXReader();
		List<Element> listEle = null;
		Document document;
		try {
			document = reader.read(new BufferedReader(
					new InputStreamReader(new FileInputStream(file.getLocation().toFile()), "utf-8")));
			Element root = document.getRootElement();
			listEle = root.elements();
			models = new CNodeEntity[listEle.size()];
			for (int i = 0, n = listEle.size(); i < n; i++) {
				Element element = listEle.get(i);
				CNodeEntity model = new CNodeEntity();
				model.setName(element.element("name").getStringValue());
				model.setIp(element.element("IP").getStringValue());
				model.setPort(element.element("port").getStringValue());
				model.setRootPass(element.element("rootPassword").getStringValue());
				model.setUser(element.element("user").getStringValue());
				model.setdPath(element.element("defaultPath").getStringValue());
				model.setNodePath(element.element("nodePath").getStringValue());
				model.setNetcard(element.element("netcard").getStringValue());
				model.setGateway(element.element("gateway").getStringValue());
				models[i] = model;
			}
			if (models.length > 0) {
				nodeName = new String[models.length];
				for (int i = 0; i < models.length; i++) {
					nodeName[i] = ((CNodeEntity) models[i]).getName();
				}
			}

			OutputFormat xmlFormat = UIUtils.xmlFormat();
			XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
			output.write(document);
			output.close();

		} catch (DocumentException e) {
			e.printStackTrace();
			MessageDialog.openWarning(UIUtils.getActiveShell(), "Error", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openWarning(UIUtils.getActiveShell(), "Error", e.getMessage());
		}
		if(models.length==0){
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "节点不存在，请先创建节点！！！");
			UIUtils.closeEditor(this);
		}
		return models;
	}

	public void writeNodeXml(CReadWriteEntity entity, IFile file, SAXReader reader) {

		File fileLocal = file.getLocation().toFile();
		Document document;
		try {
			document = reader.read(new BufferedReader(new InputStreamReader(new FileInputStream(fileLocal), "utf-8")));

			Element root = document.getRootElement();
			Element eleConnection = root.addElement("read");

			Element element = eleConnection.addElement("name");
			element.addText(entity.getPosEntity().getName());
			List<TableNodeEntity> listNode = entity.getPosEntity().getListDb();
			if (listNode.size() > 0) {
				element = eleConnection.addElement("dbNodes");
				for (int i = 0; i < listNode.size(); i++) {
					Element element1 = element.addElement("dbNode");
					element1.addAttribute("library", listNode.get(i).getLibrary());
					element1.addAttribute("physicalMachine", listNode.get(i).getPhysicalMachine());
					element1.addAttribute("nodeType", listNode.get(i).getNodeType());
					element1.addAttribute("listenerAddress", listNode.get(i).getListenerAddress());
					element1.addAttribute("listenerPost", listNode.get(i).getListenerPost());

					Element cnodeElm = element1.addElement("cnode");

					Element element11 = cnodeElm.addElement("name");
					element11.addText(listNode.get(i).getNodeEntity().getName());

					element11 = cnodeElm.addElement("IP");
					element11.addText(listNode.get(i).getNodeEntity().getIp());

					element11 = cnodeElm.addElement("port");
					element11.addText(listNode.get(i).getNodeEntity().getPort());

					element11 = cnodeElm.addElement("rootPassword");
					element11.addText(listNode.get(i).getNodeEntity().getRootPass());

					element11 = cnodeElm.addElement("user");
					element11.addText(listNode.get(i).getNodeEntity().getUser());

					element11 = cnodeElm.addElement("defaultPath");
					element11.addText(listNode.get(i).getNodeEntity().getdPath());
				}
			}

			element = eleConnection.addElement("dbzipPath");
			element.addText(entity.getPosEntity().getDbzipPath());

			if (entity.getPosEntity().getIsDbBase()) {
				element = eleConnection.addElement("isDbBase");
				element.addText(entity.getPosEntity().getIsDbBase() + "");

				element = eleConnection.addElement("max_wal_senders");
				element.addText(entity.getPosEntity().getMax_wal());

				element = eleConnection.addElement("max_standby_archive_delay");
				element.addText(entity.getPosEntity().getMax_standby_arc());

				element = eleConnection.addElement("wal_keep_segments");
				element.addText(entity.getPosEntity().getWal_keep());

				element = eleConnection.addElement("max_standby_streaming_delay");
				element.addText(entity.getPosEntity().getMax_standby_str());
//
//				element = eleConnection.addElement("replication_timeout");
//				element.addText(entity.getPosEntity().getReplication());

				element = eleConnection.addElement("wal_receiver_status_interval");
				element.addText(entity.getPosEntity().getWal_receiver());

				element = eleConnection.addElement("hot_standby_feedback");
				element.addText(entity.getPosEntity().getHot_standby());

				element = eleConnection.addElement("dbUser");
				element.addText(entity.getPosEntity().getDbUser());
				
				element = eleConnection.addElement("dbPassword");
				element.addText(entity.getPosEntity().getDbPassword());
				
				element = eleConnection.addElement("caseInsensitive");
				element.addText(entity.getPosEntity().isInsensitive()+"");

				element = eleConnection.addElement("delegate_IP");
				element.addText(entity.getPosEntity().getDelegate_IP()+"");
			} else {
				element = eleConnection.addElement("isDbBase");
				element.addText(entity.getPosEntity().getIsDbBase() + "");

				element = eleConnection.addElement("posPath");
				element.addText(entity.getPosEntity().getPosPath());
				List<KeyValueEntity> listKey = entity.getPosEntity().getListKey1();
				if (listKey.size() > 0) {
					element = eleConnection.addElement("dbKeyValueNodes");
					for (int i = 0; i < listKey.size(); i++) {
						Element element1 = element.addElement("dbKeyValueNode");
						element1.addAttribute(listKey.get(i).getKey(), listKey.get(i).getValue());
					}
				}
			}

			List<TableNodeEntity> listPoolNode = entity.getPoolEntity().getListPool();
			if (listPoolNode.size() > 0) {
				element = eleConnection.addElement("poolNodes");
				for (int i = 0; i < listPoolNode.size(); i++) {
					Element element1 = element.addElement("poolNode");
					element1.addAttribute("library", listPoolNode.get(i).getLibrary());
					element1.addAttribute("physicalMachine", listPoolNode.get(i).getPhysicalMachine());
					element1.addAttribute("nodeType", listPoolNode.get(i).getNodeType());
					element1.addAttribute("listenerAddress", listPoolNode.get(i).getListenerAddress());
					element1.addAttribute("listenerPost", listPoolNode.get(i).getListenerPost());
					element1.addAttribute("netcard", listPoolNode.get(i).getNodeEntity().getNetcard());

					Element cnodeElm = element1.addElement("cnode");
					Element element11 = cnodeElm.addElement("name");
					element11.addText(listPoolNode.get(i).getNodeEntity().getName());

					element11 = cnodeElm.addElement("IP");
					element11.addText(listPoolNode.get(i).getNodeEntity().getIp());

					element11 = cnodeElm.addElement("port");
					element11.addText(listPoolNode.get(i).getNodeEntity().getPort());

					element11 = cnodeElm.addElement("rootPassword");
					element11.addText(listPoolNode.get(i).getNodeEntity().getRootPass());

					element11 = cnodeElm.addElement("user");
					element11.addText(listPoolNode.get(i).getNodeEntity().getUser());

					element11 = cnodeElm.addElement("defaultPath");
					element11.addText(listPoolNode.get(i).getNodeEntity().getdPath());
				}
			}

			element = eleConnection.addElement("poolzipPath");
			element.addText(entity.getPoolEntity().getPoolzipPath());

			if (entity.getPoolEntity().getIsPoolBase()) {
				element = eleConnection.addElement("isPoolBase");
				element.addText(entity.getPoolEntity().getIsPoolBase() + "");

				element = eleConnection.addElement("delegate_IP");
				element.addText(entity.getPoolEntity().getDelegate_IP());

				element = eleConnection.addElement("port");
				element.addText(entity.getPoolEntity().getPort());

				element = eleConnection.addElement("pcp_port");
				element.addText(entity.getPoolEntity().getPcp_port());


				element = eleConnection.addElement("wd_port");
				element.addText(entity.getPoolEntity().getWd_port());

//				element = eleConnection.addElement("netcard");
//				element.addText(entity.getPoolEntity().getNetcard());

				element = eleConnection.addElement("pcpUser");
				element.addText(entity.getPoolEntity().getPcpUser());

				element = eleConnection.addElement("pcpPass");
				element.addText(entity.getPoolEntity().getPcpPass());
			} else {
				element = eleConnection.addElement("isPoolBase");
				element.addText(entity.getPoolEntity().getIsPoolBase() + "");

				element = eleConnection.addElement("poolPath");
				element.addText(entity.getPoolEntity().getPoolPath());
				List<KeyValueEntity> listKey = entity.getPoolEntity().getListKey2();
				if (listKey.size() > 0) {
					element = eleConnection.addElement("poolKeyValueNodes");
					for (int i = 0; i < listKey.size(); i++) {
						Element element1 = element.addElement("poolKeyValueNode");
						element1.addAttribute(listKey.get(i).getKey(), listKey.get(i).getValue());
					}
				}
			}

			OutputFormat xmlFormat = UIUtils.xmlFormat();
			XMLWriter output = null;
			output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
			output.write(document);
			output.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean checkDBZip(String str){
		List<String> list = new ArrayList<String>();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(str));
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					String name = ze.getName();
					System.out.println(name);
					list.add(name);
				}
			}
			zin.closeEntry();
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e1.getMessage());
			return false;
		} 
		if(list.size()==0){
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "此压缩文件有误，请检查");
			return false;
		}else{
			if (!(list.contains("db/") 
					&& list.contains("db/lib/")
//					&& list.contains("db/etc/")
					&& list.contains("db/bin/")
					&& list.contains("db/share/"))){
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "压缩文件内应首先包含db文件夹，请检查");
				return false;
			}
		}
		return true;
	}
	private boolean checkClusterZip(String str){
		List<String> list = new ArrayList<String>();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(str));
			ZipInputStream zin = new ZipInputStream(in);
			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					String name = ze.getName();
					System.out.println(name);
					list.add(name);
				}
			}
			zin.closeEntry();
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e1.getMessage());
			return false;
		} 
		if(list.size()==0){
			MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "此压缩文件有误，请检查");
			return false;
		}else{
			if (!(list.contains("kingbasecluster/") 
					&& list.contains("kingbasecluster/lib/")
					&& list.contains("kingbasecluster/etc/")
					&& list.contains("kingbasecluster/bin/")
					&& list.contains("kingbasecluster/share/"))){
				MessageDialog.openWarning(UIUtils.getActiveShell(), "提示", "压缩文件内应首先包含kingbasecluster文件夹，请检查");
				return false;
			}
		}
		return true;
	}
}
