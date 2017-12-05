package com.kingbase.db.console.bundle.model.tree;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.model.IBasicModel;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.util.ImageURL;

public class SchemaEntity extends CTableTreeNode implements ITreeProvider {

	private String name;
	private long oid;

	private String database;
	private ExportOrImportEntity node;
	private String type;

	public SchemaEntity(ExportOrImportEntity node, String type) {
		this.node = node;
		this.type = type;
	}

	public Image getImage(Object arg0) {
		return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.tree_database_enable);
	}

	public String getText(Object arg0) {
		return name;
	}

	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub
		super.setDesc(desc);
	}

	private boolean hasInit = false;

	public boolean isOpen() {
		return hasInit;
	}

	public boolean hasChildren() {
		if (type.equals("schema")) {
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
		if(type.equals("table")){
			
			getTable();
		}
		setHasInit(true);
	}

	private void getTable() {
		Connection source = ConsoleView.getConnection(node.getAddress(), node.getPort(), node.getUser(),
				node.getPassword(), this.getDatabase());
		if (source != null) {
			try {
				Statement stmSchema = source.createStatement();// 查找模式
				ResultSet rsSchema = null;

				rsSchema = stmSchema.executeQuery("select oid, relname from sys_class where relnamespace = "
						+ this.getOid() + "and relkind = 'r'");
				while (rsSchema.next()) {
					TableEntity table = new TableEntity();
					String tableName = rsSchema.getString("relname");
					long tableOid = rsSchema.getLong("OID");
					table.setOid(tableOid);
					table.setName(tableName);
					table.setDatabase(this.getDatabase());
					table.setSchema(this.getName());
					this.addChild(table);// 将表添加进去
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

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

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public long getOid() {
		return oid;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}
}
