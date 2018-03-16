/**
 * 
 */
package com.kingbase.db.replication.bundle.graphical.editor;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;

import com.kingbase.db.replication.bundle.graphical.model.ReplicationConnection;
import com.kingbase.db.replication.bundle.views.ReplicationView;

/**
 * @author jpliu
 *
 */
public class ConnectLineEditorPart extends AbstractConnectionEditPart{
	protected IFigure createFigure() {
		ReplicationConnection model = (ReplicationConnection) getModel();

		PolylineConnection connection = new PolylineConnection();
		connection.setLineWidth(2);
		if (model.getDataInfo() != null && model.getDataInfo().getSubscribeEnable().equals("t")) {

			connection.setToolTip(new Label("订阅启用"));
			connection.setForegroundColor(ReplicationView.lightGreen);
		} else if (model.getDataInfo() != null && model.getDataInfo().getSubscribeEnable().equals("f")) {

			connection.setToolTip(new Label("订阅禁用"));
			connection.setForegroundColor(ReplicationView.red);
		}

		ConnectionLayer connectionLayer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
		connectionLayer.setConnectionRouter(null);
		connectionLayer.setAntialias(SWT.ON);

		connection.setTargetDecoration(new PolygonDecoration());
		connection.setConnectionRouter(new BendpointConnectionRouter());
		return connection;
	}
	@Override
	protected void createEditPolicies() {
	}
	
}
