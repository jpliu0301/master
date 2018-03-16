package com.kingbase.db.replication.bundle.model.tree;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;
import com.kingbase.db.replication.bundle.views.ReplicationView;

public class SubscribeDataInfo extends CTableTreeNode implements ITreeProvider {

	private boolean hasInit = false;
	private String dbName;// 服务器名称
	private String dbServer;
	private String dbPort;
	private String dbUser;
	private String dbPasswrod;
	private String driverPath;
	private String driverName;

	private String subscribeName;// 订阅名称
	private String subscribeEnable;// 订阅是否可用
	
	private String databaseName;// 数据库名称
	private String databaseOid;// 数据库oid
	private Point location;
	private List<ReleaseDataInfo> releaseList;
	public static final Image disable_image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_subscriber_disable);
	public static final Image enable_image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_subscriber_enable);
	@Override
	public Image getImage(Object arg0) {
		if (subscribeEnable.equals("t")) { //$NON-NLS-1$
			return enable_image;
		} else {
			return disable_image;
		}
	}

	@Override
	public String getText(Object arg0) {
		return subscribeName;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public void refresh() {
		setHasInit(false);
		removeAll();
		treeExpanded();
	}
	@Override
	public void treeExpanded() {

		if (isOpen()) {
			return;
		}
		getSubscribeTables();
		setHasInit(true);
	}

	private void getSubscribeTables() {
		SubscribeDataBase datebase = (SubscribeDataBase) this.getParentModel();
		SubscribeDataSource parent = (SubscribeDataSource) datebase.getParentModel();
		ReplicationRoot rootFolder = (ReplicationRoot) parent.getParentModel().getParentModel();

		ReplicationFile ifile = ((ReplicationFile) (rootFolder.getChildren())[0]);
		List<Element> listEle = ReplicationView.getlistEle(ifile);
		 
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

		Connection sourceCon = DatabaseUtil.getConnection(parent, datebase.getDatabaseName());
		if (sourceCon == null){
			return  ;
		}
		try {
			ResultSet subscribeSet = sourceCon.createStatement().executeQuery(
					"SELECT SUB_ORIGIN, ARRAY_TO_STRING(SUB_REPLICATION_SETS, ''',''') AS SETS FROM SYSLOGICAL.SUBSCRIPTION WHERE SUB_NAME = '" //$NON-NLS-1$
							+ this.getSubscribeName() + "'"); //$NON-NLS-1$
			while (subscribeSet.next()) {
				String id = subscribeSet.getString("SUB_ORIGIN"); //$NON-NLS-1$
				String sets = subscribeSet.getString("SETS"); //$NON-NLS-1$

				Connection sourceConTables = null;
				for (ReleaseDataBase releaseDataBase : list) {
					if (releaseDataBase.getNodeId().equals(id)) {
						ReleaseDataSource sourceMeta = new ReleaseDataSource();
						sourceMeta.setDbName(releaseDataBase.getDbName());
						sourceMeta.setDbServer(releaseDataBase.getDbServer());
						sourceMeta.setDbPort(releaseDataBase.getDbPort());
						sourceMeta.setDbUser(releaseDataBase.getDbUser());
						sourceMeta.setDbPasswrod(releaseDataBase.getDbPasswrod());
						sourceMeta.setDriverName(releaseDataBase.getDriverName());
						sourceMeta.setDriverPath(releaseDataBase.getDriverPath());

						sourceConTables = DatabaseUtil.getConnection(sourceMeta, releaseDataBase.getDatabaseName());
						break;
					}
				}
				if (sourceConTables != null) {
					ResultSet tableSet = sourceConTables.createStatement().executeQuery(
							"SELECT (NSPNAME || '.' || RELNAME) AS TALENAME,RELID FROM SYSLOGICAL.TABLES WHERE SET_NAME IN ('" //$NON-NLS-1$
									+ sets + "') ORDER BY TALENAME"); //$NON-NLS-1$
					String oidNext = ""; //$NON-NLS-1$
					while (tableSet.next()) {

						SubscribeTable metaChild = new SubscribeTable();// 具体的某个发布
						String oid = tableSet.getString("RELID"); //$NON-NLS-1$
						String talename = tableSet.getString("TALENAME"); //$NON-NLS-1$
						metaChild.setTableName(talename);
						metaChild.setTableOid(oid);
						if(!oidNext.equals(oid)){
							oidNext = oid;
							this.addChild(metaChild);
						}
					}
					sourceConTables.createStatement().close();
					tableSet.close();
//					if (sourceConTables != null) {
//						sourceConTables.close();
//					}
				}else{
					MessageDialog.openError(UIUtils.getActiveShell(), "错误", Messages.SubscribeDataInfo_could_not_find_server); //$NON-NLS-1$
				}

			}
			sourceCon.createStatement().close();
			subscribeSet.close();
		} catch (Exception e) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", e.getMessage()); //$NON-NLS-1$
		}
	}
	public boolean isOpen() {
		return hasInit;
	}

	public void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public boolean hasChildren() {
		if (!hasInit)
			return true;
		return super.hasChildren();
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbServer() {
		return dbServer;
	}

	public void setDbServer(String dbServer) {
		this.dbServer = dbServer;
	}

	public String getDbPort() {
		return dbPort;
	}

	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPasswrod() {
		return dbPasswrod;
	}

	public void setDbPasswrod(String dbPasswrod) {
		this.dbPasswrod = dbPasswrod;
	}

	public String getDriverPath() {
		return driverPath;
	}

	public void setDriverPath(String driverPath) {
		this.driverPath = driverPath;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getSubscribeName() {
		return subscribeName;
	}

	public void setSubscribeName(String subscribeName) {
		this.subscribeName = subscribeName;
	}

	public String getSubscribeEnable() {
		return subscribeEnable;
	}

	public void setSubscribeEnable(String subscribeEnable) {
		this.subscribeEnable = subscribeEnable;
	}

	public List<ReleaseDataInfo> getReleaseList() {
		return releaseList;
	}

	public void setReleaseList(List<ReleaseDataInfo> releaseList) {
		this.releaseList = releaseList;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseOid() {
		return databaseOid;
	}

	public void setDatabaseOid(String databaseOid) {
		this.databaseOid = databaseOid;
	}

}
