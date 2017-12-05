/**
 * 
 */
package com.kingbase.db.console.bundle.model.tree;

import org.eclipse.swt.graphics.Image;
import org.pentaho.di.model.IBasicModel;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.core.util.ImageURL;

/**
 * @author jpliu
 *
 */
public class InstanceInstallEntity extends CTableTreeNode implements ITreeProvider {

	public InstanceInstallEntity() {

	}

	public Image getImage(Object arg0) {
		return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.folder);
	}

	public String getText(Object arg0) {
		return "数据加载";
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
}
