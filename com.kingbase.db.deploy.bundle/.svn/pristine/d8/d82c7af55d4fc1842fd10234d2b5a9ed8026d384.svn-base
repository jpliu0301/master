package com.kingbase.db.deploy.bundle.graphical.figure;

import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.swt.graphics.Image;
import org.pentaho.di.graphical.figure.FigureWithAnchor;

import com.kingbase.db.deploy.bundle.graphical.model.DeployContentsMasterModel;

public class DeployFigure extends FigureWithAnchor implements MouseMotionListener {


	public DeployFigure(String text, boolean isPool, boolean isMain, String status) {

		if (isPool && isMain) {
			init(DeployContentsMasterModel.subscriber_enable, DeployContentsMasterModel.subscriber_disable, status);
		} else if (isPool && !isMain) {
			init(DeployContentsMasterModel.subscriber_enable, DeployContentsMasterModel.subscriber_disable, status);
		} else if (!isPool && isMain) {
			init(DeployContentsMasterModel.database_enable, DeployContentsMasterModel.database_disable, status);
		} else {
			init(DeployContentsMasterModel.database_enable, DeployContentsMasterModel.database_disable, status);
		}
		addMouseMotionListener(this);
		setLayoutManager(new ToolbarLayout());
		setToolTip(new Label(text));
		setBorder(new MarginBorder(2));
	}

	private void init(Image imageEnable, Image imageDisable, String status) {
		Image image = null;
		if (status != null && status.equals(DeployContentsMasterModel.up)) {
			image = imageEnable;
		} else {
			image = imageDisable;
		}
		ImageFigure iconFigure = new ImageFigure(image);
		add(iconFigure);
		setOpaque(false);
	}

	@Override
	public String getText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasBreakpoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseHover(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
