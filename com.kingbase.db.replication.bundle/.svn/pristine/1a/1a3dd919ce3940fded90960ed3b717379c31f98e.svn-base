package com.kingbase.db.replication.bundle.graphical.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.pentaho.di.graphical.editpart.NodeLabelEditPart;
import org.pentaho.di.graphical.editpart.ProcessContainerPart;
import org.pentaho.di.graphical.model.AbstractActivityNode;

import com.kingbase.db.replication.bundle.graphical.editor.ConnectLineEditorPart;
import com.kingbase.db.replication.bundle.graphical.editor.ReplicationEditorPart;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationAllProcess;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationConnection;
import com.kingbase.db.replication.bundle.graphical.model.ReplicationProcess;

/**
 * @author jpliu
 *  工厂，将model与editor连接起来
 */
public class ReplicationEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart arg0, Object model) {

		EditPart part = null;
		if ((model instanceof ReplicationProcess) || (model instanceof ReplicationAllProcess)) {
			part = new ProcessContainerPart();
		} else if (model instanceof ReplicationConnection) {
			part = new ConnectLineEditorPart();
		} else if (model instanceof AbstractActivityNode) {
			part = new ReplicationEditorPart();
		} else {
			part = new NodeLabelEditPart();
		}

		part.setModel(model);
		return part;
	}

}
