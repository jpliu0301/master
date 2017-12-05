package com.kingbase.db.console.bundle.model.tree;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.model.IBasicModel;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;

public class DatabaseEntity extends CTableTreeNode implements ITreeProvider {

	private String name;
	private long oid;
	private ExportOrImportEntity node;
	private String type;

	public DatabaseEntity(ExportOrImportEntity node, String type) {
		this.node = node;
		this.type = type;
	}

	public Image getImage(Object arg0) {
		return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.replication);
	}

	public String getText(Object arg0) {
		return name;
	}

	private boolean hasInit = false;

	public boolean isOpen() {
		return hasInit;
	}

	public boolean hasChildren() {
		if (type.equals("database")) {
			return false;
		} else {
			return true;
		}
	}

	public void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public void treeExpanded() {
		if (isOpen()) {
			return;
		}
		if (!type.equals("database")) {
			getSchema();
		}
		setHasInit(true);
	}
	public void getSchema() {
		Connection connection = ConsoleView.getConnection(node.getAddress(), node.getPort(), node.getUser(),
				node.getPassword(), this.getName());
		if (connection != null) {
			try {
				DatabaseMetaData metaData = connection.getMetaData();
				String schemaTerm = metaData.getSchemaTerm();
				if (!schemaTerm.equals("")) {
					Statement stmDatabase = connection.createStatement();// 查找数据库
					ResultSet rsDatabase = null;
					rsDatabase = stmDatabase.executeQuery(
							"select oid ,nspname from  sys_namespace ss where ss.nspname NOT LIKE 'SYS_%'");
					while (rsDatabase.next()) {
						SchemaEntity schema = new SchemaEntity(node, type);
						String schemaName = rsDatabase.getString("nspname");
						long oid = rsDatabase.getLong("OID");
						schema.setOid(oid);
						schema.setName(schemaName);
						schema.setDatabase(this.getName());

						if (!(schemaName.toLowerCase().startsWith("information")||schemaName.toLowerCase().startsWith("outln")||schemaName.toLowerCase().startsWith("utl_file"))) {
							this.addChild(schema);
						}
					}
					stmDatabase.close();
					if (rsDatabase != null) {
						rsDatabase.close();
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				MessageDialog.openError(UIUtils.getActiveShell(), "Error", e1.getMessage());
			}
		}
	};

	@Override
	public IBasicModel[] getChildren() {
		return super.getChildren();
	}

	public void refresh() {
		setHasInit(false);
		removeAll();
		treeExpanded();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}
}
