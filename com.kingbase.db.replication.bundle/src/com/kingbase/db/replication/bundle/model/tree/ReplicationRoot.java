/**
 * 
 */
package com.kingbase.db.replication.bundle.model.tree;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;

/**
 * @author Duke
 *
 */
public class ReplicationRoot extends CTableTreeNode implements ITreeProvider {

	private boolean hasInit = false;

	private IProject project = null;
	public static final Image image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.replication);

	public ReplicationRoot(IProject project) {
		this.project = project;
	}

	@Override
	public Image getImage(Object arg0) {
		return image;
	}

	@Override
	public String getText(Object arg0) {
		return Messages.ReplicationRoot_Data_sysc;
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
		try {
			IResource[] resources = project.members();
			for (int i = 0; i < resources.length; i++) {
				IResource res = (IResource) resources[i];
				if (res instanceof IFile) {
					IFile ifile = (IFile) res;
					File file = ifile.getLocation().toFile();
					if (file.getName().equals("release.xml")) { //$NON-NLS-1$
						ReplicationFile conFile = new ReplicationFile(file, true);
						this.addChild(conFile);
					} else if(file.getName().equals("subscribe.xml")){
						ReplicationFile traFile = new ReplicationFile(file, false);
						this.addChild(traFile);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		setHasInit(true);
	}
}
