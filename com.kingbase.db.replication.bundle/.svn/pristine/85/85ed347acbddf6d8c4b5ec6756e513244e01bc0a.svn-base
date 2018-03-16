/**
 * 
 */
package com.kingbase.db.replication.bundle.graphical.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.graphical.editpart.AbstractEditPart;
import org.pentaho.di.graphical.model.AbstractActivityNode;

import com.kingbase.db.core.util.ImageURL;
import com.kingbase.db.replication.bundle.KBReplicationCore;
import com.kingbase.db.replication.bundle.graphical.figure.ReplicationFigure;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationSourceNode;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationTargetNode;
import com.kingbase.db.replication.bundle.model.tree.ReleaseDataInfo;
import com.kingbase.db.replication.bundle.model.tree.SubscribeDataInfo;
import com.kingbase.db.replication.bundle.views.ReplicationView;

/**
 * @author jpliu
 *
 */
public class ReplicationEditorPart extends AbstractEditPart implements PropertyChangeListener {
	protected IFigure createFigure() {
		AbstractActivityNode model = (AbstractActivityNode) getModel();
		if (model instanceof ReplicationSourceNode) {
			ReplicationSourceNode targetNode = (ReplicationSourceNode) model;
			ReleaseDataInfo dataInfo = targetNode.getDataInfo();
			String tooltip = getReleaseTooltip(dataInfo);
			ReplicationFigure wfigure = new ReplicationFigure(tooltip, ReplicationView.publish,targetNode.getInput(),dataInfo);
			return wfigure;
		} else if (model instanceof ReplicationTargetNode) {

			ReplicationTargetNode sourceNode = (ReplicationTargetNode) model;
			SubscribeDataInfo dataInfo = sourceNode.getDataInfo();
			Image image = null;
			if (dataInfo.getSubscribeEnable().equals("t")) { //$NON-NLS-1$
				image = ReplicationView.subscriber_enable;
			} else {
				image = ReplicationView.subscriber_disable;
			}
			String tooltip = getSubscribeTooltip(dataInfo);
			ReplicationFigure wfigure = new ReplicationFigure(tooltip, image,sourceNode.getInput(),dataInfo);
			return wfigure;
		}
		return null;
	}

	public void activate() {
		if (isActive()) {
			return;
		}
		super.activate();
		((AbstractActivityNode) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if(evt.getPropertyName().equals("name")){
			refreshVisuals();
		}
	}

	private String getSubscribeTooltip(SubscribeDataInfo dataInfo) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" 服务器： ");
		buffer.append(dataInfo.getDbName() + " \n");
		buffer.append(" 数据库： ");
		buffer.append(dataInfo.getDatabaseName() + "\n");
		buffer.append(" host： ");
		buffer.append(dataInfo.getDbServer() + "\n");
		buffer.append(" port： ");
		buffer.append(dataInfo.getDbPort() + "\n");
		buffer.append(" user： ");
		buffer.append(dataInfo.getDbUser() + "\n");
		buffer.append(" 订阅名称： ");
		buffer.append(dataInfo.getSubscribeName());

		return buffer.toString();
	}

	private String getReleaseTooltip(ReleaseDataInfo dataInfo) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" 服务器： ");
		buffer.append(dataInfo.getDbName() + " \n");
		buffer.append(" 数据库： ");
		buffer.append(dataInfo.getDatabaseName() + "\n");
		buffer.append(" host： ");
		buffer.append(dataInfo.getDbServer() + "\n");
		buffer.append(" port： ");
		buffer.append(dataInfo.getDbPort() + "\n");
		buffer.append(" user： ");
		buffer.append(dataInfo.getDbUser() + "\n");
		buffer.append(" 发布名称： ");
		buffer.append(dataInfo.getReleaseName());

		return buffer.toString();
	}

	@Override
	protected void createEditPolicies() {
	}
}
