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

public class ReleaseDataInfo  extends CTableTreeNode implements ITreeProvider{


	private boolean hasInit = false;
	private String dbName;// 服务器名称
	private String dbServer;
	private String dbPort;
	private String dbUser;
	private String dbPasswrod;
	private String driverPath;
	private String driverName;
	
	private String databaseName;// 数据库名称
	private String databaseOid;// 数据库oid
	private String releaseName;// 发布名称
	
	private List<String> tableNameList = new ArrayList<String>(); 
	public static final Image image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_publish);
	private Point location;
	@Override
	public Image getImage(Object arg0) {
		return image;
	}

	@Override
	public String getText(Object arg0) {
		return releaseName;
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
		getReleaseTables();
		setHasInit(true);
	
	}

	private void getReleaseTables() {
		ReleaseDataBase datebase = (ReleaseDataBase) this.getParentModel();
		ReleaseDataSource parent = (ReleaseDataSource) datebase.getParentModel();

		Connection sourceCon = DatabaseUtil.getConnection(parent, datebase.getDatabaseName());
		if (sourceCon == null) {
			return;
		}
		try {
			ResultSet tableSet = sourceCon.createStatement().executeQuery(
					"SELECT (NSPNAME || '.' || RELNAME) AS TALENAME,RELID FROM SYSLOGICAL.TABLES WHERE SET_NAME IN ('" //$NON-NLS-1$
							+ this.getReleaseName() + "') ORDER BY TALENAME"); //$NON-NLS-1$
			String oidNext = ""; //$NON-NLS-1$
			while (tableSet.next()) {

				ReleaseTable metaChild = new ReleaseTable();// 具体的某个发布
				String oid = tableSet.getString("RELID"); //$NON-NLS-1$
				String talename = tableSet.getString("TALENAME"); //$NON-NLS-1$
				metaChild.setTableName(talename);
				metaChild.setTableOid(oid);
				if (!oidNext.equals(oid)) {
					oidNext = oid;
					this.addChild(metaChild);
				}
			}
			tableSet.close();

			sourceCon.createStatement().close();
//			if (sourceCon != null) {
//				sourceCon.close();
//			}
		} catch (Exception e) {
			MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage()); //$NON-NLS-1$
		}
	}
	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
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

	public String getReleaseName() {
		return releaseName;
	}

	public void setReleaseName(String releaseName) {
		this.releaseName = releaseName;
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

	public List<String> getTableNameList() {
		return tableNameList;
	}

	public void setTableNameList(List<String> tableNameList) {
		this.tableNameList = tableNameList;
	}
	

}
