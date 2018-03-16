package com.kingbase.db.replication.bundle.model.tree;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.core.util.DateUtil;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

public class SubscribeDataBase extends CTableTreeNode implements ITreeProvider {

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
    private String nodeId;// 数据库节点nodeId
    private boolean isExistTable = true;
    public static final Image enable_image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_database_enable);
    public static final Image disable_image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_database_disable);

    @Override
    public Image getImage(Object arg0) {
	if (isExistTable) {

	    return enable_image;
	} else {

	    return disable_image;
	}
    }

    public boolean isExistTable() {
	return isExistTable;
    }

    public void setExistTable(boolean isExistTable) {
	this.isExistTable = isExistTable;
    }

    @Override
    public String getText(Object arg0) {
	return databaseName;
    }

    public void refresh() {
	setHasInit(false);
	removeAll();
	ReplicationFile ifile = (ReplicationFile) this.getParentModel().getParentModel();
	File file = ifile.getFile();
	SAXReader reader = new SAXReader();
	Document document = null;
	try {
	    document = reader.read(file);
	} catch (DocumentException e) {
	    e.printStackTrace();
	}
	createNode(document, file, this);
	treeExpanded();
    }

    @Override
    public void treeExpanded() {

	if (isOpen()) {
	    return;
	}
	if (!isExistTable) {
	    return;
	}
	getSubscribeInfo();// 得到数据库下的所有订阅
	setHasInit(true);
    }

    /**
     * 创建xml节点:每个数据库都重写一遍服务器的连接数据;
     */
    private void createNode(Document document, File file, SubscribeDataBase database) {

	Element root1 = document.getRootElement();
	List<Element> listEle = root1.elements();

	Connection sourceCon = DatabaseUtil.getConnection((SubscribeDataSource) (this.getParentModel()), this.getDatabaseName());
	if (sourceCon != null) {
	    try {
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) {
		    String nodeId = null;
		    Statement stm = sourceCon.createStatement();
		    ResultSet resultSet = stm.executeQuery("SELECT NODE_ID FROM SYSLOGICAL.LOCAL_NODE");
		    while (resultSet.next()) {
			nodeId = resultSet.getString("NODE_ID");
		    }
		    if (nodeId == null) {

			resultSet = stm.executeQuery("SELECT syslogical.create_node( node_name := '" + DateUtil.getDateTime1()+ "_" + (new Random()).nextInt(100) + "_" + database.getDatabaseName() + "', dsn := 'host="
				+ this.getDbServer() + " port=" + this.getDbPort() + " user=" + database.getDbUser() + " password=" + database.getDbPasswrod() + " dbname="
				+ database.getDatabaseName() + "' )");
			while (resultSet.next()) {
			    nodeId = resultSet.getString("create_node");
			}
		    }
		    resultSet.close();
		    stm.close();
		    for (int i = 0, n = listEle.size(); i < n; i++) {
			Element element = listEle.get(i);
			Element nodesElm = element.element("nodes");
			String Ip = element.element("server").getStringValue();
			String Port = element.element("port").getStringValue();
			String User = element.element("username").getStringValue();
			String Pwd = element.element("password").getStringValue();
			List<Element> nodeElm = nodesElm.elements("node");
			if ((database.getDbServer().equals(Ip) && database.getDbPort().equals(Port) 
				&& database.getDbUser().equals(User) && database.getDbPasswrod().equals(Pwd))) {
			    boolean flag = false;
			    for (Element node : nodeElm) {
				Element ID = node.element("nodeId");
				Element dbname = node.element("dbname");
				if (ID != null && !ID.getStringValue().equals(nodeId) && dbname!=null && dbname.getStringValue().equals(database.getDatabaseName())) {// 若是已经创建了node节点，但是数据库里面与xml不一致
				    node.element("nodeId").setText(nodeId); //$NON-NLS-1$
				    XMLWriter output;
				    try {
					output = new XMLWriter(new FileWriter(file), UIUtils.xmlFormat());
					output.write(document);
					output.close();
					return;
				    } catch (IOException e) {
					e.printStackTrace();
				    }
				}
				if (dbname != null && dbname.getStringValue().equals(database.getDatabaseName())){
				    flag = true;
				}
			    }
			    if(!flag || nodeElm.isEmpty()){
				Element eleNodes = element.element("nodes");
				Element addElement = eleNodes.addElement("node");

				Element eleNodeId = addElement.addElement("nodeId");
				eleNodeId.addText(nodeId);

				eleNodeId = addElement.addElement("dbname");
				eleNodeId.addText(database.getDatabaseName());
				
				try {
				    XMLWriter output = new XMLWriter(new FileWriter(file), UIUtils.xmlFormat());
				    output.write(document);
				    output.close();
				} catch (IOException e) {
				    e.printStackTrace();
				}
			    }
			}
		    }
		}
	    } catch (SQLException e) {
		e.printStackTrace();
		try {
		    database.setExistTable(false);
		    sourceCon.rollback();
		} catch (SQLException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
	    }
	}
    }

    private void getSubscribeInfo() {

	SubscribeDataSource parent = (SubscribeDataSource) this.getParentModel();
	Connection sourceCon = DatabaseUtil.getConnection(parent, this.getDatabaseName());
	if (sourceCon == null) {
	    return;
	}
	try {
	    ResultSet subscribeSet = sourceCon.createStatement().executeQuery("SELECT SUB_NAME, SUB_ENABLED FROM SYSLOGICAL.SUBSCRIPTION ORDER BY SUB_NAME");
	    while (subscribeSet.next()) {
		String subscribeName = subscribeSet.getString("SUB_NAME"); //$NON-NLS-1$
		String subscribeEnable = subscribeSet.getString("SUB_ENABLED"); //$NON-NLS-1$
		SubscribeDataInfo metaChild = new SubscribeDataInfo();// 具体的某个发布
		metaChild.setSubscribeName(subscribeName);
		metaChild.setSubscribeEnable(subscribeEnable);
		metaChild.setDatabaseName(this.getDatabaseName());
		metaChild.setDatabaseOid(this.getDatabaseOid());
		metaChild = setDataSourceMetaInfo(metaChild, this);
		this.addChild(metaChild);
	    }
	    sourceCon.createStatement().close();
	    subscribeSet.close();
	    // if (sourceCon != null) {
	    // sourceCon.close();
	    // }
	} catch (Exception e) {
	    MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
	}
    }

    public static SubscribeDataInfo setDataSourceMetaInfo(SubscribeDataInfo metaChild, SubscribeDataBase meta) {
	metaChild.setDbName(meta.getDbName());
	metaChild.setDbServer(meta.getDbServer());
	metaChild.setDbPort(meta.getDbPort());
	metaChild.setDbUser(meta.getDbUser());
	metaChild.setDbPasswrod(meta.getDbPasswrod());
	metaChild.setDriverName(meta.getDriverName());
	metaChild.setDriverPath(meta.getDriverPath());
	return metaChild;
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

    public String getNodeId() {
	return nodeId;
    }

    public void setNodeId(String nodeId) {
	this.nodeId = nodeId;
    }

}
