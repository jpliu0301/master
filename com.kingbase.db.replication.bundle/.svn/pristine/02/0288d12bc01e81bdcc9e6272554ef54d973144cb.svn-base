/**
 * 
 */
package com.kingbase.db.replication.bundle.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;

import com.kingbase.db.core.util.PlatformUtil;
import com.kingbase.db.core.util.UIUtils;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataSource;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataSource;

/**
 * @author jpliu
 *
 */
public class DatabaseUtil {

	private static Map<String, Connection> connectionMap = new HashMap<String, Connection>();
	private static Map<String, File> fileMap = new HashMap<String, File>();

	/**
	 * 发布服务器获取连接
	 * 
	 */
	public static Connection getConnection(ReleaseDataSource sourceMeta, String database) {
		if(sourceMeta.getDriverPath()==null){
			return null;
		}
		String sqlUrl = "jdbc:kingbase8://" + sourceMeta.getDbServer() + ":" + sourceMeta.getDbPort() + "/" + database;
		String driverName = sourceMeta.getDriverName();
		String userName = sourceMeta.getDbUser();
		String password = sourceMeta.getDbPasswrod();
		
		//去掉template2连接
	        String urlTem = "jdbc:kingbase8://" + sourceMeta.getDbServer() + ":" + sourceMeta.getDbPort() + "/" + UIUtils.getDatabase();
	        Connection con = connectionMap.get(urlTem + userName + password);
	        if (con != null) {
	            connectionMap.remove(urlTem + userName + password);
	        }
		
		if (connectionMap.get(sqlUrl + userName + password) != null) {
			Connection connection = connectionMap.get(sqlUrl + userName + password);

			boolean isClosed = false;
			try {
				isClosed = connection.isClosed();
			} catch (Exception e) {
				isClosed = true;
			}

			if (isClosed) {
				connectionMap.remove(sqlUrl + userName + password);
			} else {
				return connection;
			}
		}
		File file = fileMap.get(sourceMeta.getDriverPath());
		if(file == null){
			file = PlatformUtil.getConfigurationFile(KBReplicationCore.PLUGIN_ID, sourceMeta.getDriverPath());
			fileMap.put(sourceMeta.getDriverPath(), file);
		}
		Connection connection = null;
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			Class driverClassName = loader != null ? loader.loadClass(driverName) : Class.forName(driverName);
			if (driverClassName != null) {
				Driver driver = (Driver) driverClassName.newInstance();
				DriverManager.registerDriver(driver);
				if (driver.acceptsURL(sqlUrl)) {
					Properties props = new Properties();
					props.put("user", userName); //$NON-NLS-1$
					props.put("password", password); //$NON-NLS-1$
					connection = driver.connect(sqlUrl, props);
				}
			}
			connectionMap.put(sqlUrl + userName + password, connection);
		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e1) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
					Messages.CreateSourceConnectionDialog_Server_is_not_connected + "\n\n" + e1.getMessage());
			return null;
		}
		return connection;
	}

	/**
	 * 订阅服务器获取连接
	 * 
	 */
	public static Connection getConnection(SubscribeDataSource sourceMeta, String database) {
		if(sourceMeta.getDriverPath()==null){
			return null;
		}
		String sqlUrl = "jdbc:kingbase8://" + sourceMeta.getDbServer() + ":" + sourceMeta.getDbPort() + "/" + database;
		String driverName = sourceMeta.getDriverName();
		String userName = sourceMeta.getDbUser();
		String password = sourceMeta.getDbPasswrod();
		//去掉template2连接
		String urlTem = "jdbc:kingbase8://"+sourceMeta.getDbServer() + ":" + sourceMeta.getDbPort() + "/" +  UIUtils.getDatabase();
		Connection con = connectionMap.get(urlTem + userName + password);
		if (con != null) {
		    connectionMap.remove(urlTem + userName + password);
		}
		
		if (connectionMap.get(sqlUrl + userName + password) != null) {
			Connection connection = connectionMap.get(sqlUrl + userName + password);

			boolean isClosed = false;
			try {
				isClosed = connection.isClosed();
			} catch (Exception e) {
				isClosed = true;
			}

			if (isClosed) {
				connectionMap.remove(sqlUrl + userName + password);
			} else {
				return connection;
			}
		}
		File file = fileMap.get(sourceMeta.getDriverPath());
		if(file == null){
			file = PlatformUtil.getConfigurationFile(KBReplicationCore.PLUGIN_ID, sourceMeta.getDriverPath());
			fileMap.put(sourceMeta.getDriverPath(), file);
		}
		Connection connection = null;
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
			Class driverClassName = loader != null ? loader.loadClass(driverName) : Class.forName(driverName);
			if (driverClassName != null) {
				Driver driver = (Driver) driverClassName.newInstance();
				DriverManager.registerDriver(driver);
				if (driver.acceptsURL(sqlUrl)) {
					Properties props = new Properties();
					props.put("user", userName); //$NON-NLS-1$
					props.put("password", password); //$NON-NLS-1$
					connection = driver.connect(sqlUrl, props);
				}
			}
			connectionMap.put(sqlUrl + userName + password, connection);
		} catch (Exception e1) {
			MessageDialog.openError(UIUtils.getActiveShell(), "错误", //$NON-NLS-1$
					Messages.CreateSourceConnectionDialog_Server_is_not_connected + "\n\n" + e1.getMessage());
			return null;
		}
		return connection;
	}

	public static Map<String, Connection> getConnectionMap() {
		return connectionMap;
	}

	public static void setConnectionMap(Map<String, Connection> connectionMap) {
		DatabaseUtil.connectionMap = connectionMap;
	}
	
}
