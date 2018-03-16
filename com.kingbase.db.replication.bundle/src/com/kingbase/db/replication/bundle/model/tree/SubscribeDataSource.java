package com.kingbase.db.replication.bundle.model.tree;

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
import java.util.Map;
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
import com.kingbase.db.replication.bundle.dialog.ConnectionPasswordDialog;
import com.kingbase.db.replication.bundle.util.DatabaseUtil;

/**
 * @author Duke
 *
 */
public class SubscribeDataSource extends CTableTreeNode implements ITreeProvider {

    private String dbName;// 服务器名称
    private String dbServer;
    private String dbPort;
    private String dbUser;
    private String dbPasswrod;
    private String driverPath;
    private String driverName;

    private String subscribeName;// 订阅名称
    private String subscribeEnable;// 订阅名称
    private boolean isSaveP = true;
    public static final Image image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_servers);

    public boolean isSaveP() {
	return isSaveP;
    }

    public void setSaveP(boolean isSaveP) {
	this.isSaveP = isSaveP;
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

    public String getName() {
	return dbName;
    }

    public String getDbName() {
	return dbName;
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

    @Override
    public Image getImage(Object element) {

	return image;
    }

    @Override
    public String getText(Object element) {
	return dbName; // 服务器名
    }

    private boolean hasInit = false;
    private List<SubscribeDataBase> databaseList = new ArrayList<SubscribeDataBase>();

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

    public void refresh() {
	setHasInit(false);
	removeAll();
	treeExpanded();
    }

    private void changeConnection() {

	Map<String, Connection> connectionMap = DatabaseUtil.getConnectionMap();
	String url = "jdbc:kingbase8://" + this.getDbServer() + ":" + this.getDbPort() + "/" + UIUtils.getDatabase();
	String userName = this.getDbUser();
	String password = this.getDbPasswrod();
	Connection connection = connectionMap.get(url + userName + password);
	if (connection != null) {
	    connectionMap.remove(url + userName + password);
	}
	for (SubscribeDataBase database : databaseList) {
	    url = "jdbc:kingbase8://" + this.getDbServer() + ":" + this.getDbPort() + "/" + database.getDatabaseName();
	    connection = connectionMap.get(url + userName + password);
	    if (connection != null) {
		connectionMap.remove(url + userName + password);
	    }
	}
	DatabaseUtil.setConnectionMap(connectionMap);
    }

    @Override
    public void treeExpanded() {
	if (isOpen()) {
	    return;
	}
	if (!isSaveP()) {
	    ConnectionPasswordDialog dialog = new ConnectionPasswordDialog(UIUtils.getActiveShell(), null, this);
	    SubscribeDataSource open = (SubscribeDataSource) dialog.open();
	    if (open == null) {
		setHasInit(true);
		return;
	    }
	    this.dbUser = open.getDbUser();
	    this.dbPasswrod = open.getDbPasswrod();
	    this.isSaveP = open.isSaveP();
	    savePassword(open);
	}
	Connection sourceCon = DatabaseUtil.getConnection(this, UIUtils.getDatabase());
	if (sourceCon == null) {
	    return;
	}
	getDatabaseInfo(sourceCon);// 得到所有的数据库
	changeConnection();// 刷新时，将数据库缓存删除

	ReplicationFile ifile = (ReplicationFile) this.getParentModel();
	File file = ifile.getFile();
	SAXReader reader = new SAXReader();
	Document document = null;
	try {
	    document = reader.read(file);
	} catch (DocumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	for (SubscribeDataBase database : databaseList) {
	    createNode(document, file, database);
	    this.addChild(database);
	}
	setHasInit(true);
    }

    public void getDatabaseInfo(Connection sourceCon) {
	databaseList.clear();
	if (sourceCon != null) {
	    try {
		DatabaseMetaData metaData = sourceCon.getMetaData();
		String schemaTerm = metaData.getSchemaTerm();
		if (!schemaTerm.equals("")) {
		    Statement stm1 = sourceCon.createStatement();
		    ResultSet rs0 = null;
		    rs0 = stm1.executeQuery("SELECT DATNAME,OID FROM SYS_DATABASE WHERE NOT DATISTEMPLATE AND DATALLOWCONN ORDER BY DATNAME");
		    while (rs0.next()) {
			SubscribeDataBase metaChild = new SubscribeDataBase();
			String datename = rs0.getString("DATNAME"); //$NON-NLS-1$
			String oid = rs0.getString("OID"); //$NON-NLS-1$
			metaChild.setDatabaseOid(oid);
			metaChild.setDatabaseName(datename);
			setDataSourceMetaInfo(metaChild, this);
			databaseList.add(metaChild);
		    }
		    stm1.close();
		    if (rs0 != null) {
			rs0.close();
		    }
		}
		// if (sourceCon != null) {
		// sourceCon.close();
		// }
	    } catch (SQLException e) {
		e.printStackTrace();
		MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
	    }
	}
    }

    /**
     * 创建xml节点:每个数据库都重写一遍服务器的连接数据;
     */
    private void createNode(Document document, File file, SubscribeDataBase database) {

	Element root1 = document.getRootElement();
	List<Element> listEle = root1.elements();

	Connection sourceCon = DatabaseUtil.getConnection(this, database.getDatabaseName());
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
		database.setExistTable(false);
	    }
	}
    }

    private void savePassword(SubscribeDataSource source) {

	ReplicationFile ifile = (ReplicationFile) this.getParentModel();
	File file = ifile.getFile();
	File fileLocal = file;
	SAXReader reader = new SAXReader();
	List<Element> listEle = null;
	Document document;
	try {
	    document = reader.read(file);
	    Element root = document.getRootElement();
	    listEle = root.elements();
	    List<ReleaseDataBase> list = new ArrayList<ReleaseDataBase>();
	    for (int i = 0, n = listEle.size(); i < n; i++) {
		Element element = listEle.get(i);
		if (element.element("name").getStringValue().equals(this.getDbName())) {
		    element.element("username").setText(source.getDbUser());
		    element.element("password").setText(source.getDbPasswrod());
		    element.element("isSaveP").setText(source.isSaveP() ? "true" : "false");
		}
	    }
	    OutputFormat xmlFormat = UIUtils.xmlFormat();
	    XMLWriter output = new XMLWriter(new FileWriter(fileLocal), xmlFormat);
	    output.write(document);
	    output.close();

	} catch (DocumentException e) {
	    e.printStackTrace();
	    MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
	} catch (IOException e) {
	    e.printStackTrace();
	    MessageDialog.openError(UIUtils.getActiveShell(), "Error", e.getMessage());
	}
    }

    public static SubscribeDataBase setDataSourceMetaInfo(SubscribeDataBase metaChild, SubscribeDataSource meta) {
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
