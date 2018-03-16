/**
 * 
 */
package com.kingbase.db.replication.bundle.model.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.model.IBasicModel;
import org.pentaho.di.viewer.CTableTreeNode;
import org.pentaho.di.viewer.ITreeProvider;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.i18n.messages.Messages;
import com.kingbase.db.replication.bundle.views.ReplicationView;

/**
 * @author jpliu
 *
 */
public class ReplicationFile extends CTableTreeNode implements ITreeProvider {

	private boolean isRelease = false;
	private File file = null;
	public static final Image image = ImageURL.createImage(KBReplicationCore.PLUGIN_ID, ImageURL.folder);

	public ReplicationFile(File file, boolean isRelease) {
		this.file = file;
		this.isRelease = isRelease;
	}

	@Override
	public Image getImage(Object arg0) {
		return image;
	}

	@Override
	public String getText(Object arg0) {
		if (this.isRelease) {
			return Messages.ReplicationFolder_release;
		} else {
			return Messages.ReplicationFolder_subscribe;
		}
	}

	public boolean isRelease() {
		return isRelease;
	}

	public void setRelease(boolean isRelease) {
		this.isRelease = isRelease;
	}

	public File getFile() {
		return file;
	}
	private boolean hasInit = false;

	public boolean isOpen() {
		return hasInit;
	}

	public boolean hasChildren() {
		boolean hasChildren = super.hasChildren();
		if (!hasInit)
			return true;
		return hasChildren;
	}

	public void setHasInit(boolean hasInit) {
		this.hasInit = hasInit;
	}

	public void treeExpanded() {
		if (isOpen()) {
			return;
		}
		try {
			if (isRelease) {
				if (file != null && file.exists()) {
					getReleaseSource(file);
				}
			} else {
				if (file != null && file.exists()) {
					getSubscribeSource(file);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		setHasInit(true);
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

	private void getReleaseSource(File file) throws DocumentException {
	        List<ReleaseDataSource> list = new ArrayList<ReleaseDataSource>();
		List<Element> listEle = ReplicationView.getlistEle(this);
		for (int i = 0, n = listEle.size(); i < n; i++) {
			ReleaseDataSource sourceMeta = new ReleaseDataSource();
			Element element = listEle.get(i);
			
			sourceMeta.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$
			sourceMeta.setSaveP(element.element("isSaveP").getStringValue().equals("true") ? true : false); //$NON-NLS-1$ //$NON-NLS-2$

			list.add(sourceMeta);
		}
		Collections.sort(list, new Comparator<ReleaseDataSource>() {

			@Override
			public int compare(ReleaseDataSource o1, ReleaseDataSource o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (ReleaseDataSource node : list) {
			this.addChild(node);
		}

	}
	private void getSubscribeSource(File file) throws DocumentException {
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		List<Element> listEle = root.elements();
		List<SubscribeDataSource> list = new ArrayList<SubscribeDataSource>();
		for (int i = 0, n = listEle.size(); i < n; i++) {
			SubscribeDataSource sourceMeta = new SubscribeDataSource();
			Element element = listEle.get(i);
			if (element.element("nodeId") != null){ //$NON-NLS-1$
				continue;
			}
			sourceMeta.setDbName(element.element("name").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbServer(element.element("server").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbPort(element.element("port").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbUser(element.element("username").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDbPasswrod(element.element("password").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDriverName(element.element("driverName").getStringValue()); //$NON-NLS-1$
			sourceMeta.setDriverPath(element.element("driverPath").getStringValue()); //$NON-NLS-1$
			sourceMeta.setSaveP(element.element("isSaveP").getStringValue().equals("true")?true:false); //$NON-NLS-1$ //$NON-NLS-2$
			
			list.add(sourceMeta);
		}
		Collections.sort(list, new Comparator<SubscribeDataSource>() {

			@Override
			public int compare(SubscribeDataSource o1, SubscribeDataSource o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (SubscribeDataSource node : list) {
			this.addChild(node);
		}
		
	}
	
	public static ReleaseDataSource setDataSourceMetaInfo(ReleaseDataSource metaChild, ReleaseDataSource meta) {
		metaChild.setDbName(meta.getDbName());
		metaChild.setDbServer(meta.getDbServer());
		metaChild.setDbPort(meta.getDbPort());
		metaChild.setDbUser(meta.getDbUser());
		metaChild.setDbPasswrod(meta.getDbPasswrod());
		metaChild.setDriverName(meta.getDriverName());
		metaChild.setDriverPath(meta.getDriverPath());
		return metaChild;
	}
	public static SubscribeDataSource setDataSourceMetaInfo(SubscribeDataSource metaChild, SubscribeDataSource meta) {
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
