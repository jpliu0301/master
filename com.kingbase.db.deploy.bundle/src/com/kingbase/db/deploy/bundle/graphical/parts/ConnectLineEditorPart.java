/**
 * 
 */
package com.kingbase.db.deploy.bundle.graphical.parts;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.swt.SWT;

import com.kingbase.db.deploy.bundle.graphical.model.DeployConnection;
import com.kingbase.db.deploy.bundle.graphical.model.DeployContentsMasterModel;

/**
 * @author jpliu
 *
 */
public class ConnectLineEditorPart extends AbstractConnectionEditPart{
	protected IFigure createFigure() {
		DeployConnection model = (DeployConnection) getModel();

		PolylineConnection connection = new PolylineConnection();
		connection.setLineWidth(2);
		if (model.getStatus() != null && model.getStatus().equals(DeployContentsMasterModel.up)) {

			connection.setForegroundColor(DeployContentsMasterModel.lightGreen);
			connection.setToolTip(new Label(DeployContentsMasterModel.up)); 
		} else if (model.getStatus() != null && model.getStatus().equals(DeployContentsMasterModel.down)) {

			connection.setForegroundColor(DeployContentsMasterModel.red);
			connection.setToolTip(new Label(DeployContentsMasterModel.down));
		}else{
			connection.setForegroundColor(DeployContentsMasterModel.black);
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
