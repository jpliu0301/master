package com.kingbase.db.replication.bundle.model.tree;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;

public class ReleaseTable extends CTableTreeNode implements ITreeProvider {

	private String tableName;
	private String tableOid;
	private String schemaName;
	private String txtContent;
	public static final Image image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.tree_table);
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getTableOid() {
		return tableOid;
	}
	public void setTableOid(String tableOid) {
		this.tableOid = tableOid;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getTxtContent() {
		return txtContent;
	}
	public void setTxtContent(String txtContent) {
		this.txtContent = txtContent;
	}
	@Override
	public Image getImage(Object arg0) {
 
		return image;
	}
	@Override
	public String getText(Object arg0) {
		return tableName == null ? schemaName : tableName;
	}
	
	
}
