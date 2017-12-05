package com.kingbase.db.console.bundle.model.tree;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.model.IBasicModel;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.core.util.ImageURL;

public class TableEntity  extends CTableTreeNode implements ITreeProvider {

	private String name;
	private long oid;
	private String database;
	private String schema;;
	public TableEntity() {
	}

	public Image getImage(Object arg0) {
		return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.tree_table);
	}

	public String getText(Object arg0) {
		return name;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	private boolean hasInit = false;

	public boolean isOpen() {
		return hasInit;
	}

	public boolean hasChildren() {
		return false;
	}

	public void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public void treeExpanded() {

		setHasInit(false);
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

	public long getOid() {
		return oid;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public void setOid(long oid) {
		this.oid = oid;
	}
}
