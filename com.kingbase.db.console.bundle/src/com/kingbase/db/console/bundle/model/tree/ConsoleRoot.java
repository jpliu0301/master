/**
 * 
 */
package com.kingbase.db.console.bundle.model.tree;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.console.bundle.KBConsoleCore;
import com.kingbase.db.console.bundle.views.ConsoleView;
import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.core.util.UIUtils;

/**
 * @author Duke
 *
 */
public class ConsoleRoot extends CTableTreeNode implements ITreeProvider {

	private boolean hasInit = false;

	public ConsoleRoot() {
	}

	

	protected boolean isOpen() {
		return hasInit;
	}

	public boolean hasChildren() {
		if (!hasInit)
			return true;
		return super.hasChildren();
	}

	protected void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public void refresh() {
		setHasInit(false);
		removeAll();
		treeExpanded();
	}

	public void treeExpanded() {
		if (isOpen()) {
			return;
		}

		this.addChild(new InstanceInstallEntity());
		this.addChild(new InstanceUnInstallEntity());
		this.addChild(new ServiceStatusEntity());
		this.addChild(new ExportOrImportEntity());
		
		setHasInit(true);
	}



	public Image getImage(Object arg0) {
		return ImageURL.createImage(KBConsoleCore.PLUGIN_ID, ImageURL.replication);
	}

	public String getText(Object arg0) {
		return "KB控制台";
	}
}
